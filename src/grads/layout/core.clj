(ns grads.layout.core
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.form :as form]
            [grads.database.core :as db]
            [grads.database.backoffice :as bo]
            [grads.layout.form :refer :all]
            [grads.layout.table :refer :all]))

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
        kunci-jawaban [:a {:href "/kunci" :class "button"} "kunci jawaban"]
        done [:a {:href "/done" :class "button"} "selesai"]]
    {:home                     home
     :sign-up                  sign-up
     :fill-answer              fill-answer
     :backoffice               backoffice
     :kunci-jawaban            kunci-jawaban
     :backoffice-sign-in       backoffice-sign-in
     :backoffice-sign-out      backoffice-sign-out
     :backoffice-new-answer    backoffice-new-answer
     :backoffice-update-answer backoffice-update-answer
     :done                     done}))

(defn top-link
  [& k]
  (let [refs (link-list)]
    [:div {:class "row"}
     (conj [:div {:class "large-12 collumns"}]
           [:div {:class "nav-bar left"}] (vec (concat [:ul {:class "button-group"}]
                                                       (for [i k]
                                                         [:li (i refs)]))))]))
(defn wrapper
  [paragraph]
  [:div {:class "row"}
   [:div {:class "large-8 collumn"}
    paragraph]])

;;announcement

(defn ann-home []
  (wrapper [:p "Selamat datang, untuk mengisi form silahkan meng-klik kotak sign-up"]))

(defn ann-sign-up []
  (wrapper [:p "silahkan mengisi data diri kamu. kalau kamu pengguna zenius silahkan gunakan email zeniusmu :)"]))

(defn ann-fill-answer-top []
  (wrapper [:div [:p "masukan jumlah benar, salah, dan kosong pada kotak yang tersedia."]
            [:p "untuk pendaftar IPA silahkan mengisi TKPA dan IPA saja, untuk IPS isi TKPA dan IPS"]
            [:p "untuk IPC, semuanya dong :P"]]))

(defn ann-fill-answer-mid []
  (wrapper [:p "klik dibawah ini untuk memasukan datamu :)"]))

(defn ann-fill-answer-bot []
  (wrapper [:div
            [:p "untuk memasukan data klik yang diatas"]
            [:p "bila sudah selesai dan datamu benar, silahkan klik dibawah ini"]]))

(defn ann-kunci []
  (wrapper [:p "dibawah ini adalah list kode soal, untuk melihan kunci jawabannya silahkan klik sesuai nomor :)"]))

(defn ann-done []
  (wrapper [:p "terima kasih atas partisipasinya :)"]))

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
  (common (top-link :home :sign-up :kunci-jawaban) (ann-home)))

(defn sign-up
  []
  (common (top-link :home :sign-up :kunci-jawaban) (ann-sign-up) (form-signup)))

(defn fill-answer
  [u]
  (common (top-link :home :kunci-jawaban)
          (ann-fill-answer-top)
          (table-user-info u)
          (form-user-answer 1 2 3)
          (ann-fill-answer-bot)
          (top-link :done)))

(defn done
  []
  (common (top-link :home :kunci-jawaban) (ann-done)))

(defn answer-key-list
  []
  (common (top-link :home :kunci-jawaban) (ann-kunci) (table-key-list)))

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
                  (alert-fail-sign-in))))

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


