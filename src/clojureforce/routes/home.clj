(ns clojureforce.routes.home
  (:use compojure.core)
  (:require [clojureforce.layout :as layout]
            [clojureforce.util :as util]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
