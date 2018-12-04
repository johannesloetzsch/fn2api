(ns fn2api-lib.mounts.logging-test
  (:require [clojure.test :refer :all]
            [fn2api-lib.mounts.config :refer [config-merge]]
            [fn2api-lib.mounts.logging :refer [logging]]
            [taoensso.timbre :as timbre]
            [clojure.string :refer [includes?]]))

(def logfile "out/test/fn2api.log")

(deftest config+level
  (clojure.java.io/delete-file logfile true)
  (config-merge {:fn2api-lib {:logging {:appenders {:spit {:enabled? true
                                                    :fname logfile}}}}})
  (let [{:keys [info trace]} logging]
       (is (= (:level timbre/*config*) :trace))  ;; set by timbre/set-level!
       (is timbre/-levels-map {:trace 1, :debug 2, :info 3, :warn 4, :error 5, :fatal 6, :report 7})
     
       (is (includes? (with-out-str (info "test info message"))
                      "test info message"))
       (is (includes? (with-out-str (trace "test trace message"))
                      "test trace message"))
       (timbre/with-level :debug
         (is (:level timbre/*config*) :debug)
         (is (= (with-out-str (trace "ignored by insufficient log-level")) "")))
     
       (is (= 2 (count (clojure.string/split-lines (slurp logfile)))))))
