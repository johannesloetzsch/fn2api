(ns fn2api.lib.spec
  (:require [clojure.spec.alpha :as s]))

(defn sub-specs
  "returns a seq of specs recursively used by the spec s"
  [s]
  (-> (cond
        (and (keyword? s) (s/get-spec s))
          (concat [s] (sub-specs (s/describe (s/get-spec s))))
        (sequential? s)
          (filter identity (map sub-specs s))
        (and (var? s) (s/get-spec s))
          (sub-specs (s/describe (s/get-spec s))))
      flatten))
