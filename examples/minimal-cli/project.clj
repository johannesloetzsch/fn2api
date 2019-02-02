(defproject minimal-cli "0.2.0-SNAPSHOT"
  :description "An example commandline interface created with fn2api"
  :url "https://github.com/johannesloetzsch/fn2api/"
  :scm {:name "git" :url "https://github.com/johannesloetzsch/fn2api"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[fn2api-cli "0.2.0"]

                 [org.clojure/clojure "1.10.0"]
                 [metosin/spec-tools "0.8.2"]]
  :main ^:skip-aot minimal-cli
  :profiles {:provided {:aot :all}})  ;; for install + uberjar tasks
