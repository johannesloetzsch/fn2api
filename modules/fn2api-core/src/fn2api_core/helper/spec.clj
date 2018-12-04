(ns fn2api-core.helper.spec
  (:require [clojure.spec.alpha :as s]))

(defn conform+explain [spec x]
  {:valid (s/valid? spec x)
   :conform (s/conform spec x)
   :explain (s/explain-data spec x)})

(defn unform+valid [spec y]
  (let [x (s/unform spec y)]
       (if-not (s/valid? spec x)
               {:valid false
                :unform x
                :explain (s/explain-data spec x)}
               (let [y* (s/conform spec x)]
                    (assert (= y y*)
                            {:valid true
                             ;:correct false
                             :unform x
                             :conform y*})
                    {:valid true
                     ;:correct true
                     :unform x}))))

(defn transform
  "tree transformation by conforming+unforming with different specs"
  [s_conform s_unform data]
  (let [conformed (s/conform s_conform data)
        _ (assert (not (s/invalid? conformed))
                  (str "Transformation not possible: " (s/explain s_conform data)))
        reformed (s/unform s_conform conformed)]
       (assert (s/valid? s_conform reformed)
               (s/explain-data s_conform reformed))
       (s/unform s_unform conformed)))



;(require '[spec-tools.visitor :as visitor])
;(defn walk [x]
;  (let [specs (atom {})]
;       (visitor/visit x (fn [_ spec _ _]
;                            (if-let [s (s/get-spec spec)]
;                                    (swap! specs assoc spec (s/describe (:spec s)) #_(s/describe s))
;                                    @specs)))))
;
;(let [port-example-args (:args (s/get-spec (var port-example)))]
;     (println (s/describe port-example-args))
;     (clojure.pprint/pprint (walk port-example-args))
;     (clojure.pprint/pprint (conform||explain port-example-args [{:port 42}])))
