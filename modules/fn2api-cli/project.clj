(defproject fn2api-cli "version-is-inherited"
  :plugins [[lein-parent "0.3.5"]]
  :parent-project {:path "../../project.clj"
                   :inherit [:version :description :url :license
                             :plugins :dependencies :managed-dependencies]
                   :only-deps [org.clojure/clojure]}
  :dependencies [[fn2api-core]
                 [fn2api-format]

                 [org.clojure/tools.cli]])
