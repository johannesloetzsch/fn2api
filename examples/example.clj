(ns example
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as sts]))

(s/def ::x sts/int?)
(s/def ::y sts/int?)
(s/def ::math-request (s/keys :req-un [::x ::y]))
(s/def ::total sts/int?)
(s/def ::math-response (s/keys :req-un [::total]))

(s/fdef plus
  :args (s/cat :kwargs ::math-request)
  :ret ::math-response)

(defn plus
  "this is just an simple example"
  [{:keys [x y]}]
  {:total (+ x y)})
