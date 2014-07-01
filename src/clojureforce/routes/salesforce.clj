(ns clojureforce.routes.salesforce
  (:use compojure.core)
  (:require [clojureforce.layout :as layout]
            [clojureforce.util :as util]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
              [credentials :as creds])
            [friend-oauth2.workflow :as oauth2]))


(defn reports-page
  "Shows a list of available reports in salesforce"
  [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (:access_token (second (first authentications)))
        reports-response (get-salesforce-reports access-token)]
    (str (vec (map :name reports-response)))))


(defn get-salesforce-reports
  "Call for authenticated salesforce user's reports"
  [access-token]
  (let [url "https://na3.salesforce.com/services/data/v31.0/analytics/dashboards"
        response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token) }})
        reports (j/parse-string (:body response) true)]
    reports))


(defroutes salesforce-routes
  (GET "/list-reports" request
    (reports-page request))
  (GET "/role-user" req
    (friend/authorize #{::user} "You're a user!")))
