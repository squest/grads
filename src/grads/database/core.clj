(ns grads.database.core
  (require [com.ashafa.clutch :as cl]
           [cemerick.url :as c.url]))

(defonce db (assoc
              (c.url/url "https://kunci-sbmptn.cloudant.com/" "grader")
              :username "edingtoompamerhingstrart"
              :password "OccwArJ8dYubAWBAAatsRckI"))

(defn my-view
  ([doc view]
   (my-view doc view {}))
  ([doc view key]
   (map :value (cl/get-view db doc view key))))


;;querying

(defn get-user-map
  [username]
  (first (my-view "user" "byId" {:key username})))

(defn get-answer-key-list
  []
  (->> (my-view "key" "byPacket")
       (map :paket)
       (filter #(not (nil? %)))
       (sort-by #(Integer/parseInt %))))

