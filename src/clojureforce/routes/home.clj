(ns clojureforce.routes.home
  (:use compojure.core)
  (:require [clojureforce.layout :as layout]
            [clojureforce.util :as util]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defn status-page []
  (let [count (:count (:session ) 0)
        session (assoc (:session ) :count (inc count))]
    (-> (ring.util.response/response
          (str "<p>We've hit the session page " (:count session)
            " times.</p><p>The current session: " session "</p>"))
      (assoc :session session))))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/status" [] (status-page)))

