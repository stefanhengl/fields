(ns fields.core-test
  (:require [clojure.test :refer :all]
            [fields.core :refer :all]))

(def data {:a 1
	   :b [{:aa 2 :bb 3} {:aa 4 :bb 5}]
	   :c {:cc 6 :dd {:aaa 7 :bbb 8}}
           :d [{:ee [{:ccc [{:aaaa 1 :bbbb 2} {:aaaa 3 :bbbb 4}]}]}]})

(def test-table
  [[{:a 1} "(a)"]
   [{:a 1, :b [{:aa 2} {:aa 4}]} "(a,b(aa))"]
   [{:b [{:aa 2, :bb 3} {:aa 4, :bb 5}], :c {:cc 6, :dd {:bbb 8}}}  "(b,c(cc,dd(bbb)))"]
   [{:a 1 :d [{:ee [{:ccc [{:bbbb 2} {:bbbb 4}]}]}]}  "(a,d(ee(ccc(bbbb))))"]])

(deftest query-by-fields
  (doseq [[expected query] test-table]
    (is (= expected (select-keys-by-fields data query)))))

(deftest nil-query
  (is (= data (select-keys-by-fields data nil))))

(deftest empty-query
  (is (= {} (select-keys-by-fields data "()")))
  (is (= {} (select-keys-by-fields data ""))))

(defn helper-parse-and-select
  [map query]
  (->> query
      parse
      (select-keys-by-zipper map)))

(deftest query-by-zippers
  (doseq [[expected query] test-table]
    (is (= expected (helper-parse-and-select data query)))))

(deftest meomize-parsing
  (def parse-memo (memoize parse))
  (is (= (parse-memo "(a,b(aa))") (parse "(a,b(aa))")))
  (is (= (parse-memo "(a,b(aa))") (parse "(a,b(aa))"))))
