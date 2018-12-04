(ns fn2api-core.helper.misc)

(defn xor-switch
  "Returns either a or b, whichever is true. Ensures that only is true."
  [a b]
  (let [a||nil (if-not (and (sequential? a) (empty? a)) a)]
       (assert (not (and a||nil b))
               (str "Only one of them should be true: " a " vs " b))
       (or a||nil b)))

(defn map-arg-symbols->values
  "Use `àrg-sumbol->value` to lookup the values for for each symbol of the destructured map `m`"
  [arg-symbol->value m]
  (if m (let [as (arg-symbol->value (:as m))
              ks (apply merge (map #(if-let [v (arg-symbol->value %)]
                                            (hash-map (keyword %) v))
                                   (:keys m)))]
              (if as as ks))))

(defn only [s {:keys [error]}]
  (assert (= 1 (count s))
          (or error s))
  (first s))
