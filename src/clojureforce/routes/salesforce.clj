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
;; Salesforce Routes
(declare salesforce-get)
(declare salesforce-post)
;; Data Functions
(declare get-salesforce-api-data)
(declare post-salesforce-api-data)

(def sf-base-url "https://na3.salesforce.com")
(def sf-api-path "/services/data/v30.0")

;; Authentication
(defn salesforce-route-authentication
  "Return an access token for the salesforce api"
  [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (first (first authentications))]
    access-token))

;; Salesforce routes
(defn salesforce-get
  "Generic route for a GET request"
  [request url]
  (let [access-token (salesforce-route-authentication request)
        data-response (get-salesforce-api-data url access-token)]
    data-response))

(defn salesforce-post
  "Generic route for a POST request"
  [request url]
  (let [access-token (salesforce-route-authentication request)
        data-response (post-salesforce-api-data url access-token request)]
    data-response))

;; Data for the pages
(defn get-salesforce-api-data
  "GET data from the Salesforce API"
  [url access-token]
  (let [response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token)}})
        data (:body response)]
    data))

(defn post-salesforce-api-data
  "POST data to the Salesforce API"
  [url access-token request]
  (let [response (client/post url {:accept :json :headers {"Authorization" (str "Bearer " access-token) :body (get-in request [:body]) :content-type :json}})
        data (:body response)]
    data))



;; Routes
(defroutes salesforce-routes
  (POST "/reports/:id/instances/new-async" [id :as request]
    (friend/authenticated (salesforce-post request
                            (str sf-base-url sf-api-path "/analytics/reports/" id "/instances"))))
  (GET "/reports/:report-id/instances/:instance-id" [report-id instance-id :as request]
    (friend/authenticated (salesforce-get request
                            (str sf-base-url sf-api-path "/analytics/reports/" report-id "/instances/" instance-id))))
  (GET "/reports/:id/instances" [id :as request]
    (friend/authenticated (salesforce-get request
                            (str sf-base-url sf-api-path "/analytics/reports/" id "/instances"))))
  (GET "/reports/:id/describe" [id :as request]
    (friend/authenticated (salesforce-get request
                            (str sf-base-url sf-api-path "/analytics/reports/" id "/describe"))))
  (GET "/reports/:id" [id :as request]
    (friend/authenticated (salesforce-get request
                            (str sf-base-url sf-api-path "/analytics/reports/" id))))
  (GET "/reports" request
    (friend/authenticated (salesforce-get request
                            (str sf-base-url sf-api-path "/analytics/reports"))))
  (GET "/scatterplots" request
    (friend/authenticated (layout/render "salesforce-scatter.html")))
  (GET "/barcharts" request
    (friend/authenticated (layout/render "salesforce-bar.html")))
  )
