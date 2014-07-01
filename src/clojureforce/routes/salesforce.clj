(ns clojureforce.routes.salesforce
  (:use compojure.core)
  (:require [clojureforce.layout :as layout]
            [clojureforce.util :as util]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
              [credentials :as creds])
            [friend-oauth2.workflow :as oauth2]))

(defn list-reports-page [request]
  (friend/authenticated "Authenticated"))

(defroutes salesforce-routes
  (GET "/list-reports" request
    (list-reports-page request))
  (GET "/role-user" req
    (friend/authorize #{::user} "You're a user!")))
