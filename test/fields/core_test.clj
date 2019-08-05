(ns fields.core-test
  (:require [clojure.test :refer :all]
            [fields.core :refer :all]))

(def test-map {:series [
                        {
                         :title "Chernobyl"
                         :genres ["Drama" "History" "Thriller"]
                         :rating "9.5"
                         :seasons [
                                   {
                                    :season 1
                                    :year 2019
                                    :episodes [
                                               {:title "1:23:45" :rating "9.5" :director {:first "Johan" :last "Renck"}}
                                               {:title "Please Remain Calm" :rating "9.7" :director {:first "Johan" :last "Renck"}}
                                               {:title "Open Wide, O Earth" :rating "9.6"  :director {:first "Johan" :last "Renck"}}
                                               ]
                                    }
                                   ]
                         }
                        ]
               :movies [
                        {:title "The Shawshank Redemption" :year 1994 :rating "9.3" :director {:first "Frank" :last "Darabont"}}
                        ]}
  )

(select-keys-by-fields test-map "(series(seasons(season)))")


(deftest deeplt-nested-query
  (let [query "(series(title,seasons(season,episodes(director))),movies(director))"
        result (select-keys-by-fields test-map query)
        expected {:series [
                           {
                            :title "Chernobyl"
                            :seasons [
                                   {:season 1
                                    :episodes [
                                               {:director {:first "Johan" :last "Renck"}}
                                               {:director {:first "Johan" :last "Renck"}}
                                               {:director {:first "Johan" :last "Renck"}}
                                               ]
                                    }]}]
               :movies [
                        {:director {:first "Frank" :last "Darabont"}}
                        ]
               }
        ]
    (is (= expected result))))
