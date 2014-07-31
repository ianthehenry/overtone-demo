(ns overdemo.one
  (:use [overtone.live]))

(definst sin-mouse [left 400 right 500 amp 1]
  (-> (sin-osc (mouse-x left right))
      (* (mouse-y))))

(defn just-triad [root]
  [root
   (* root (/ 4 3))
   (* root (/ 3 2))])

(let [a-major-just (just-triad 440)
      a-major-equal (map (comp midi->hz note)
                         ["A4", "C#5", "E5"])]

  (doseq [[j e] (map vector a-major-just a-major-equal)]
    (sin-mouse j e)))

#_ (kill sin-mouse)
