(ns overdemo.sounds
  (:use [overtone.live]))

(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4]
  (* (env-gen (env-lin attack sustain release) 1 1 0 1 FREE)
     (saw freq)
     vol))

#_ (saw-wave)
#_ (kill saw-wave)

(definst spooky-house [freq 440 width 0.2
                       attack 0.3 sustain 4 release 0.3
                       vol 0.4]
  (* (env-gen (env-lin attack sustain release) 1 1 0 1 FREE)
     (sin-osc (+ freq (* 20 (lf-pulse:kr 0.5 0 width))))
     vol))

#_ (spooky-house)
#_ (kill spooky-house)

(defcgen bell-partials [freq dur partials]
  (:ar (apply + (map (fn [partial proportion]
                       (let [env      (env-gen (perc 0.01 (* dur proportion)))
                             vol      (/ proportion 2)
                             overtone (* partial freq)]
                         (* env vol (sin-osc overtone))))
                     partials
                     (iterate #(/ % 2) 1.0)))))

(definst pretty-bell [freq 220 dur 1.0 amp 1.0]
  (let [snd (* amp (bell-partials freq dur [0.5 1 3 4.2 5.4 6.8]))]
    (detect-silence snd :action FREE)
    snd))

#_ (doseq [hz [440 660 880]]
     (pretty-bell hz))

(definst space-theremin
  [out-bus 0 freq 440 amp 0.1 gate 1 lfo-rate 6 lfo-width 0.5
   cutoff 4000 rq 0.25 lag-time 0.1 pan 0]
  (let [lfo    (lf-tri:kr (+ lfo-rate (mul-add (lf-noise1:kr 5) 0.3 0.3)) (rand 2))
        osc    (* 0.5 (saw (midicps (+ (cpsmidi (lag freq lag-time))
                                       (* lfo lfo-width)))))
        filter (b-low-pass4 osc (lag cutoff (* lag-time 4)) rq)
        env    (env-gen:ar (adsr 0.6 0 1 0.05) gate FREE)]
    (out out-bus (pan2 (* filter env (lag amp (* 4 lag-time))) pan))))

(defsynth monotron
  [note 60 volume 0.7 mod_pitch_not_cutoff 1 pitch 0.0
   rate 4.0 int 1.0 cutoff 1000.0 peak 0.5 pan 0]
  (let [note_freq       (midicps note)
        pitch_mod_coef  mod_pitch_not_cutoff
        cutoff_mod_coef (- 1 mod_pitch_not_cutoff)
        LFO             (* int (saw rate))
        VCO             (saw (+ note_freq pitch (* pitch_mod_coef LFO)))
        vcf_freq        (+ cutoff (* cutoff_mod_coef LFO) note_freq)
        VCF             (moog-ff VCO vcf_freq peak)
        ]
    (out 0 (pan2 (* volume VCF) pan))))

(comment
  (def N0 (monotron 40 0.8 1 0.0 2.5 350.0 800.0 3.0))

  (ctl N0 :note   60)               ;; midi note value: 0 to 127
  (ctl N0 :volume 0.7)              ;; gain of the output: 0.0 to 1.0
  (ctl N0 :mod_pitch_not_cutoff 0)  ;; use 0 or 1 only to select LFO pitch or cutoff modification
  (ctl N0 :pitch  10.0)             ;; this + note is frequency of the VCO
  (ctl N0 :rate   1.5)              ;; frequency of the LFO
  (ctl N0 :int    800.0)            ;; intensity of the LFO
  (ctl N0 :cutoff 600.0)            ;; cutoff frequency of the VCF
  (ctl N0 :peak   0.5)              ;; VCF peak control (resonance) 0.0 to 4.0
  )
