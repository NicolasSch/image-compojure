(ns image-compose.grammar
  (:require [image-compose.core :as image-compojure])
  (:import (java.awt.geom RoundRectangle2D RoundRectangle2D$Double Rectangle2D$Double)
           (java.awt Polygon Color)))



(def transformation (image-compojure/transform
                      (image-compojure/translate 100 100)
                      (image-compojure/rotate 5)))

(def shapes [(Rectangle2D$Double. 0 0 100 100)
             (Polygon. (int-array [150 250 325 375 450 275 100]) (int-array [150 100 125 225 250 375 300]) 7)])


(defn example-one []
  (image-compojure/compose
    800 600
    {:antialiasing         :off
     :aplpha-interpolation :default
     :color-rendering      :quality
     :dithering            :disable
     :fractional-metrics   :on
     :interpolatioin       :bicubic
     :rendering            :default
     :stroke-control       :default
     :text-antialiasing    :default
     }

    (image-compojure/with-shape-settings
      {:width       1.0
       :join        :miter
       :miter-limit 10.0
       :cap         :square
       :dash        nil
       :dash-phase  0
       :composite   :src_over
       :alpha       1.0
       :paint       (image-compojure/color 125 125 125 255)
       :xor-mode    nil
       }
      (image-compojure/with-transform
        (image-compojure/transform
          (image-compojure/translate 100 100)
          (image-compojure/rotate 10)
          (image-compojure/shear 100 100)
          (image-compojure/scale 100 100))
        (image-compojure/line 1 1 2 2 {:paint     :red
                                       :linewidth 0.5
                                       :joinstyle :dash
                                       :composite :src})
        (image-compojure/with-transform
          transformation
          (image-compojure/rectangle 100 100 100 100)))

      (image-compojure/rectangle 50 50 100 100 {:paint     :green
                                                :linewidth 0.5
                                                :joinstyle :dash
                                                :composite :source
                                                :fill      true})
      (image-compojure/image 0 0 (image-compojure/load-image "src/test.png"))
      (image-compojure/shapes shapes)

      (dotimes [n 10]
        (map #(image-compojure/line 50 (* n %) 700 (* n %)) (range 1 11)))

      )))

;normal code in dsl
(defn example []
  (image-compojure/render-output
    (image-compojure/compose 800 2000
                             {:antialiasing :on}
                             (image-compojure/with-shape-settings
                               {:paint :red}
                               (dotimes [n 10]
                                 (doall (map
                                          #(if (= 0 (mod n 2))
                                            (image-compojure/line 400 50 (* n %) 1000)
                                            (image-compojure/line 400 50 (* n %) 1000 {:paint :blue}))
                                          (range 100 106))))))))


