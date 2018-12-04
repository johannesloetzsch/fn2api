(ns fn2api-web.mounts.router-env-test
  (:require [clojure.test :refer :all]
            [criterium.core :refer [with-progress-reporting quick-benchmark]]
            [mount.core]
            [fn2api-web.mounts.router :refer [router]]
            [fn2api-web.app.minimal :refer [router->app]]))

(defn test+bench
  "'f should be an function with arity 0. test_fn should return true when applied on (f)"
  [f test_fn]
  (let [results (with-progress-reporting (quick-benchmark (f) {}))
        t_s (first (:mean results))]
       (printf "Required time: %.1f ms\n" (* t_s 10e3))
       (doseq [result (:results results)]
              (assert (test_fn result) (str "Not true: (test_fn " result ")")))
       t_s))

(defn bench-mean-quotient [test_fn f1 f2]
  (/ (test+bench f1 test_fn) (test+bench f2 test_fn)))


(deftest ^:slow environments
  (mount.core/start)

  (testing "using the compiled productive router we should be at least 10 times faster than with the :dev router (for trivial functions)"
    (is (> (bench-mean-quotient #(= 404 (:status %))
                                #((router->app (:dev router)) {:request-method :get :uri "/"})
                                #((router->app (:prod router)) {:request-method :get :uri "/"}))
           10))))
