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

(declare get-salesforce-reports-list)
(declare reports-page)

(defn reports-page
  "Shows a list of available reports in salesforce"
  [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (first (first authentications))
        reports-response (get-salesforce-reports-list access-token)
        ]
    reports-response
    ))


(defn get-salesforce-reports-list
  "Call for authenticated salesforce user's reports"
  [access-token]
  (let [url "https://na3.salesforce.com/services/data/v31.0/analytics/reports"
        response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token) }})
        reports (json/parse-string (:body response) true)]
    reports))

(defn get-salesforce-report
  "Get the data from a single report"
  [report-id]
  report-id
  )


(defroutes salesforce-routes
  (GET "/list-reports" request
    (friend/authenticated (reports-page request)))
  (GET "/report-data/:report-id" {{report-id "report-id"} :params}
    (friend/authenticated (get-salesforce-report report-id))))
