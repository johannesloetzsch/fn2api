(defproject fn2api-core "version-is-inherited"
  :plugins [[lein-parent "0.3.5"]]
  :parent-project {:path "../../project.clj"
                   :inherit [:version :description :url :scm :license
                             :plugins :dependencies :managed-dependencies]
                   :only-deps [org.clojure/clojure]}
  :dependencies [[metosin/spec-tools]])
