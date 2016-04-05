(ns image-compose.unittest)
(:require [graphics2d-enclojed.core :refer :all])

(defn draw-polygone
  (polygon [150 250 325 375 450 275 100] [150 100 125 225 250 375 300] true {:color (create-color :red)})
  (render-output))

(defn draw-rect-image
  (let [a (create-image-from-file "res/bg-1.JPG")]
    (rectangle 100 100 400 400 true)
    (image 0 0 a))
  (render-output))