(ns fields.core
  (:require [clojure.zip :as z]))

(defn- tokenize
  "Partitions a string by ), (, or ,. Characters stay together
  foo,bar(hu,ra) => [foo , bar ( hu , ra )]"
  [s]
  (clojure.string/split s (re-pattern (str "(?=\\))|(?<=\\))|(?=\\()|(?<=\\()|(?=,)|(?<=,)"))))

(defn- strip-surrounding-parens
  "Example: (abc) -> abc, abc -> abc"
  [s]
  (if (= \( (first s))
    (apply str (-> s drop-last rest))
    s))

(defn- lexer [query]
  "(a, b(c,d), e) -> [a b ( c d ) e]"
  (-> query
      (clojure.string/replace " " "")
      (strip-surrounding-parens)
      (tokenize)
      ((partial filterv #(not (= "," %))))))

(defprotocol PParser
  (advanceToken [this] "")
  (thisToken [this] "return this token"))

(deftype Parser [tokens state]
  PParser
  (advanceToken [this]
    (if (= (:readPosition @state) (count tokens))
        false
        (do
          (swap! state assoc :position (@state :readPosition))
          (swap! state update :readPosition inc)
          true)))
  (thisToken [this] (nth tokens (:position @state))))

(defn newParser [tokens]
  (Parser. tokens (atom {:position 0 :readPosition 0})))

(def my-zipper (z/zipper (constantly true)
                         (fn [node] (:children node))
                         (fn [node, children] {:children children :value (node :value)})
                         {:children [] :value nil}))

(defn- parse
  "Parses a query into a zipper/tree."
  [query]
  (let [p (newParser (lexer query))]
    (advanceToken p)
    (let [zi (-> my-zipper
                 (z/insert-child {:children [] :value (thisToken p)})
                 (z/down))]
      (advanceToken p)
      (loop [loc zi]
        (let [nextLoc (case (thisToken p)
                        ")" (z/up loc)
                        "(" (do
                              (advanceToken p)
                              (-> loc
                                  (z/insert-child {:children [] :value (thisToken p)})
                                  (z/down)))
                        (-> loc
                            (z/insert-right {:children [] :value (thisToken p)})
                            (z/right)))]
          (if (advanceToken p) (recur nextLoc) (z/up nextLoc)))))))

(defn- auto
  [v x] (with-meta x ((if v dissoc assoc) (meta x) :zip-filter/no-auto? true)))

(defn- right-locs
  "Returns a lazy sequence of locations to the right of loc, starting with loc."
  [loc] (lazy-seq (when loc (cons (auto false loc) (right-locs (z/right loc))))))

(defn- child-locs
  [loc]
  (right-locs (z/down loc)))

(defn- walk
  [m, loc]
  (let [anchor (-> loc z/node :value keyword)]
    (if (empty? (z/children loc))
      (select-keys m [anchor]) ;; => leaf
      (let [children (child-locs loc)]
        (if (vector? (anchor m))
          {anchor (map (fn [o] (apply merge (map (partial walk o) children))) (anchor m))}
          {anchor ((fn [o] (apply merge (map (partial walk o) children)))(anchor m))})))))
  
(defn select-keys-by-fields [m query]
  (if (nil? query)
    m
    (do
      (let [loc (parse query)
            children (right-locs (z/down loc))]
        (apply merge (map (partial walk m) children))))))

