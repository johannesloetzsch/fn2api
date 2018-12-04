(ns fn2api-format.core
  (:refer-clojure :exclude [format])
  (:require [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [muuntaja.core :as m]
            [muuntaja.format.yaml]
            [fn2api-format.format-plain]))

(def ^{:doc "my munjana instance
(adds support for additional formats)"}
  mm (m/create (-> m/default-options
                   (assoc-in [:formats "application/x-yaml"] muuntaja.format.yaml/format)
                   (assoc-in [:formats "text/plain"] fn2api-format.format-plain/format) )))

(def format->transformer {"application/edn" st/json-transformer
                          "application/json" st/json-transformer
                          "application/x-yaml" st/json-transformer
                          "text/plain" st/string-transformer})

(def default-format "application/edn")

(defn decode [formatted &{:keys [format spec]}]
  (let [formatted:str (cond (string? formatted) formatted
                            (map? formatted) (:data formatted))
        _ (assert (string? formatted:str))
        format||default (cond format format
                              (:format formatted) (:format formatted)
                              :else default-format)

        data (m/decode mm format||default formatted:str)]

       (if spec
           (let [transformer (format->transformer format||default)]
                (assert transformer, (str "No transformer found for " format||default))
                (st/decode spec data transformer))
           data)))

(defn encode [edn &{:keys [spec format slurp? intoMap?]
                    :or {format "application/edn"
                         slurp? true}}]
  (let [spec||default (or spec (s/get-spec edn))
        _ (assert spec||default #_(or (s/spec? spec||default) (fn? spec||default))
                  (str "Needs be a spec: " (s/describe spec||default)))
        edn:data (if (var? edn)
                     (var-get edn)
                     edn)
        transformer (format->transformer format)

        x (st/encode spec||default edn:data transformer)
        formatted (m/encode mm format x)]

       (if-not slurp?
               formatted
               (if intoMap?
                   {:data (slurp formatted) :format format}
                   (slurp formatted)))))
