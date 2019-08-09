(ns fields.core-test
  (:require [clojure.test :refer :all]
            [fields.core :refer :all]))

(def data {:a 1
	   :b [{:aa 2 :bb 3} {:aa 4 :bb 5}]
	   :c {:cc 6 :dd {:aaa 7 :bbb 8}}
           :d [{:ee [{:ccc [{:aaaa 1 :bbbb 2} {:aaaa 3 :bbbb 4}]}]}]})

(deftest queries
  (is (= {:a 1} (select-keys-by-fields data "(a)")))
  (is (= {:a 1, :b [{:aa 2} {:aa 4}]} (select-keys-by-fields data "(a,b(aa))")))
  (is (= {:b [{:aa 2, :bb 3} {:aa 4, :bb 5}], :c {:cc 6, :dd {:bbb 8}}} (select-keys-by-fields data "(b,c(cc,dd(bbb)))") ))
  (is (= {:a 1 :d [{:ee [{:ccc [{:bbbb 2} {:bbbb 4}]}]}]} (select-keys-by-fields data "(a,d(ee(ccc(bbbb))))"))))

(deftest nil-query
  (is (= data (select-keys-by-fields data nil))))

(deftest empty-query
  (is (= {} (select-keys-by-fields data "()")))
  (is (= {} (select-keys-by-fields data ""))))
