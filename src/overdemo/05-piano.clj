(ns overdemo.piano
  (:use [overtone.live])
  (:use [overdemo.wicki])
  (:use [overtone.inst.sampled-piano]))

(defonce players (atom {}))

#_ (reset! players nil)

(definst ping [note 60 amp 1 gate 1]
  (-> (sin-osc (midicps note))
      (* (env-gen (adsr) gate :action FREE))
      (* amp)))

(defn midi-on [note velocity-f]
  (if-let [player (get @players note)]
    (ctl player :gate 0))
  (swap! players assoc note (ping note velocity-f)))

(defn midi-off [note]
  (if-let [player (get @players note)]
      (ctl player :gate 0))
  (swap! players dissoc note))

(on-event [:midi :note-on]
          (fn [e] (midi-on (-> e :note harmonic-to-wicki) (:velocity-f e)))
          ::wicki-on)

(on-event [:midi :note-off]
          (fn [e] (midi-off (-> e :note harmonic-to-wicki)))
          ::wicki-off)

#_ (remove-event-handler ::wicki-on)
#_ (remove-event-handler ::wicki-off)
