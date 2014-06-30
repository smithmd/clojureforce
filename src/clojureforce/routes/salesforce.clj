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


(def client-config
  {:client-id "3MVG9Km_cBLhsuPy_yi8OscDmCRcTnQRCLS_sSLrhur.23PmBXSU0KsW8H9_n6NU0OECokNTe1StOsZhcA4Cp"
   :client-secret "5840135966506047574"
   :callback {
               :domain "https://rocky-river-7942.herokuapp.com/"
               :path "/salesforce.callback" }})

(def uri-config
  {:authentication-uri {:url "https://login.salesforce.com/services/oauth2/authorize"
                        :query {:client_id (:client-id client-config)
                                :response_type "code"
                                :redirect_uri (format-config-uri client-config)
                                :scope "user"}}

   :access-token-uri {:url "https://login.salesforce.com/services/oauth2/token"
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization code"
                              :redirect_uri (format-config-uri client-config)}}})

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