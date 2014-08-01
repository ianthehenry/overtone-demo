(ns overdemo.effects
  (:use [overtone.live])
  (:use [overtone.studio.scope]))

#_ (scope :bus 0)
#_ (scope :bus 8)

(definst echo []
  (let [guitar (in (num-output-buses))]
    (* guitar (sin-osc:ar (mouse-x 0 20)))))

#_ (echo)
#_ (kill echo)

#_ (inst-fx! echo fx-echo)
#_ (clear-fx echo)
