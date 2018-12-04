(ns fn2api-core.signatures-spec-test
  (:require [clojure.test :refer :all]
            [fn2api-core.signatures-spec :refer [spec->type spec->st-spec+id fn->specs_strict fn->specs_fallback]]
            [fn2api-core.core-test :as core-test :refer [fn-trivial fn-with-variadic-argv
                                                         fn-with-map+argv
                                                         fn-with-explicit-map fn-with-inline-kwargs]]
            [fn2api-core.helper.spec :refer [conform+explain]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]))


;; specs for example functions using the different supported signatures (from fn2api-core.core-test)

(s/def ::mykw keyword?)
(s/def ::mystkw (st/spec keyword?))
(s/def ::kw1 string?)
(s/def ::kw2 string?)
(s/def ::kwargs (s/keys :req-un [::kw1 ::kw2]))

(s/fdef core-test/fn-trivial
  :args (s/cat :arg1 ::mykw
               :arg2 ::mystkw))

(s/fdef core-test/fn-with-variadic-argv
  :args (s/cat :arg1 ::mykw
               :arg2 ::mystkw
               :variadic sequential?))

(s/fdef core-test/fn-with-map+argv
  :args (s/cat :arg1 ::mykw
               :arg2 ::mystkw
               :keywords ::kwargs
               :variadic sequential?))

(s/fdef core-test/fn-with-explicit-map
  :args (s/cat :arg1 ::mykw
               :arg2 ::mystkw
               :keywords ::kwargs))

(s/fdef core-test/fn-with-inline-kwargs
  :args (s/cat :arg1 ::mykw
               :arg2 ::mystkw
               :keywords (s/* (s/cat :name keyword? :value string?))))


(deftest specs_valid
  (is (s/valid? (:args (s/get-spec (var fn-trivial)))
                [:a1 :a2]))
  (is (s/valid? (:args (s/get-spec (var fn-with-variadic-argv)))
                [:a1 :a2 [:v1 :v2]]))
  (is (s/valid? (:args (s/get-spec (var fn-with-map+argv)))
                [:a1 :a2 {:kw1 "kw1" :kw2 "kw2"} [:v1 :v2]]))
  (is (s/valid? (:args (s/get-spec (var fn-with-explicit-map)))
                [:a1 :a2 {:kw1 "kw1" :kw2 "kw2"}]))
  (is (s/valid? (:args (s/get-spec (var fn-with-inline-kwargs)))
                [:a1 :a2 :kw1 "kw1" :kw2 "kw2"])))



(defn equal_st-spec+id?
  "helper function to compare :st/spec+id"
  [s1 s2 &[{:keys [debug] :or {debug false}}]]
  (if debug
    (println (s/describe (:spec s1)) "\n"
             (s/describe (:spec s2))))
  (and (= (:fn2api/id s1) (:fn2api/id s2))
       (= (s/describe (:spec s1))
          (s/describe (:spec s2)))))


(deftest test_spec->st-spec+id
  (is (= :registered_st/spec
         (spec->type ::mystkw)))
  (is (equal_st-spec+id? (spec->st-spec+id ::mystkw)
                         (assoc (st/get-spec ::mystkw) :fn2api/id 'mystkw)))

  (is (= :registered_s/spec
         (spec->type ::mykw)))
  (is (equal_st-spec+id? (spec->st-spec+id ::mykw)
                         (st/spec {:spec (st/get-spec ::mykw) :fn2api/id 'mykw})))

  (is (= :map1entry  ;; of :fn
         (spec->type {:i int?})))
  (is (equal_st-spec+id? (spec->st-spec+id {:i int?})
                         (st/spec {:spec int? :fn2api/id 'i})))

  (is (= :map1entry  ;; of :registered_s/spec
         (spec->type {:i ::mykw})))
  (is (equal_st-spec+id? (spec->st-spec+id {:i ::mykw})
                         (st/spec {:spec ::mykw :fn2api/id 'i})))

  (is (= :map1entry  ;; of :registered_st/spec
         (spec->type {:i ::mystkw})))
  (is (equal_st-spec+id? (spec->st-spec+id {:i ::mystkw})
                         (assoc (st/get-spec ::mystkw) :fn2api/id 'i))))


(defn equal_list-of_st-spec+id?
  "helper function to compare a list of :st/spec+id"
  [s1 s2 &[{:keys [debug?]}]]
  (let [matching-sub-specs (map #(apply equal_st-spec+id? %)
                                (map vector (sort-by :fn2api/id s1)
                                            (sort-by :fn2api/id s2)))]
       (if debug? (println matching-sub-specs))
       (= (count s1) (count s2)
          (count (filter true? matching-sub-specs)))))


(deftest test_fn->specs_fallback

  (is (equal_list-of_st-spec+id?
        (fn->specs_fallback (var fn-trivial))
        [(spec->st-spec+id {:a1 ::mykw})
         (spec->st-spec+id {:a2 ::mystkw})]))

  (is (equal_list-of_st-spec+id?
        (fn->specs_fallback (var fn-with-variadic-argv)
                            {:specs_explicit {:a1 ::mykw}})
        [(spec->st-spec+id {:a1 ::mykw})             ;; explicit
         (spec->st-spec+id {:a2 ::mystkw})           ;; parsed
         (spec->st-spec+id {:argv sequential?})]))   ;; parsed

  (is (equal_list-of_st-spec+id?
        (fn->specs_fallback (var fn-with-map+argv))
        [(spec->st-spec+id {:a1 ::mykw})
         (spec->st-spec+id {:a2 ::mystkw})
         (spec->st-spec+id {:argv sequential?})
         (spec->st-spec+id {:kw1 ::kw1})
         (spec->st-spec+id {:kw2 ::kw2})
         (spec->st-spec+id {:kws ::kwargs})]))

  (is (equal_list-of_st-spec+id?
        (fn->specs_fallback (var fn-with-explicit-map))
        [(spec->st-spec+id {:a1 ::mykw})
         (spec->st-spec+id {:a2 ::mystkw})
         (spec->st-spec+id {:kw1 ::kw1})
         (spec->st-spec+id {:kw2 ::kw2})
         (spec->st-spec+id {:kws ::kwargs})]))

  ;; TODO

  #_(is (equal_list-of_st-spec+id?
        (fn->specs_fallback (var fn-with-inline-kwargs) {:debug? true})
        [(spec->st-spec+id {:a1 ::mykw})
         (spec->st-spec+id {:a2 ::mystkw})
         (spec->st-spec+id {:kw1 ::kw1})
         (spec->st-spec+id {:kw2 ::kw2})
         (spec->st-spec+id {:kws ::kwargs})])))
