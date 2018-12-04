(ns fn2api-core.helper.defn-args
  (:require [clojure.spec.alpha :as s]))

;; See https://blog.klipse.tech/clojure/2016/10/10/defn-args.html
;; Loaded from gist: https://gist.github.com/viebel/ab64ed95820af42b366889a872dc28ac
;; With own modifications to match :clojure.core.specs.alpha/param-list

(s/def ::local-name (s/and simple-symbol? #(not= '& %)))

(s/def ::binding-form
  (s/or :local-symbol ::local-name
        :seq-destructure ::seq-binding-form
        :map-destructure ::map-binding-form))

;; sequential destructuring
(s/def ::seq-binding-form
  (s/and vector?
         (s/conformer identity vec)
         (s/cat :forms (s/* ::binding-form)
                :rest (s/? (s/cat :amp #{'&} :var-form ::binding-form))
                :as (s/? (s/cat :as #{:as} :sym ::local-name)))))

;; map destructuring
(s/def ::keys (s/coll-of ident? :kind vector?))
(s/def ::syms (s/coll-of symbol? :kind vector?))
(s/def ::strs (s/coll-of simple-symbol? :kind vector?))
(s/def ::or (s/map-of simple-symbol? any?))
(s/def ::as ::local-name)
(s/def ::map-special-binding
  (s/keys :opt-un [::as ::or ::keys ::syms ::strs]))

(s/def ::map-binding (s/tuple ::binding-form any?))

(s/def ::ns-keys
  (s/tuple
    (s/and qualified-keyword? #(-> % name #{"keys" "syms"}))
    (s/coll-of simple-symbol? :kind vector?)))

(s/def ::map-bindings
  (s/every (s/or :mb ::map-binding
                 :nsk ::ns-keys
                 :msb (s/tuple #{:as :or :keys :syms :strs} any?)) :into {}))

(s/def ::map-binding-form (s/merge ::map-bindings ::map-special-binding))

;; bindings
(s/def ::binding (s/cat :binding ::binding-form :init-expr any?))
(s/def ::bindings (s/and vector? (s/* ::binding)))

;; defn, defn-, fn
(defn arg-list-unformer [a]
  (vec 
    (if (and (coll? (last a)) (= '& (first (last a))))
      (concat (drop-last a) (last a))
      a)))

(s/def ::arg-list
  (s/and
    vector?
    (s/conformer identity arg-list-unformer)
    (s/cat :params (s/* ::binding-form)
           :var-params (s/? (s/cat :ampersand #{'&} :var-form ::binding-form)))))

(s/def ::args+body
  (s/cat :args ::arg-list
         :prepost (s/? map?)
         :body (s/* any?)))

(s/def ::defn-args
  (s/cat :name simple-symbol?
         :docstring (s/? string?)
         :meta (s/? map?)
         :bs (s/alt :arity-1 ::args+body
                    :arity-n (s/cat :bodies (s/+ (s/spec ::args+body))
                                    :attr (s/? map?)))))
