(ns grads.routes.home
  (:require [compojure.route :refer :all]
            [compojure.core :refer :all]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [response]]
            [noir.response :as resp]
            [noir.session :as sess]
            [clojure.java.io :as io]
            [grads.layout.core :as page]
            [grads.database.user :as user]
            [grads.database.backoffice :as backoffice]))

(defn unsigned []
  "not done")

(defn something []
  nil)

(defroutes home-routes
           (GET "/" [] (page/home))
           (GET "/sign-up" [] (page/sign-up))
           (POST "/sign-up" req (let [sign-up-map (:params req)]
                                  (if (user/valid?-email-pass sign-up-map)
                                    (do
                                      (user/save-new-user sign-up-map)
                                      (sess/put! :username (:name sign-up-map))
                                      (resp/redirect "/fill-answer"))
                                    (str sign-up-map)
                                    )))
           (GET "/fill-answer" req (let [user (sess/get :username)]
                                     (page/fill-answer user)))
           (POST "/fill-answer" req (let [user (sess/get :username)]
                                      (do
                                        (user/update-user-form-status user (:params req))
                                        (resp/redirect "/fill-answer"))))
           (GET "/done" req (unsigned))

           (GET "/kunci" req (page/answer-key-list))
           (GET "/kunci/:kode" req (page/answer-keys-page (:kode (:params req))))

           (GET "/backoffice" req (let [admin (sess/get :admin)]
                                    (if admin
                                      (page/back-office true)
                                      (page/back-office false))))
           (GET "/backoffice/sign-in" req (page/back-office-sign-in))
           (POST "/backoffice/sign-in" req (let [{:keys [username password]} (:params req)]
                                             (if (and (= "admin" username)
                                                      (= "mizones" password))
                                               (do
                                                 (sess/put! :admin true)
                                                 (resp/redirect "/backoffice"))
                                               (page/back-office "gagal"))))
           (GET "/backoffice/sign-out" req (do
                                             (sess/clear!)
                                             (resp/redirect "/backoffice")))
           (GET "/backoffice/new-answer" req (page/backoffice-new-answer))
           (POST "/backoffice/new-answer" req (let [answer-map (:params req)]
                                                (if (backoffice/validate-answer-map answer-map)
                                                  (do
                                                    (backoffice/save-answer-key answer-map)
                                                    (resp/redirect "/backoffice"))
                                                  (resp/redirect "/backoffice"))))
           (GET "/backoffice/update-answer" req (page/backoffice-update-answer))
           (GET "/backoffice/update-answer/:kode" [kode] (page/backoffice-update-answer-k kode))
           (POST "/backoffice/update-answer/:kode" req (let [new-map (:params req)
                                                             delete? (:delete new-map)
                                                             answer-map (dissoc new-map :delete)]
                                                         (if (= delete? "ya")
                                                           (do
                                                             (backoffice/delete-answer-key (:kode new-map))
                                                             (resp/redirect "/backoffice"))
                                                           (do
                                                             (backoffice/save-answer-key answer-map)
                                                             (resp/redirect "/backoffice"))))))

