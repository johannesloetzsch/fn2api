(ns fn2api-core.core-test
  (:require [clojure.test :refer :all]
            [fn2api-core.signatures :refer [fn->signature-map fn->signatures fn->type]]
            [fn2api-core.core :refer [fn->fn]]))

;; Some example functions using the different supported signatures

(defn fn-trivial
  [a1 a2]
  [a1 a2 nil nil nil nil])

(defn fn-with-variadic-argv
  [a1 a2 & argv]
  [a1 a2 nil nil nil argv])

(defn fn-with-map+argv
  ([a1 a2]
   (fn-with-map+argv a1 a2 nil))
  ([a1 a2 {:keys [kw1 kw2] :as kws} & argv]
   [a1 a2 kw1 kw2 kws argv]))

(defn fn-with-explicit-map
  [a1 a2 &[{:keys [kw1 kw2] :as kws}]]
  [a1 a2 kw1 kw2 kws nil])

(defn fn-with-inline-kwargs
  [a1 a2 &{:keys [kw1 kw2] :as kws}]
  [a1 a2 kw1 kw2 kws nil])


(deftest signature-maps
  (is (= (fn->signature-map (var fn-trivial))
         {:args ['a1 'a2] :argv nil :kwargs {:maps [nil nil] :mapv [] :ikws nil}}))
  (is (= (fn->signature-map (var fn-with-variadic-argv))
         {:args ['a1 'a2] :argv 'argv :kwargs {:maps [nil nil] :mapv [] :ikws nil}}))
  (is (= (fn->signature-map (var fn-with-explicit-map))
         {:args ['a1 'a2] :argv nil :kwargs {:maps [nil nil] :mapv [{:keys ['kw1 'kw2] :as 'kws}] :ikws nil}}))
  (is (= (fn->signature-map (var fn-with-inline-kwargs))
         {:args ['a1 'a2] :argv nil :kwargs {:maps [nil nil] :mapv [] :ikws {:keys ['kw1 'kw2] :as 'kws}}})))
  (is (= (fn->signature-map (var fn-with-map+argv))
         {:args ['a1 'a2 nil] :argv 'argv :kwargs {:maps [nil nil {:keys ['kw1 'kw2] :as 'kws}] :mapv [] :ikws nil}}))

(deftest types
  (is (= (fn->type (var fn-trivial))
         :trivial))
  (is (= (fn->type (var fn-with-variadic-argv))
         :with-variadic-argv))
  (is (= (fn->type (var fn-with-explicit-map))
         :with-explicit-map))
  (is (= (fn->type (var fn-with-inline-kwargs))
         :with-inline-kwargs))
  (is (= (fn->type (var fn-with-map+argv))
         :with-map+argv)))

(deftest signatures
  (is (= (fn->signatures (var fn-trivial))
         {:args ['a1 'a2] :argv nil :kwargs #{}}))
  (is (= (fn->signatures (var fn-with-variadic-argv))
         {:args ['a1 'a2] :argv 'argv :kwargs #{}}))
  (is (= (fn->signatures (var fn-with-explicit-map))
         (fn->signatures (var fn-with-inline-kwargs))
         {:args ['a1 'a2] :argv nil :kwargs #{'kw1 'kw2 'kws}}))
  (is (= (fn->signatures (var fn-with-map+argv))
         {:args ['a1 'a2] :argv 'argv :kwargs #{'kw1 'kw2 'kws}})))


;; All the arguments as a single map — used to call the functions wrapped by fn->fn
(def arguments:map {:a1 :a1 :a2 :a2 :kw1
                    "kw1" :kw2 "kw2" :kws {:kw1 "kw1" :kw2 "kw2"}  ;; kws is redundant
                    :argv [:v1 :v2]})

(deftest test_fn->fn

  (testing "only explicit args"
    (let [args (select-keys arguments:map [:a1 :a2])]
      (is (= [:a1 :a2 nil nil nil nil]
             (fn-trivial :a1 :a2)
             (fn-with-variadic-argv :a1 :a2)
             (fn-with-map+argv :a1 :a2)
             (fn-with-explicit-map :a1 :a2)
             (fn-with-inline-kwargs :a1 :a2)
             ((fn->fn (var fn-trivial)) args)
             ((fn->fn (var fn-with-variadic-argv)) args)
             ((fn->fn (var fn-with-map+argv)) args)
             ((fn->fn (var fn-with-explicit-map)) args)
             ((fn->fn (var fn-with-inline-kwargs)) args)))))

  (testing "explicit + variadic args"
    (let [args (select-keys arguments:map [:a1 :a2 :argv])]
      (is (= [:a1 :a2 nil nil nil [:v1 :v2]]
             (fn-with-variadic-argv :a1 :a2 :v1 :v2)
             (fn-with-map+argv :a1 :a2 nil :v1 :v2)
             ((fn->fn (var fn-with-variadic-argv)) args)
             ((fn->fn (var fn-with-map+argv)) args)))))

  (testing "explicit + keyword args"
    (let [args (select-keys arguments:map [:a1 :a2 :kw1 :kw2])]
      (is (= [:a1 :a2 "kw1" "kw2" {:kw1 "kw1" :kw2 "kw2"} nil]
             (fn-with-map+argv :a1 :a2 {:kw1 "kw1" :kw2 "kw2"})
             (fn-with-explicit-map :a1 :a2 {:kw1 "kw1" :kw2 "kw2"})
             (fn-with-inline-kwargs :a1 :a2 :kw1 "kw1" :kw2 "kw2")
             ((fn->fn (var fn-with-map+argv)) args)
             ((fn->fn (var fn-with-explicit-map)) args)
             ((fn->fn (var fn-with-inline-kwargs)) args)))))

  (testing "explicit + keywords/map + variadic args"
    (is (= [:a1 :a2 "kw1" "kw2" {:kw1 "kw1" :kw2 "kw2"} [:v1 :v2]]
           (fn-with-map+argv :a1 :a2 {:kw1 "kw1" :kw2 "kw2"} :v1 :v2)
           ((fn->fn (var fn-with-map+argv)) arguments:map)
           ((fn->fn (var fn-with-map+argv)) (dissoc arguments:map :kws))
           ((fn->fn (var fn-with-map+argv)) (dissoc arguments:map :kw1 :kw2))))))
