(ns clojureforce.util
  (:require [noir.io :as io]
            [markdown.core :as md]
            [cheshire.core :as j]
            [clj-http.client :as client]))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))

(defn get-salesforce-reports
  "Call for authenticated salesforce user's reports"
  [access-token]
  (let [url "https://na3.salesforce.com/services/data/v29.0/analytics/reports"
        response (client/get url {:accept :json :headers {"Authorization" (str "Bearer " access-token) }})
        reports (j/parse-string (:body response) true)]
    reports))