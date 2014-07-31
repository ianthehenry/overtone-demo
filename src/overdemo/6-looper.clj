(ns overdemo.looper
  (:use [overtone.live]))

(def loop-data (buffer (* 44100 1) 1))

(defsynth fetch-data []
  (record-buf (in (num-output-buses:ir))
              loop-data :action FREE :loop 1))

(definst replay []
  (* (mouse-y) (play-buf 1 loop-data
                         :rate (mouse-x 0.25 4)
                         :loop 1
                         :action FREE)))

(fetch-data)

(replay)
(kill replay)

(inst-fx! replay fx-reverb)

#_ (kill replay)
#_ (kill fetch-data)
