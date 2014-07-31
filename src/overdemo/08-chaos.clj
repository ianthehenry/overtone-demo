(ns overdemo.slackbot
  (:use [overtone.live])
  (:use [clojure.core.async :only [chan <! >! <!! >!! go go-loop]])
  (:use [org.httpkit.server :only [run-server]])
  (:use [overtone.inst.synth])
  (:require [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [response]]
            [clojure.data.json :as json]))

(definst ping [freq 440]
  (-> (sin-osc freq)
      (* (env-gen (perc)))))

(stop)

(defn try-note [a]
  (try (note a) (catch Exception e nil)))

(defonce ping-ch (chan))

(go-loop []
  (ping (<! ping-ch))
  (recur))

(defn handler [req]
  (println (:params req))

  (let [words (-> req :params (get "text") (clojure.string/split #" "))
        notes (->> words (map try-note) (remove nil?) (map midi->hz))]
    (doseq [freq notes]
      (>!! ping-ch freq)))

  {:status 200})

(def app (wrap-params handler))

(defonce server (atom nil))

(defn start-server [port]
  (if @server
    (println "server already running")
    (do
      (reset! server (run-server #'app {:port port}))
      (println "server running on port " port))))

(defn stop-server []
  (if @server
    (do
      (@server)
      (reset! server nil)
      (println "server stopped"))
    (println "server not running")))

(defn -main [& args]
  (start-server 5000))

#_ (stop-server)
#_ (start-server 5000)
