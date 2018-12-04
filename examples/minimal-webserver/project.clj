(defproject minimal-webserver "0.0.1-SNAPSHOT"
  :description "An example web interface created with fn2api"
  :url "https://github.com/johannesloetzsch/fn2api/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [metosin/spec-tools "0.8.2"]]
  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.2"]]}})
