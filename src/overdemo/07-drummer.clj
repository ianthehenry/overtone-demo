(ns overdemo.drummer
  (:use [overtone.live])
  (:use [org.httpkit.server :only [run-server]])
  (:require [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [response header]]
            [clojure.java.io :refer [reader]]
            [clojure.data.json :as json]
            [overtone.inst.drum :as drum]))


(def snare (sample (freesound-path 26903)))
(def kick (sample (freesound-path 2086)))
(def close-hihat (sample (freesound-path 802)))
(def clap (sample (freesound-path 48310)))
(def gshake (sample (freesound-path 113625)))
(definst ping [freq 440 amp 1 gate 1]
  (-> (sin-osc freq)
      (* (env-gen (perc) :action FREE))
      (* amp)))

(defonce machine (atom {"kick" 0 "hi-hat" 0 "snare" 0 "clap" 0 "ping" 0 "shaker" 0}))

(defn array [stupid-machine divisions]
  (if (number? stupid-machine)
    (cons stupid-machine (take (- divisions 1) (repeat 0)))

    (let [[l r] stupid-machine]
      (concat (array l (quot divisions 2))
              (array r (quot divisions 2))))))

(defn matches? [n desc]
  (= (get (vec (array desc 16)) n) 1))

(defn sounds-at [machine n]
  (->> ["kick" "hi-hat" "snare" "clap" "ping" "shaker"]
       (filter (fn [inst]
                 (matches? n (get machine inst))))
       (map {"kick" kick "hi-hat" close-hihat
             "snare" snare "clap" clap
             "ping" ping "shaker" gshake})))

(defonce nome (metronome 360))

(defn looper [nome]
  (let [beat (nome)]

    (doseq [sound (sounds-at @machine (mod beat 16))]
      (at (nome beat) (sound)))

    (apply-by (nome (inc beat)) #'looper nome [])))

#_ (looper nome)

(defn app [req]
  (let [desc (json/read (reader (:body req)))]
    (reset! machine desc))
  (-> (response "")
      (header "Access-Control-Allow-Origin" "*")))

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

#_ (start-server 7000)
#_ (stop-server)
