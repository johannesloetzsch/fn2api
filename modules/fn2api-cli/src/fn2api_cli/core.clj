(ns fn2api-cli.core
  (:require [fn2api-core.core :refer [fn->fn]]
            [fn2api-core.signatures :refer [fn->signature-map]]
            [fn2api-core.signatures-spec :refer [spec->st-spec+id fn->specs_strict fn->specs_fallback]]  ;; TODO strict
            [fn2api-format.core :refer [decode]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [clojure.tools.cli :refer [parse-opts]]))

(defn spec->cli-options [specs]
  (map (fn [speced]
           (let [spec (spec->st-spec+id speced)
                 id (str (:fn2api/id spec (if-let [sn (st/spec-name spec)]
                                                  (clojure.string/replace sn #".*/" ""))))
                 _ (assert id)
                 argument (:fn2api/argument spec (if-not (:fn2api/assoc-fn spec)
                                                         ;; TODO only when `assoc-fn` is ignoring the value returned by `parse-fn`
                                                         (clojure.string/upper-case id)))]
                [(if-let [short-opt (:fn2api/short-opt spec)] (str "-" short-opt))
                (str "--" (:fn2api/long-opt spec id)
                     (if argument (str " " argument)))
                (:fn2api/switch-description spec (:description spec))
                :id id
                :default (:fn2api/default spec)
                :parse-fn (if argument
                              #(try (decode % :format "text/plain" :spec spec)
                                    (catch clojure.lang.ExceptionInfo e ::s/invalid)))
                :validate (if argument
                              [#(s/valid? spec %) (:reason spec)])
                :assoc-fn (if-let [assoc-fn (:fn2api/assoc-fn spec)]
                                  assoc-fn
                                  (fn [m k v] (assoc-in m [k] v)))]))
       specs))


(s/def ::help (st/spec {:spec boolean?
                        :id 'help
                        :description "Show help"
                        :fn2api/short-opt "h"
                        :fn2api/default false
                        :fn2api/assoc-fn (fn [m k _] (assoc-in m [k] true))}))
(s/def ::verbosity (st/spec {:spec pos-int?
                             :id 'verbosity
                             :description "Verbosity level"
                             :fn2api/switch-description "Increase verbosity (can be applied multiple times)"
                             :fn2api/short-opt "v"
                             :fn2api/long-opt "verbose"
                             :fn2api/default 0
                             :fn2api/assoc-fn (fn [m k _] (update-in m [k] inc))}))

(defn fn->cli [fn:var opts &{:keys [argv-keyword]}]
  (let [specs (concat [::help ::verbosity] (fn->specs_fallback fn:var))
        cli-options (spec->cli-options specs)
        parsed (parse-opts opts cli-options)
        argv (:arguments parsed)
        kwargs (clojure.walk/keywordize-keys (:options parsed))]
       (if (or (:errors parsed)
               (:help kwargs))
           (do (if (> (:verbosity kwargs) 0)
                   (println "argv:" argv "kwargs:" kwargs))
               (if (:errors parsed)
                   (println (clojure.string/join "\n" (:errors parsed))))
               (println "Usage:")
               (println (:summary parsed)))
           (let [argv-keyword||default (or argv-keyword
                                           (if-let [argv-name (:argv (fn->signature-map fn:var))]
                                                   (keyword argv-name)))
                 arguments:map (merge kwargs (if argv-keyword {argv-keyword||default argv}))]
                ((fn->fn fn:var) arguments:map)))))
