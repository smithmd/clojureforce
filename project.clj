(defproject clojureforce "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [lib-noir "0.8.4"]
                 [ring-server "0.3.1"]
                 [selmer "0.6.8"]
                 [com.taoensso/timbre "3.2.1"]
                 [com.taoensso/tower "2.0.2"]
                 [markdown-clj "0.9.44"]
                 [environ "0.5.0"]
                 [noir-exception "0.2.2"]
                 [com.cemerick/friend "0.2.0"]
                 [friend-oauth2 "0.1.1"]
                 [cheshire "5.3.1"]]

  :repl-options {:init-ns clojureforce.repl}
  :plugins [[lein-ring "0.8.10"]
            [lein-environ "0.5.0"]]
  :ring {:handler clojureforce.handler/app
         :init    clojureforce.handler/init
         :destroy clojureforce.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.0"]]
         :env {:dev true}}}
  :min-lein-version "2.0.0")