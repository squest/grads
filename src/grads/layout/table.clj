(ns grads.layout.table
  (:require [hiccup.page :refer [html5 include-css]]
            [grads.database.core :as db]
            [grads.database.backoffice :as bo]))

(defn table-user-info
  [u]
  (let [{:keys [username
                email
                paket
                pilihan-1
                pilihan-2
                pilihan-3
                form-status]} (db/get-user-map u)
        table-pilihan (vec (concat [:table]
                                   (for [i [pilihan-1 pilihan-2 pilihan-3]]
                                     (let [[k [u j]] i]
                                       [:tr
                                        [:td k]
                                        [:td u]
                                        [:td j]]))))
        table-answer (let [ks [:TKPA :IPA :IPS]]
                       (vec (concat [:table]
                                    (for [k ks]
                                      (let [status (k form-status)
                                            [kode [benar salah kosong]] status]
                                        [:tr
                                         [:td (apply str (drop 1 (str k)))]
                                         [:td kode]
                                         [:td "benar " benar]
                                         [:td "salah " salah]
                                         [:td "kosong " kosong]])))))]
    [:div {:class "row"}
     [:div {:class "large-6 collumns"}
      [:table
       [:tr
        [:td "username"] [:td username]]
       [:tr
        [:td "email"] [:td email]]
       [:tr
        [:td "paket"] [:td paket]]
       [:tr
        [:td "pilihan"] [:td table-pilihan]]
       [:tr
        [:td "jawaban"] [:td table-answer]]]]]))

(defn partition-
  [n coll]
  (loop [xs coll res []]
    (if (>= n (count xs))
      (conj res xs)
      (recur (drop n xs) (conj res (take n xs))))))

(defn table-key-list
  []
  (let [key-list (partition- 10 (db/get-answer-key-list))]
    (conj [:div {:class "row"}]
          (conj [:div {:class "large-10 collumns"}]
                (vec (concat [:table]
                             (for [ks key-list]
                               (vec (concat [:tr]
                                            (mapv (fn [k]
                                                    [:td
                                                     [:a {:href (str "/kunci/" k)} (str k)]])
                                                  ks))))))))))

(defn table-answer-key
  "generate table of answer key 'k'"
  [k]
  (let [answer-key-map (bo/get-answer-key-map k)
        packetid (:packetid answer-key-map)
        answer-key (->> (:answer answer-key-map)
                        (vec)
                        (map (fn [[k v]]
                               [(->> k
                                     (str)
                                     (drop 1)
                                     (apply str)
                                     Integer/parseInt) v]))
                        (sort-by first))
        answer-table (vec (cons :div (vec (for [[n a] answer-key]
                                            [:tr
                                             [:td n]
                                             [:td a]]))))]
    [:div {:class "row"}
     [:div {:class "large-6 collumn"}
      [:table
       [:tr
        [:td "no paket"]
        [:td packetid]
        answer-table]]]]))


(defn table-key-list-back-office
  "generate table cointaining list of answer key list"
  []
  (let [key-list (partition- 10 (db/get-answer-key-list))]
    (conj [:div {:class "row"}]
          (conj [:div {:class "large-10 collumns"}]
                (vec (concat [:table]
                             (for [ks key-list]
                               (vec (concat [:tr]
                                            (mapv (fn [k]
                                                    [:td
                                                     [:a {:href (str "/backoffice/update-answer/" k)} (str k)]])
                                                  ks))))))))))
