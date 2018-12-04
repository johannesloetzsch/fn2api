(ns fn2api-core.signatures-spec
  (:require [fn2api-core.signatures :refer [fn->signatures]]
            [fn2api-core.signatures-spec.parse-by-transform :refer [fn->specs]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]))

(defn spec->type
  "classify the different types of supported specs"
  [spec]
  (cond (and (st/spec? spec)
             (symbol? (:fn2api/id spec))) ;; Name of the argument described by this spec
          :st/spec+id
        (and (st/spec? spec) (or (:spec spec) #_(:form spec)))
          :st/spec
        (and (s/get-spec spec) (or (:spec (s/get-spec spec) #_(:form (s/get-spec spec)))))
          :registered_st/spec
        (s/spec? spec)
          :s/spec
        (s/get-spec spec)
          :registered_s/spec
        (and (fn? spec) (not (nil? spec)))
          :fn
        (symbol? spec)
          :symbol
        (instance? clojure.lang.MapEntry spec)
          :MapEntry
        (and (map? spec) (= 1 (count spec)))
          :map1entry
        :else false))
        

(defn spec->st-spec+id
  "Translate any supported spec to a :st/spec+id.
   For details about the types see spec->type."
  [spec &[{:keys [id debug?]}]]
  (let [guess-spec-id #(if-let [id:might-contain-namespace (st/spec-name %)]
                               (clojure.string/replace id:might-contain-namespace #".*/" ""))
        id||default (if-let [id:any (or id (guess-spec-id spec))]
                            (symbol id:any))
        spec-type (spec->type spec)
        _ (if debug? (println spec-type))
        result (case spec-type
                     :st/spec+id
                     spec
                     :st/spec
                     (if (:fn2api/id spec)
                         spec
                         (assoc spec :fn2api/id id||default))
                     :registered_st/spec
                     (spec->st-spec+id (s/get-spec spec) {:id id||default :debug? debug?})
                     :s/spec
                     (st/spec {:fn2api/id id||default
                               :spec spec})
                     :registered_s/spec
                     (st/spec {:fn2api/id id||default
                               :spec spec})
                     :fn
                     (do (assert id) ;; for :fn we can't guess-spec-id
                         (st/spec {:fn2api/id (symbol id)
                                  :spec spec}))
                     :symbol
                     (spec->st-spec+id (var-get (resolve spec)) {:id id||default :debug? debug?})
                     :MapEntry
                     (spec->st-spec+id (val spec) {:id (key spec) :debug? debug?})
                     :map1entry
                     (spec->st-spec+id (first spec) {:debug? debug?})
                     (assert false (str "unknown spec-type: " spec-type)))]
       (assert (= (spec->type result) :st/spec+id))
       result))
                   

(defn fn->specs_strict
  "Ensure that a spec for every argument can be found by parsing the fspec"
  [var:fn]
  "TODO")

(defn fn->specs_fallback
  "Merge available specs for the arguments of a function from different sources:
   1. explicit argument (via optional argument)
   2. from parsing the fspec
   3. lookup of specs in s/registry with same name like argument  TODO
   4. create a fall back any? spec"
  [var:fn &[{:keys [specs_explicit debug?]}]]
  (let [signatures (fn->signatures var:fn)
        parsed-specs (fn->specs var:fn)

        explicit (map #(spec->st-spec+id % #_{:debug? debug?}) specs_explicit)
        parsed (->> (map hash-map (concat (:args signatures)
                                          (if-let [argv (:argv signatures)] [argv])
                                          (:kwargs signatures))
                                  (concat (:args parsed-specs)
                                          (if-let [argv (:argv parsed-specs)] [argv])
                                          (if-let [kwargs (:kwargs parsed-specs)]
                                            (->> kwargs
                                                 s/describe rest (apply hash-map) vals (apply concat)))  ;; :req-un & co
                                          [(:kwargs parsed-specs)]))  ;; :as
                    (map #(spec->st-spec+id % {:debug? debug?})))
        fallback (map #(spec->st-spec+id {% any?} #_{:debug? debug?}) (concat (:args signatures)
                                                                              (if-let [argv (:argv signatures)] [argv])
                                                                              (:kwargs signatures)))]
       (if debug? 
           (do (println signatures)
               (println parsed-specs)
               (println {#_#_:fallback fallback :parsed parsed :explicit explicit})))
       (->> [fallback parsed explicit]
            ((fn [list-of-specs] (map #(zipmap (map :fn2api/id %) %) list-of-specs)))
            (apply merge)
            vals)))
