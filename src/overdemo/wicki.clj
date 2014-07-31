(ns overdemo.wicki)

(defn normalize-index [raw-index]
  (let [index (- raw-index 1)
        row (quot index 7)
        chunks (quot row 2)]
    (if (and
         (= (mod row 2) 0)
         (> chunks 3))
      (+ index 1)
      index)))

(defn harmonic-to-wicki [index]
  (let [index (normalize-index index)
        column (mod index 7)
        row (quot index 7)
        octave (quot row 2)
        row-offset (* (mod row 2) 7)]
    (-> column
        (* 2)
        (+ row-offset)
        (+ 18)
        #_ (+ 24)
        (+ (* 12 octave)))))
