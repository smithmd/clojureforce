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
;; pages
(declare reports-page)
(declare salesforce-report-page)
;; data functions
(declare get-salesforce-reports-list)
(declare get-salesforce-report-data)


;; Page routes
(defn reports-page
  "Shows a list of available reports in salesforce"
  [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (first (first authentications))
        reports-response (get-salesforce-reports-list access-token)
        ]
    reports-response
    ))

(defn salesforce-report-page
  "Return a report for a js library to display"
  [request report-id]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (first (first authentications))
        data-response (get-salesforce-report-data report-id access-token)]
    ))


;; Data for the pages
(defn get-salesforce-reports-list
  "Call for authenticated salesforce user's reports"
  [access-token]
  (let [url "https://na3.salesforce.com/services/data/v31.0/analytics/reports"
        response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token)}})
        reports (json/parse-string (:body response) true)]
    reports))


(defn get-salesforce-report-data
  "Get the data from a single report"
  [report-id access-token]
  (let [url (str "https://na3.salesforce.com/services/data/v29.0/analytics/reports/" report-id)
        response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token)}})
        report-data (json/parse-string (:body response) true)]
    report-data))

;; Routes
(defroutes salesforce-routes
  (GET "/reports" request
    (friend/authenticated (reports-page request)))
  (GET "/reports/:id" [id :as request]
    (friend/authenticated (salesforce-report-page request id)))
  )
