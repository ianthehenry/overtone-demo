(ns overdemo.vocoder
  (:use [overtone.live])
  (:use [overtone.studio.scope])
  (:use [overdemo.wicki]))

(def a (buffer 2048))
(def b (buffer 2048))

(definst vocoder [freq 300 amp 1]
  (let [input (in (num-output-buses:ir))
        ugen square
        src (mix [(ugen (* 1.01 freq))
                  (ugen (* 0.99 freq))]); synth
        formed (pv-mul (fft a input) (fft b src))
        audio (ifft formed)]
    (* audio amp)))

(defn midi-on [raw-note velocity-f]
  (let [note (harmonic-to-wicki raw-note)
        freq (midi->hz note)]
    (ctl vocoder :freq freq)
    (println note)))

(on-event [:midi :note-on]
          (fn [e]
            (midi-on (:note e) (:velocity-f e)))
          ::wicki)

#_ (remove-event-handler ::wicki)
#_ (vocoder)
#_ (kill vocoder)
