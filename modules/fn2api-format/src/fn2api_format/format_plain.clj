(ns fn2api-format.format-plain
  (:refer-clojure :exclude [format])
  (:require [muuntaja.format.core :as fc])
  (:import (java.io OutputStream)))

(defn plain-decoder [options]
  (let [options-args (mapcat identity options)]
    (reify
      fc/Decode
      (fc/decode [_ data _]
        (slurp data)))))

(defn plain-encoder [options]
  (let [options-args (mapcat identity options)]
    (reify
      fc/EncodeToBytes
      (fc/encode-to-bytes [_ data _]
        (.getBytes
          ^String (str data)))
      fc/EncodeToOutputStream
      (fc/encode-to-output-stream [_ data _]
        (fn [^OutputStream output-stream]
          (.write output-stream (.getBytes ^String (str data))))))))

(def ^{:doc "Can serialize `str` without quotation marks.
Usefull in combination with `st/string-transformer`.
Only works for literals; compounds are not supported."}
  format (fc/map->Format {:decoder [plain-decoder]
                          :encoder [plain-encoder]}))
