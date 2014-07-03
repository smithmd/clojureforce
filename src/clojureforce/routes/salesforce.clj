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

;; Authentication
(declare salesforce-route-authentication)
;; Pages
(declare salesforce-get)
(declare salesforce-post)
;; Data Functions
(declare get-salesforce-api-data)
(declare post-salesforce-api-data)

(def sf-base-url "https://na3.salesforce.com")
(def sf-api-path "/services/data/v30.0")

;; Authentication
(defn salesforce-route-authentication
  "Return a report for a js library to display"
  [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (first (first authentications))]
    access-token))

;; Page routes
(defn salesforce-get
  [request url]
  (let [access-token (salesforce-route-authentication request)
        data-response (get-salesforce-api-data url access-token)]
    data-response))

(defn salesforce-post
  [request url]
  (let [access-token (salesforce-route-authentication request)
        data-response (pose-salesforce-api-data url access-token)]
    data-response))

;; Data for the pages
(defn get-salesforce-api-data
  "GET data from the Salesforce API"
  [url access-token]
  (let [response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token)}})
        data (json/parse-string (:body response) true)]
    data))

(defn post-salesforce-api-data
  "POST data to the Salesforce API"
  [url access-token]
  (let [response (client/post url {:accept :json :headers {"Authorization" (str "Bearer " access-token)}})
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
