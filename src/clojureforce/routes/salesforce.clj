(ns clojureforce.routes.salesforce
  (:use compojure.core)
  (:require [clojureforce.layout :as layout]
            [clojureforce.util :as util]
            [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri get-access-token-from-params]]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [cheshire.core :as j]
            [clj-http.client :as client]))


(defn get-salesforce-reports
  "Call for authenticated salesforce user's reports"
  [access-token]
  (let [url "https://na3.salesforce.com/services/data/v29.0/analytics/reports"
        response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token) }})
        reports (j/parse-string (:body response) true)]
    reports))

(defn reports-page
  "Shows a list of available reports in salesforce"
  [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (:access_token (second (first authentications)))
        reports-response (get-salesforce-reports access-token)]
    (str (vec (map :name reports-response)))))

(defroutes salesforce-routes
  (GET "/get-reports" request (friend/authorize #{::user} (reports-page request)))
  (friend/logout (ANY "/logout" request (ring.util.response/redirect "/"))))