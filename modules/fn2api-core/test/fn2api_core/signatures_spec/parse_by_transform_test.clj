(ns fn2api-core.signatures-spec.parse-by-transform-test
  (:require [clojure.test :refer :all]
            [fn2api-core.signatures-spec.parse-by-transform :refer [fn->specs]]
            [fn2api-core.core-test :refer [fn-trivial fn-with-variadic-argv
                                           fn-with-map+argv fn-with-explicit-map fn-with-inline-kwargs]]
            [fn2api-core.signatures-spec-test]
            [clojure.spec.alpha :as s]))


(deftest test_fn->specs
  (testing "fn-trivial"
    (is (= (s/conform :fn2api-core.signatures-spec.parse-by-transform/spec-trivial
                      (s/form (:args (s/get-spec (var fn-trivial)))))
           {:cat 'clojure.spec.alpha/cat
            :args [{:name :arg1 :spec [:kw :fn2api-core.signatures-spec-test/mykw]}
                   {:name :arg2 :spec [:kw :fn2api-core.signatures-spec-test/mystkw]}]}))

    (is (= (fn->specs (var fn-trivial))
           {:args [:fn2api-core.signatures-spec-test/mykw :fn2api-core.signatures-spec-test/mystkw]})))

  (testing "fn-with-variadic-argv"
    (is (= (s/conform :fn2api-core.signatures-spec.parse-by-transform/spec-with-variadic-argv
                      (s/form (:args (s/get-spec (var fn-with-variadic-argv)))))
           {:cat 'clojure.spec.alpha/cat
            :args [{:name :arg1 :spec [:kw :fn2api-core.signatures-spec-test/mykw]}
                   {:name :arg2 :spec [:kw :fn2api-core.signatures-spec-test/mystkw]}]
            :argv {:name :variadic :spec 'clojure.core/sequential?}}))

    (is (= (fn->specs (var fn-with-variadic-argv))
           {:args [:fn2api-core.signatures-spec-test/mykw :fn2api-core.signatures-spec-test/mystkw]
            :argv 'clojure.core/sequential?})))

  (testing "fn-with-map+argv"
    (is (= (s/conform :fn2api-core.signatures-spec.parse-by-transform/spec-with-map+argv
                      (s/form (:args (s/get-spec (var fn-with-map+argv)))))
           {:cat 'clojure.spec.alpha/cat
            :args [{:name :arg1 :spec [:kw :fn2api-core.signatures-spec-test/mykw]}
                   {:name :arg2 :spec [:kw :fn2api-core.signatures-spec-test/mystkw]}]
            :kwargs {:name :keywords :spec [:kw :fn2api-core.signatures-spec-test/kwargs]}
            :argv {:name :variadic :spec 'clojure.core/sequential?}}))

    (is (= (fn->specs (var fn-with-map+argv))
           {:args [:fn2api-core.signatures-spec-test/mykw :fn2api-core.signatures-spec-test/mystkw]
            :kwargs :fn2api-core.signatures-spec-test/kwargs
            :argv 'clojure.core/sequential?})))

  (testing "fn-with-explicit-map"
    (is (= (s/conform :fn2api-core.signatures-spec.parse-by-transform/spec-with-explicit-map
                      (s/form (:args (s/get-spec (var fn-with-explicit-map)))))
           {:cat 'clojure.spec.alpha/cat
            :args [{:name :arg1 :spec [:kw :fn2api-core.signatures-spec-test/mykw]}
                   {:name :arg2 :spec [:kw :fn2api-core.signatures-spec-test/mystkw]}]
            :kwargs {:name :keywords :spec [:kw :fn2api-core.signatures-spec-test/kwargs]}}))

    (is (= (fn->specs (var fn-with-explicit-map))
           {:args [:fn2api-core.signatures-spec-test/mykw :fn2api-core.signatures-spec-test/mystkw]
            :kwargs :fn2api-core.signatures-spec-test/kwargs}))))


    ;; TODO fn-with-inline-kwargs

    #_(s/describe :fn2api-core.signatures-spec.parse-by-transform/spec-with-explicit-map)

    #_(s/conform :fn2api-core.signatures-spec.parse-by-transform/spec-with-explicit-map
                 (s/form (:args (s/get-spec (var fn-with-explicit-map)))))
