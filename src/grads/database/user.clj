(ns grads.database.user
  (require [grads.database.core :refer :all]
           [com.ashafa.clutch :as cl]))

;;user adder and updater

(defn save-new-user
  "save user-map in couchbase"
  [user-map]
  (let [jurusan-map (read-string (slurp "raw-excel/jurusan.edn"))
        {:keys [name
                password
                email
                paket
                pilihan-1
                pilihan-2
                pilihan-3]} user-map
        default-status {:IPA  [0 [0 0 0]]
                        :IPS  [0 [0 0 0]]
                        :TKPA [0 [0 0 0]]}]
    (cl/put-document db {:ctype       "user-profile"
                         :username    name
                         :password    password
                         :email       email
                         :paket       paket
                         :pilihan-1   [pilihan-1 (get jurusan-map (Integer/parseInt pilihan-1))]
                         :pilihan-2   [pilihan-2 (get jurusan-map (Integer/parseInt pilihan-2))]
                         :pilihan-3   [pilihan-3 (get jurusan-map (Integer/parseInt pilihan-3))]
                         :form-status default-status})))



(defn update-user-form-status
  [username answer-map]
  (let [{:keys [kode-1
                benar-1
                salah-1
                kosong-1
                kode-2
                benar-2
                salah-2
                kosong-2
                kode-3
                benar-3
                salah-3
                kosong-3]} answer-map
        new-status {:TKPA [kode-1 [benar-1 salah-1 kosong-1]]
                    :IPA  [kode-2 [benar-2 salah-2 kosong-2]]
                    :IPS  [kode-3 [benar-3 salah-3 kosong-3]]}
        old-map (get-user-map username)
        ]
    (cl/put-document db (assoc old-map :form-status new-status))))

;;user checker

(defn check-email-password
  "check if username/email is avaible and password is correct"
  [user-map]
  (let [{:keys [email password]} user-map
        user-old-map (get-user-map email)]
    (if (nil? user-old-map)
      true
      (= password (:password user-old-map)))))

(defn valid?-email
  [user-map]
  (let [{:keys [email email-1]} user-map]
    (= email email-1)))

(defn valid?-password
  [user-map]
  (let [{:keys [password password-1]} user-map]
    (and (= password password-1)
         (> (count (seq password)) 7))))

(defn valid?-email-pass
  [user-map]
  (and (valid?-password user-map)
       (valid?-email user-map)
       (check-email-password user-map)))
