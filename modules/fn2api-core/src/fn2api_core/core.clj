(ns fn2api-core.core
  (:require [fn2api-core.signatures :refer [fn->signature-map]]
            [fn2api-core.helper.misc :refer [map-arg-symbols->values xor-switch]]))

(defn fn->fn
  "Transform a function to take exactly one explicit map containing all arguments"
  [fn:var]
  (fn [arguments:map]
    (let [arg-symbol->value (fn [s] (get arguments:map (keyword s)))
          sm (fn->signature-map fn:var)
          args (map arg-symbol->value (:args sm))
          maps (map #(map-arg-symbols->values arg-symbol->value %) (get-in sm [:kwargs :maps]))
          _ (assert (= (count args) (count maps)))
          args+maps (mapv xor-switch args maps)
  
          inline_kws (map-arg-symbols->values arg-symbol->value (get-in sm [:kwargs :ikws]))
          inline_kws_vector (interleave (keys inline_kws) (vals inline_kws))
  
          variadic_mapv (map #(map-arg-symbols->values arg-symbol->value %) (get-in sm [:kwargs :mapv]))
          argv (arg-symbol->value (:argv sm))
          variadic (xor-switch variadic_mapv argv)]

         (apply (var-get fn:var) (concat args+maps inline_kws_vector variadic)))))
