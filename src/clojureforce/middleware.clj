(ns clojureforce.middleware
  (:require [taoensso.timbre :as timbre]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [selmer.middleware :refer [wrap-error-page]]
            [noir-exception.core
              :refer [wrap-internal-error wrap-exceptions]]
            [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri get-access-token-from-params]]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(def config-auth {:roles #{::user}})

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


(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(def development-middleware
  [log-request
   wrap-error-page
   wrap-exceptions])

(def production-middleware
  [#(wrap-internal-error % :log (fn [e] (timbre/error e)))
   (friend/auhenticate
     ring-app
     {:allow-anon? true
      :workflows [(oauth2/workflow
                    {:client-config client-config
                     :uri-config uri-config
                     :access-token-parsefn get-access-token-from-params
                     :config-auth config-auth})]})])

(defn load-middleware []
  (concat (when (env :dev) development-middleware)
          production-middleware))
