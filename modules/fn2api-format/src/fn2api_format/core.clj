(ns fn2api-format.core
  (:refer-clojure :exclude [format])
  (:require [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [muuntaja.core :as m]
            [muuntaja.format.yaml]
            [fn2api-format.format-plain])
  (:import [java.net URL]
           [java.io InputStream]))

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

(defn decode [in &{:keys [format spec]}]
  (let [in:data (cond (map? in) (:data in)
                      :else in)
        in:str (if (or (instance? URL in:data)
                       (instance? InputStream in:data))
                   (slurp in:data)
                   in:data)
        _ (assert (string? in:str))
        format||default (cond format format
                              (:format in) (:format in)
                               :else default-format)  ;; TODO for URL: try to get default-format from extension

        data (m/decode mm format||default in:str)]

       (if spec
           (let [transformer (format->transformer format||default)
                 _ (assert transformer, (str "No transformer found for " format||default))
                 result (st/decode spec data transformer)]
                (if (s/invalid? result)
                    (throw (ex-info "Invalid Spec" (-> (s/explain-data spec data)
                                                       (select-keys [::s/problems]))))
                    result))
           data)))

(defn encode [in &{:keys [spec format slurp? intoMap?]
                   :or {format "application/edn"
                        slurp? true}}]
  (let [spec||default (or spec (s/get-spec in))
        _ (assert spec||default #_(or (s/spec? spec||default) (fn? spec||default))
                  (str "Needs be a spec: " (s/describe spec||default)))
        in:data (if (var? in)
                    (var-get in)
                    in)
        transformer (format->transformer format)

        x (st/encode spec||default in:data transformer)
        formatted (m/encode mm format x)]

       (if-not slurp?
               formatted
               (if intoMap?
                   {:data (slurp formatted) :format format}
                   (slurp formatted)))))
