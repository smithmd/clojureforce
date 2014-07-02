(ns clojureforce.routes.salesforce
  (:use compojure.core)
  (:require [clojureforce.layout :as layout]
            [clojureforce.util :as util]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
              [credentials :as creds])
            [friend-oauth2.workflow :as oauth2]))

(declare get-salesforce-reports)
(declare reports-page)

(defn reports-page
  "Shows a list of available reports in salesforce"
  [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])]
    (str (vec authentications))))


(defn get-salesforce-reports
  "Call for authenticated salesforce user's reports"
  [access-token]
  (let [url "https://na3.salesforce.com/services/data/v31.0/analytics/dashboards"
        response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token) }})
        reports (json/parse-string (:body response) true)]
    reports))


(defroutes salesforce-routes
  (GET "/list-reports" request
    (friend/authorize #{::user} (reports-page request)))
  (GET "/role-user" req
    (friend/authorize #{::user} "You're a user!")))
