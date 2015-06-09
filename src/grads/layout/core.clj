(ns grads.layout.core
  (:use ring.middleware.anti-forgery)
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.form :as form]
            [grads.database.core :as db]
            [grads.database.backoffice :as bo]))

;;top

(defn link-list
  []
  (let [home [:a {:href "/" :class "button"} "home"]
        sign-up [:a {:href "/sign-up" :class "button"} "sign-up"]
        fill-answer [:a {:href "/fill-answer" :class "button"} "fill-form"]
        backoffice [:a {:href "/backoffice" :class "button"} "backoffice"]
        backoffice-sign-in [:a {:href "/backoffice/sign-in" :class "button"} "sign-in"]
        backoffice-sign-out [:a {:href "/backoffice/sign-out" :class "button"} "sign-out"]
        backoffice-new-answer [:a {:href "/backoffice/new-answer" :class "button"} "new-answer"]
        backoffice-update-answer [:a {:href "/backoffice/update-answer" :class "button"} "update-answer"]
        kunci-jawaban [:a {:href "/kunci" :class "button"} "kunci jawaban"]]
    {:home                     home
     :sign-up                  sign-up
     :fill-answer              fill-answer
     :backoffice               backoffice
     :kunci-jawaban            kunci-jawaban
     :backoffice-sign-in       backoffice-sign-in
     :backoffice-sign-out      backoffice-sign-out
     :backoffice-new-answer    backoffice-new-answer
     :backoffice-update-answer backoffice-update-answer}))

(defn top-link
  [& k]
  (let [refs (link-list)]
    [:div {:class "row"}
     (conj [:div {:class "large-12 collumns"}]
           [:div {:class "nav-bar left"}] (vec (concat [:ul {:class "button-group"}]
                                                       (for [i k]
                                                         [:li (i refs)]))))]))

;;all about form

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
                   (form/label "packet-id" "kode paket soal")
                   (form/text-field "packet-id")]
                  [:div
                   (form/label "packet-type" "pilih paket")
                   (form/drop-down "packet-type" ["IPA" "IPS" "TKPA"])]
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
        old-answer (:answer old-map)
        old-packet (:packet-type old-map)
        old-id (:packet-id old-map)
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
                     (form/label "packet-id" "")
                     (form/hidden-field "packet-id" old-id)]
                    [:div
                     (form/label "delete" "apakah anda mau menghapus kunci ini ?")
                     (form/drop-down "delete" ["tidak" "ya"])]
                    [:div
                     (form/label "packet-type" "pilih paket")
                     (form/drop-down "packet-type" select-first)]
                    [:div
                     (form/label "description" "deskripsi")
                     (form/text-field "description" old-desc)]
                    [:div
                     (form/label "answer" "isi kunci")
                     (form/text-area {:rows 100 :cols 20} "answer" old-answer)]
                    (form/submit-button {:class "button"} "selesai"))]]))

;;all about table

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
        packet-id (:packet-id answer-key-map)
        answer-key (->> (:answer answer-key-map)
                        (vec)
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
        [:td packet-id]
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

;;announcement

(defn ann-backoffice
  [condi]
  [:div {:class "row"}
   [:div {:class "large-6 collumn"}
    (if condi
      [:div
       [:p "list kunci jawaban yang sudah dibuat"]
       (table-key-list-back-office)]
      [:p "Harap sign-in terlebih dahulu"])]])

(defn alert-fail-sign-in
  []
  [:div {:class "row"}
   [:div {:class "large-6 collumn"}
    [:p "GAGAL SIGN-IN"]]])

;;pages

(defn common [& body]
  (html5
    (include-css "https://cdnjs.cloudflare.com/ajax/libs/foundation/5.5.2/css/foundation.min.css")
    [:head
     [:title "Welcome to grader"]]
    [:body body]))

(defn home
  []
  (common (top-link :home :sign-up :kunci-jawaban) (table-key-list)))

(defn sign-up
  []
  (common (top-link :home :sign-up :kunci-jawaban) (form-signup)))

(defn fill-answer
  [u]
  (common (top-link :home :kunci-jawaban) (table-user-info u) (form-user-answer "1" "2" "3")))

(defn done
  []
  (common (top-link :home :kunci-jawaban)))

(defn answer-key-list
  []
  (common (top-link :home :kunci-jawaban) (table-key-list)))

(defn answer-keys-page
  [k]
  (common (top-link :home :kunci-jawaban) (table-answer-key k)))

(defn back-office
  "generate list-of keys
  if need to be updated, just click new number"
  [condi]
  (cond
    (= true condi) (common (top-link :home
                                     :backoffice
                                     :backoffice-new-answer
                                     :backoffice-update-answer
                                     :backoffice-sign-out)
                           (ann-backoffice condi))
    (= false condi) (common (top-link :home
                                      :backoffice
                                      :backoffice-sign-in)
                            (ann-backoffice condi))
    :else (common (top-link :home
                            :backoffice
                            :backoffice-sign-in)
                  (alert-fail-sign-in))) )

(defn back-office-sign-in
  []
  (common (top-link :home
                    :backoffice
                    :backoffice-sign-in) (form-backoffice-sign-in)))

(defn backoffice-new-answer
  []
  (common (top-link :home
                    :backoffice
                    :backoffice-new-answer
                    :backoffice-update-answer
                    :backoffice-sign-out) (form-backoffice-new-answer)))

(defn backoffice-update-answer
  []
  (common (top-link :home
                    :backoffice
                    :backoffice-new-answer
                    :backoffice-update-answer
                    :backoffice-sign-out)
          (table-key-list-back-office)))

(defn backoffice-update-answer-k
  [k]
  (common (top-link :home
                    :backoffice
                    :backoffice-new-answer
                    :backoffice-update-answer
                    :backoffice-sign-out) (form-backoffice-update-answer k)))
;;


