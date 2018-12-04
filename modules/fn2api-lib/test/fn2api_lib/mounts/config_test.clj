(ns fn2api-lib.mounts.config-test
  (:require [clojure.test :refer :all]
            [mount.core] 
            [fn2api-lib.mounts.config :refer [config-merge config]]
            [mount.core]))

(deftest test-config
  (testing "config depending on lein-profile"
    (mount.core/start)
    (is (= (:env config) :test)))

  (testing "use starts-with-args (nested merged)"
    (config-merge {:env :overwritten})
    (is (= (:env config) :overwritten))))
