(ns grads.database.backoffice
  (require [grads.database.core :refer :all]
           [com.ashafa.clutch :as cl]))

{:ctype       "kunci"
 :packet-id   "123"
 :packet-type "IPA" "IPS" "TKPA"
 :answer      {1  "A"
               2  "B"
               40 "C"}}

(def edn-path "./resources/key/")

(defn validate-answer-map
  [answer-map]
  (let [{:keys [packet-id packet-type answer]} answer-map]
    (= (range 1 76) (sort (keys (read-string (:answer answer-map)))))))

(defn save-answer-key
  [answer-key-map]
  (if (validate-answer-map answer-key-map)
    (let [{:keys [description packet-id packet-type answer]} answer-key-map
          upper-case-answer (->> (read-string answer)
                                 (vec)
                                 (map (fn [[k v]]
                                        [k (clojure.string/upper-case v)]))
                                 (into {}))
          capsule (assoc {} :ctype "answer-key"
                            :description description
                            :packet-id packet-id
                            :packet-type packet-type
                            :answer upper-case-answer)
          old-cloud (first (my-view "key" "byPacket" {:key (str packet-id)}))]
      (do
        (if (or (empty? old-cloud)
                (nil? old-cloud))
          (cl/put-document db (assoc {} :ctype "kunci" :paket packet-id))
          (cl/put-document db old-cloud))
        (spit (str edn-path (str packet-id) ".edn") (str capsule))
        true))
    false))

(defn get-answer-key-map
  [k]
  (let [old (read-string (slurp (str edn-path k ".edn")))
        sorted-answer (->> old
                           (:answer)
                           (vec)
                           (flatten)
                           (apply sorted-map))]
    (assoc old :answer sorted-answer)))

(defn delete-answer-key
  [k]
  (do
    (clojure.java.io/delete-file (str edn-path k ".edn"))
    (cl/delete-document db (first (my-view "key" "byPacket" {:key k})))))
