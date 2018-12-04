(ns example-test
  (:require [clojure.test :refer :all]
            [example :refer [plus]]))

(deftest test_plus

  (testing "plus"
    (is (= (plus {:x 1 :y 2}) {:total 3}))))
