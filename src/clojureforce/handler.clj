(ns clojureforce.handler
  (:require [compojure.core :refer [defroutes]]
            [clojureforce.routes.home :refer [home-routes]]
            [clojureforce.routes.salesforce :refer [salesforce-routes]]
            [clojureforce.util :as util]
            [clojureforce.middleware :refer [load-middleware]]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
              [credentials :as creds])
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri extract-access-token]]
            [ring.util.response :as resp]
            [ring.util.codec :as codec]
            [hiccup.page :as h]
            [hiccup.element :as e]
            ))

(defroutes resource-routes
  (route/resources "/"))

(defroutes app-routes
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/appender-fn})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "clojureforce.log" :max-size (* 512 1024) :backlog 10})

  (if (env :dev) (parser/cache-off!))
  (timbre/info "clojureforce started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "clojureforce is shutting down..."))


(def config-auth {:roles #{::user}})

(def client-config
  {:client-id (System/getenv "CLIENT_ID")
   :client-secret (System/getenv "CLIENT_SECRET")
   :callback {:domain "https://rocky-river-7942.herokuapp.com" :path "/salesforce.callback"}})

(def uri-config
  {:authentication-uri {:url (str (System/getenv "API_URL") "/services/oauth2/authorize")
                        :query {:client_id (:client-id client-config)
                                :response_type "code"
                                :redirect_uri (format-config-uri client-config)
                                :scope "api"}}

   :access-token-uri {:url (str (System/getenv "API_URL") "/services/oauth2/token")
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (format-config-uri client-config)
                              }}})

(def app (app-handler
           ;; add your application routes here
           [home-routes
            resource-routes
            (friend/authenticate salesforce-routes
              {:allow-anon? true
               :default-landing-uri "/"
               :login-uri "/salesforce.login"
               :workflows [(oauth2/workflow
                             {:client-config client-config
                              :uri-config uri-config
                              :config-auth config-auth
                              :access-token-parsefn extract-access-token})]
               })
            app-routes
            ]
           ;; add custom middleware here
           :middleware (load-middleware)
           ;; timeout sessions after 30 minutes
           :session-options {:timeout (* 60 30)
                             :timeout-response (redirect "/")}
           ;; add access rules here
           :access-rules []
           ;; serialize/deserialize the following data formats
           ;; available formats:
           ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
           :formats [:json-kw :edn]))
