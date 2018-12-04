(ns fn2api-cli.core-test
  (:require [clojure.test :refer :all]
            [fn2api-cli.core :refer [spec->cli-options fn->cli]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [clojure.tools.cli :refer [parse-opts]]))

(s/def ::port (st/spec {:spec int? #_(s/and int? #(< 0 % 0x10000))
                        :id 'port
                        :description "Port Number"
                        :reason "Must be a number between 0 and 65536"
                        :fn2api/short-opt "p"
                        :fn2api/default 80}))

(deftest quickstart  ;; like https://github.com/clojure/tools.cli#quick-start
  (let [cli-options (spec->cli-options [::port :fn2api-cli.core/verbosity :fn2api-cli.core/help])]
       (testing "summary"
         (is (= (:summary (parse-opts [] cli-options))
                "  -p, --port PORT  80  Port Number\n  -v, --verbose        Increase verbosity (can be applied multiple times)\n  -h, --help           Show help")))
       (testing "parse-opts"
         (is (= (dissoc (parse-opts [] cli-options) :summary)
                {:options {"port" 80, "verbosity" 0, "help" false}, :arguments [], :errors nil}))
         (is (= (dissoc (parse-opts ["-p22" "--verbose" "-vv" "--help"] cli-options) :summary)
                {:options {"port" 22, "verbosity" 3, "help" true}, :arguments [], :errors nil})))))

;(s/fdef port-example
;  :args (s/cat :p ::port :variadic #_any? sequential?))  ;; TODO any? is not working
;
;(defn port-example [port & ignored]
;  (println "Start imaginary server at port" port))
;
;(defn -main [& args]
;  (fn->cli (var port-example) args))
;
;(require '[fn2api-core.signatures :refer [fn->type fn->signature-map]])
;(require '[fn2api-core.core :refer [fn->fn]])
;(require '[fn2api-cli.core :refer [spec->cli-options]])
;(require '[fn2api-core.signatures-spec :refer [fn->specs_fallback]])
;(fn->signature-map (var port-example))
;(fn->type (var port-example))
;((fn->fn (var port-example)) {:port 3})
;
;(require '[fn2api-core.signatures-spec.parse-by-transform :refer [fn->specs]])
;(fn->specs (var port-example))
;
;(let [specs (concat [:fn2api-cli.core/help :fn2api-cli.core/verbosity] (fn->specs_fallback (var port-example)))
;      _ (println specs)
;      cli-options (spec->cli-options specs)]
;     (println cli-options)
;     (parse-opts ["-p" "3" "other" "args"] cli-options))
;(fn->cli (var port-example) ["-p" "3" "other" "args"])
