(ns overdemo.looper
  (:use [overtone.live]))

(def loop-data (buffer (* 44100 1) 1))

(defsynth fetch-data []
  (record-buf (in (num-output-buses:ir))
              loop-data :action FREE :loop 0))

(definst replay []
  (* (mouse-y) (play-buf 1 loop-data
                         :rate (mouse-x 0.2 1)
                         :loop 0
                         :action FREE)))

#_ (fetch-data)
#_ (replay)

#_ (kill replay)
#_ (kill fetch-data)
