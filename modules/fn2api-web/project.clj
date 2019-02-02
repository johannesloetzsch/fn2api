(defproject fn2api-web "version-is-inherited"
  :plugins [[lein-parent "0.3.5"]]
  :parent-project {:path "../../project.clj"
                   :inherit [:version :description :url :scm :license
                             :plugins :dependencies :managed-dependencies]
                   :only-deps [org.clojure/clojure]}
  :dependencies [[fn2api-core]
                 [fn2api-lib]

                 [metosin/reitit]
                 [ring/ring-jetty-adapter]
                 [aleph]
                 [com.unbounce/encors]]
  :main ^:skip-aot fn2api-web.core
  :profiles {:dev {:dependencies [[ring/ring-mock]
                                  [criterium]]}
             :provided {:aot :all}}  ;; for install + uberjar tasks
  :test-selectors {:default (complement :slow)})
