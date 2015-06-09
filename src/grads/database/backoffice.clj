(ns grads.database.backoffice
  (require [grads.database.core :refer :all]
           [com.ashafa.clutch :as cl]))

{:ctype      "kunci"
 :packetid   "123"
 :packettype "IPA" "IPS" "TKPA"
 :answer     {1  "A"
              2  "B"
              40 "C"}}

;;migrating to cloudant

(def edn-path "./resources/key/")

(defn validate-answer-map
  [answer-map]
  (let [{:keys [packetid packettype answer]} answer-map]
    (= (range 1 76) (sort (keys (read-string (:answer answer-map)))))))

(defn save-answer-key
  [answer-key-map]
  (if (validate-answer-map answer-key-map)
    (let [{:keys [description packetid packettype answer]} answer-key-map
          upper-case-answer (->> (read-string answer)
                                 (vec)
                                 (map (fn [[k v]]
                                        [k (clojure.string/upper-case v)]))
                                 (into {}))
          capsule (assoc {} :ctype "kunci"
                            :description description
                            :packetid packetid
                            :packettype packettype
                            :answer upper-case-answer)
          old-cloud (first (my-view "key" "byPacket" {:key (str packetid)}))]
      (do
        (cl/put-document db (if (nil? old-cloud)
                              capsule
                              (merge old-cloud capsule)))
        true))
    false))

(defn get-answer-key-map
  [k]
  (first (my-view "key" "byPacket" {:key k})))

(defn delete-answer-key
  [k]
  (do
    (cl/delete-document db (first (my-view "key" "byPacket" {:key k})))))
