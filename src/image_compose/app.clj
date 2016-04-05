(ns graphics2d-enclojed.app
  (:import (java.awt Color Font Dimension Graphics Component Graphics2D)
           (java.awt.geom RoundRectangle2D)
           (javax.swing JFrame)))


(def picture ".\res\testjpg")
(def frame (new JFrame))

; TESTAPPLIKATION


(def a {:a :test})