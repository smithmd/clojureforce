(ns clojureforce.routes.home
  (:use compojure.core)
  (:require [clojureforce.layout :as layout]
            [clojureforce.util :as util]
            [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri get-access-token-from-params]]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defn reports-page
  "Shows a list of available reports in salesforce"
  [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (:access_token (second (first authentications)))
        reports-response (util/get-salesforce-reports access-token)]
    (str (vec (map :name reports-response)))))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/get-reports" request (friend/authorize #{::user} (reports-page request))))

