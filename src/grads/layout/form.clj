(ns grads.layout.form
  (:use ring.middleware.anti-forgery)
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.form :as form]
            [grads.database.backoffice :as bo]))

(defn form-signup []
  "generate form for user to signup"
  [:div {:class "row"}
   [:div {:id "form-signup" :class "large-4 collumns"}
    (form/form-to [:post "/sign-up"]
                  [:div
                   (form/hidden-field "__anti-forgery-token" *anti-forgery-token*)]
                  [:div
                   (form/label "email" "isi email")
                   (form/email-field "email")]
                  [:div
                   (form/label "email" "ketik ulang emailmu")
                   (form/email-field "email-1")]
                  [:div
                   (form/label "password" "isi password (minimal 8 karakter)")
                   (form/password-field "password")]
                  [:div
                   (form/label "password-1" "ketik ulang passwordmu")
                   (form/password-field "password-1")]
                  [:div
                   (form/label "name" "isi nama")
                   (form/text-field "name")]
                  [:div
                   (form/label "paket" "pilih paket")
                   (form/drop-down "paket" ["IPA" "IPS" "IPC"])]
                  [:div
                   (form/label "pilihan-1" "isi kode pilihan pertama")
                   (form/text-field "pilihan-1")]
                  [:div
                   (form/label "pilihan-2" "isi kode pilihan kedua")
                   (form/text-field "pilihan-2")]
                  [:div
                   (form/label "pilihan-1" "isi kode pilihan ketiga")
                   (form/text-field "pilihan-3")]
                  (form/submit-button "sign-up"))]])

(defn form-user-answer
  "generate form for user to input their answer
  paket-list -> 1 2 3"
  [& paket-list]
  (let [paket-st (->> paket-list
                      (interpose "-")
                      (apply str))
        form-generate (for [paket paket-list]
                        (let [id (str "kode-" paket)
                              right (str "benar-" paket)
                              wrong (str "salah-" paket)
                              empty (str "kosong-" paket)]
                          [:div
                           [:h3 (cond
                                  (= 1 paket) "TKPA"
                                  (= 2 paket) "IPA"
                                  :else "IPS")]
                           [:div
                            (form/label id (str "isi kode"))
                            (form/text-field id)]
                           [:div
                            (form/label right "isi jumlah jawaban yang benar")
                            (form/text-field right)]
                           [:div
                            (form/label wrong "isi jumlah jawaban yang salah")
                            (form/text-field wrong)]
                           [:div
                            (form/label empty "isi jumlah jawaban yang tidak diisi")
                            (form/text-field empty)]]))]
    [:div {:class "row"}
     [:div {:id (str "form-" paket-st) :class "large-6 collumns"}
      (vec (concat (form/form-to [:post "/fill-answer"])
                   form-generate
                   [[:div
                     (form/hidden-field "__anti-forgery-token" *anti-forgery-token*)]]
                   [(form/submit-button {:class "button"} "masukan hasil")]))]]))

(defn form-backoffice-sign-in
  []
  [:div {:class "row"}
   [:div {:id "form-signup" :class "large-4 collumns"}
    (form/form-to [:post "/backoffice/sign-in"]
                  [:div
                   (form/hidden-field "__anti-forgery-token" *anti-forgery-token*)]
                  [:div
                   (form/label "username" "username")
                   (form/text-field "username")]
                  [:div
                   (form/label "password" "isi password")
                   (form/password-field "password")]
                  (form/submit-button {:class "button"} "sign-in"))]])

(defn form-backoffice-new-answer
  []
  [:div {:class "row"}
   [:div {:id "form-signup" :class "large-9 collumns"}
    (form/form-to [:post "/backoffice/new-answer"]
                  [:div
                   (form/hidden-field "__anti-forgery-token" *anti-forgery-token*)]
                  [:div
                   (form/label "packetid" "kode paket soal")
                   (form/text-field "packetid")]
                  [:div
                   (form/label "packettype" "pilih paket")
                   (form/drop-down "packettype" ["IPA" "IPS" "TKPA"])]
                  [:div
                   (form/label "description" "deskripsi")
                   (form/text-field "description")]
                  [:div
                   (form/label "answer" "isi kunci")
                   (form/text-area {:rows 100 :cols 20} "answer")]
                  (form/submit-button {:class "button"} "selesai"))]])

(defn form-backoffice-update-answer
  [k]
  (let [old-map (bo/get-answer-key-map k)
        old-answer (->> (:answer old-map)
                        (vec)
                        (map (fn [[k v]]
                               [(->> k
                                     (str)
                                     (drop 1)
                                     (apply str)
                                     (Integer/parseInt))
                                v]))
                        (flatten)
                        (apply sorted-map))
        old-packet (:packettype old-map)
        old-id (:packetid old-map)
        old-desc (:description old-map)
        select-first (cond
                       (= "IPA" old-packet) ["IPA" "TKPA" "IPS"]
                       (= "IPS" old-packet) ["IPS" "TKPA" "IPA"]
                       :else ["TKPA" "IPA" "IPS"])]
    [:div {:class "row"}
     [:div {:id "form-signup" :class "large-9 collumns"}
      (form/form-to [:post (str "/backoffice/update-answer/" k)]
                    [:div
                     (form/hidden-field "__anti-forgery-token" *anti-forgery-token*)]
                    [:div
                     (form/label "packetid" "")
                     (form/hidden-field "packetid" old-id)]
                    [:div
                     (form/label "delete" "apakah anda mau menghapus kunci ini ?")
                     (form/drop-down "delete" ["tidak" "ya"])]
                    [:div
                     (form/label "packettype" "pilih paket")
                     (form/drop-down "packettype" select-first)]
                    [:div
                     (form/label "description" "deskripsi")
                     (form/text-field "description" old-desc)]
                    [:div
                     (form/label "answer" "isi kunci")
                     (form/text-area {:rows 100 :cols 20} "answer" old-answer)]
                    (form/submit-button {:class "button"} "selesai"))]]))


