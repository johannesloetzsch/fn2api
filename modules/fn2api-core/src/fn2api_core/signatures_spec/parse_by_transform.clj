(ns fn2api-core.signatures-spec.parse-by-transform
  (:require [fn2api-core.helper.defn-args]
            [fn2api-core.helper.spec :refer [transform]]
            [fn2api-core.signatures :refer [fn->type]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]))


;; spec for fspecs for transformation via conform+unform

(s/def ::spec (s/or :spec s/spec?
                    :kw keyword?  ;; TODO this should be namespaced
                    :fn (and symbol? #(fn? (var-get (resolve %))))
                    ;;TODO implement compound
                    :other any?  ;; for debugging purposes
                    ))
(s/def ::name+spec (s/cat :name keyword? :spec ::spec))
(s/def ::name+spec* (s/cat :spec ::spec))
(s/def ::argv #{'clojure.core/sequential?})

(s/def ::spec-trivial
  (s/cat :cat #{'clojure.spec.alpha/cat}
         :args (s/* ::name+spec)))
(s/def ::spec-trivial*
  (s/and (s/conformer #() #(zipmap [:args] %))
         (s/cat :args (s/and (s/conformer #() vec)
                             (s/* ::name+spec*)))))

(s/def ::spec-with-variadic-argv
  (s/cat :cat #{'clojure.spec.alpha/cat}
         :args (s/* ::name+spec)
         :argv (s/cat :name keyword? :spec ::argv)))
(s/def ::spec-with-variadic-argv*
  (s/and (s/conformer #() #(zipmap [:args :argv] %))
         (s/cat :args (s/and (s/conformer #() vec)
                             (s/* ::name+spec*))
                :argv (s/cat :spec ::argv))))

(s/def ::spec-with-map+argv
  (s/cat :cat #{'clojure.spec.alpha/cat}
         :args (s/* ::name+spec)
         :kwargs ::name+spec ;; TODO not necessarily at the last position
         :argv (s/cat :name keyword? :spec ::argv)))  ;; TODO should be optional
(s/def ::spec-with-map+argv*
  (s/and (s/conformer #() #(zipmap [:args :kwargs :argv] %))
         (s/cat :args (s/and (s/conformer #() vec)
                             (s/* ::name+spec*))
                :kwargs (s/and (s/conformer #() first)
                               ::name+spec*)
                :argv (s/cat :spec ::argv))))

(s/def ::spec-with-explicit-map
  (s/cat :cat #{'clojure.spec.alpha/cat}
         :args (s/* ::name+spec)
         :kwargs ::name+spec))
(s/def ::spec-with-explicit-map*
  (s/and (s/conformer #() #(zipmap [:args :kwargs] %))
         (s/cat :args (s/and (s/conformer #() vec)
                             (s/* ::name+spec*))
                :kwargs (s/and (s/conformer #() first)
                               ::name+spec*))))


(defn fn->specs
  "get specs of fn:var by tranformation via conform+unform"
  [fn:var]
  (let [type->s_conform+s_unform {:trivial [::spec-trivial ::spec-trivial*]
                                  :with-variadic-argv [::spec-with-variadic-argv ::spec-with-variadic-argv*]
                                  :with-map+argv [::spec-with-map+argv ::spec-with-map+argv*]
                                  :with-explicit-map [::spec-with-explicit-map ::spec-with-explicit-map*]}
        fn-type (fn->type fn:var)
        [s_conform s_unform] (get type->s_conform+s_unform fn-type)]
       (assert (and s_conform s_unform)
               (str "fn->spec not defined for " fn-type))
       (transform s_conform s_unform
                  (s/form (:args (s/get-spec fn:var))))))
