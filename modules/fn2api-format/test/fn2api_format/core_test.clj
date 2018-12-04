(ns fn2api-format.core-test
  (:require [clojure.test :refer :all]
            [fn2api-format.core :refer [decode encode]]
            [clojure.spec.alpha :as s]))

(s/def ::example-str string?)
(s/def ::example-int int?)
(s/def ::example-float float?)
(s/def ::example-collection (s/coll-of (s/or :i ::example-int
                                             :f ::example-float)))
(s/def ::example (s/keys :req-un [::example-str
                                  ::example-int
                                  ::example-float
                                  ::example-collection]))
(s/def example ::example)
(def example {:example-str "hello"
              :example-int 42
              :example-float 3.14
              :example-collection [2.3 5 7]})

(let [example-edn (encode (var example))  ;; edn is the default
      example-json (encode (var example) :format "application/json")
      example-yaml (encode (var example) :format "application/x-yaml")

      example-edn:map (encode (var example) :intoMap? true)
      example-json:map (encode (var example) :intoMap? true :format "application/json")
      example-yaml:map (encode (var example) :intoMap? true :format "application/x-yaml")]

     (deftest encoding
       (testing
         (is (= example-edn "{:example-str \"hello\", :example-int 42, :example-float 3.14, :example-collection [2.3 5 7]}"))
         (is (= example-json "{\"example-str\":\"hello\",\"example-int\":42,\"example-float\":3.14,\"example-collection\":[2.3,5,7]}"))
         (is (= example-yaml "example-str: hello\nexample-int: 42\nexample-float: 3.14\nexample-collection: [2.3, 5, 7]\n"))))

     (deftest decoding
       (testing
         (is (= example (decode example-edn:map) (decode example-edn)))
         (is (= example (decode example-json:map) (decode example-json :format "application/json")))
         (is (= example (decode example-yaml:map) (decode example-yaml :format "application/x-yaml")))))

     (deftest decoding+validation
       (testing
         (is (= example (decode example-edn:map :spec ::example)))
         (is (= example (decode example-json:map :spec ::example)))
         (is (= (update-in example [:example-collection] reverse)  ;; NOTE this is unintended
                (decode example-yaml:map :spec ::example))))))

(deftest plain-format
  (testing "anything will be encoded as string; literals should be decoded to the correct type"
    (is (= (:example-str example)
					 (encode (:example-str example) :format "text/plain" :spec ::example-str)
					 (decode (:example-str example) :format "text/plain" :spec ::example-str)))
    (is (= (str (:example-int example))
					 (encode (:example-int example) :format "text/plain" :spec ::example-int)))
    (is (= (:example-int example)
					 (decode (str (:example-int example)) :format "text/plain" :spec ::example-int))))

  (testing "compounds are decoded as string (NOTE: not the original datastructure!)"
    (is (= (str example)
           (encode (var example) :format "text/plain")
           (decode (str example) :format "text/plain" :spec string?)
					 (encode (var example))  ;; NOTE: encoded compounds are equal to application/edn
				))))
