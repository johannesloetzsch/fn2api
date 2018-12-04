(ns fn2api-core.signatures
  (:require [fn2api-core.helper.defn-args]
            [clojure.core.specs.alpha]
            [clojure.spec.alpha :as s]
            [fn2api-core.helper.misc :refer [only]]))

(defn fn->signature-map
  "Get a map containing the symbols for the different kind of arguments:
   * `args` vector of simple required arguments
   * `argv` symbol to a vector of variadic arguments
   * `maps` destructured map arguments
   * `mapv` a variadic vector of destructured map arguments
   * `ikws` destructured inline keyword arguments"
  [fn:var]
  (let [arglist (-> fn:var meta :arglists last)  ;; TODO till now we assume that the last param-list contains the complete signature
        conformed_official (s/conform :clojure.core.specs.alpha/param-list arglist)  ;; this spec is not able to s/unform correctly
        conformed (s/conform :fn2api-core.helper.defn-args/arg-list arglist)]
       (assert (= conformed conformed_official)
               [conformed conformed_official])
       {:args (map (fn [params] (->> params
                                     (apply hash-map)
                                     :local-symbol))
                   (:params conformed))
        :argv (->> (get-in conformed [:var-params :var-form])
                   (apply hash-map)
                   :local-symbol)
        :kwargs {:maps (map (fn [params] (->> params
                                              (apply hash-map)
                                              :map-destructure))
                            (:params conformed))
                 :mapv (->> (get-in conformed [:var-params :var-form])
                            (apply hash-map)
                            :seq-destructure :forms
                            (map (fn [params] (->> params
                                              (apply hash-map)
                                              :map-destructure))))
                 :ikws (->> (get-in conformed [:var-params :var-form])
                            (apply hash-map)
                            :map-destructure)}}))

(defn fn->type [fn:var]
  (let [sm (fn->signature-map fn:var)
        kwargs (:kwargs sm)
        maps (remove nil? (:maps kwargs))
        types {:trivial (and (not (:argv sm)) (empty? maps) (empty? (:mapv kwargs)) (not (:ikws kwargs)))
               :with-variadic-argv (and (:argv sm) (empty? maps) (empty? (:mapv kwargs)) (not (:ikws kwargs)))
               :with-map+argv (not (empty? maps))
               :with-explicit-map (not (empty? (:mapv kwargs)))
               :with-inline-kwargs (boolean (:ikws kwargs))}]
        (->> types
             (filter val) 
             (into {}) keys
             (#(only % {:error types})))))

(defn fn->signatures
  "merge result from fn->signature-map"
  [fn:var]
  (let [signature-map (fn->signature-map fn:var)
        flatten-kwargs (fn [maps] (apply concat (map #(cons (:as %) (:keys %)) maps)))]
       {:args (vec (remove nil? (:args signature-map)))
        :argv (:argv signature-map)
        :kwargs (->> (concat (flatten-kwargs (get-in signature-map [:kwargs :maps]))
                             (flatten-kwargs (get-in signature-map [:kwargs :mapv]))
                             (flatten-kwargs [(get-in signature-map [:kwargs :ikws])]))
                     (remove nil?)
                     (into #{}))}))
