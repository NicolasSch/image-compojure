(ns image-compose.unittest
  (:import (java.awt.geom Rectangle2D$Double))
  (:require [image-compose.core :refer :all]))

(defn test-all []
  (line 100 100 200 200 {:color (color :blue)})
  (oval 0 0 100 100 {:color (color :red)}))



(defn test-shapes-func []
  (let [shapes1 [(Rectangle2D$Double. 0 0 200 200)
                (Rectangle2D$Double. 200 200 500 500)]]
    (shapes shapes1 true {:color (color :blue)}))
  (repaint)
  (render-output))

(defn draw-polygone []
  (polygon [150 250 325 375 450 275 100] [150 100 125 225 250 375 300] true {:color (color :red)})
  (render-output))

(defn draw-rect-image []
  (let [a (load-image "res/bg-1.JPG")]
    (rectangle 100 100 400 400 true)
    (image 0 0 a))
  (repaint)
  (render-output))