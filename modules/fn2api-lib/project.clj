(defproject fn2api-lib "version-is-inherited"
  :plugins [[lein-parent "0.3.5"]]
  :parent-project {:path "../../project.clj"
                   :inherit [:version :description :url :scm :license
                             :plugins :dependencies :managed-dependencies]
                   :only-deps [org.clojure/clojure]}
  :dependencies [[yogthos/config]
                 [mount]
                 [com.taoensso/encore]
                 [com.taoensso/timbre]]
  :profiles {:dev {:resource-paths ["config/dev"]}
             :test {:resource-paths ["config/test"]}})
