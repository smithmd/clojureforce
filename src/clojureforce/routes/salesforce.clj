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
(declare salesforce-route-authentication)
;; data functions
(declare get-salesforce-api-data)

(def sf-base-url "https://na3.salesforce.com")
(def sf-api-path "/services/data/v30.0")

;; Page routes
(defn salesforce-route-authentication
  "Return a report for a js library to display"
  [request url]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (first (first authentications))
        data-response (get-salesforce-api-data url access-token)]
    data-response))

;; Data for the pages
(defn get-salesforce-api-data
  "Get data from the Salesforce API"
  [url access-token]
  (let [response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token)}})
        data (json/parse-string (:body response) true)]
    data))

;; Routes
(defroutes salesforce-routes
  (GET "/reports/:report-id/instances/:instance-id" [report-id instance-id :as request]
    (friend/authenticated (salesforce-route-authentication request
                            (str sf-base-url sf-api-path "/analytics/reports/" report-id "/instances/" instance-id "?includeDetails=true"))))
  (GET "/reports/:id/instances" [id :as request]
    (friend/authenticated (salesforce-route-authentication request
                            (str sf-base-url sf-api-path "/analytics/reports/" id "/instances"))))
  (GET "/reports/:id/describe" [id :as request]
    (friend/authenticated (salesforce-route-authentication request
                            (str sf-base-url sf-api-path "/analytics/reports/" id "/describe"))))
  (GET "/reports/:id" [id :as request]
    (friend/authenticated (salesforce-route-authentication request
                            (str sf-base-url sf-api-path "/analytics/reports/" id "?includeDetails=true"))))
  (GET "/reports" request
    (friend/authenticated (salesforce-route-authentication request
                            sf-base-url sf-api-path "/analytics/reports")))
  )
