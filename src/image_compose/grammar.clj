(ns image-compose.grammar
  (:require [image-compose.core :as image-compojure])
  (:import (java.awt.geom RoundRectangle2D RoundRectangle2D$Double Rectangle2D$Double)
           (java.awt Polygon Color)))



(def second-transformation (image-compojure/transform
                             (image-compojure/translate 100 100)
                             (image-compojure/rotate 5)))

(def shapes-vec [(Rectangle2D$Double. 0 0 100 100)
                 (Polygon. (int-array [150 250 325 375 450 275 100])
                           (int-array [150 100 125 225 250 375 300]) 7)])

(def shapes-vec1 [(Polygon. (int-array [150 250 325 375 450 275 100])
                            (int-array [150 100 125 225 250 375 300]) 7)])

(def shapes-vec2 [(Rectangle2D$Double. 0 0 400 400)
                  ])

(def shapes-vec3 [(Rectangle2D$Double. 200 200 400 400)])

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

      (image-compojure/with-transform (image-compojure/transform
                                        (image-compojure/translate 100 100)
                                        (image-compojure/rotate 10)
                                        (image-compojure/shear 100 100)
                                        (image-compojure/scale 100 100))

                                      (image-compojure/line 1 1 2 2 {:paint     :red
                                                                     :linewidth 0.5
                                                                     :joinstyle :dash
                                                                     :composite :src})
                                      (image-compojure/with-transform second-transformation
                                                                      (image-compojure/rectangle 100 100 100 100)
                                                                      (image-compojure/rectangle 50 50 100 100 {:paint     :green
                                                                                                                :linewidth 0.5
                                                                                                                :joinstyle :dash
                                                                                                                :composite :source
                                                                                                                :fill      true})))
      (image-compojure/image 0 0 (image-compojure/load-image "src/test.png"))
      (image-compojure/shapes [(Rectangle2D$Double. 0 0 100 100)
                               (Polygon. (int-array [150 250 325 375 450 275 100])
                                         (int-array [150 100 125 225 250 375 300]) 7)])
      )
    ))

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

(defn image-example []
  (let [image (image-compojure/load-image "src/test.png")]
    (image-compojure/image image 0 0)
    (image-compojure/image image 0 0 500 500 0 0 500 500)
    (image-compojure/image (image-compojure/resize image 500 500) 0 0)
    ))







(defn color-example [shapes1 shapes2 shapes3]
  (let [colors [(image-compojure/color :black) (image-compojure/color 255 0 0 125) (image-compojure/color 0 0 255)]
        texture (image-compojure/texture-paint (image-compojure/load-image "res/txtr.JPG") 0 0 50 50)
        gradient-paint (image-compojure/gradient-paint 0 0 (first colors) 100 100 (second colors))]
    (image-compojure/shapes shapes2 {:fill true :paint (first colors)})
    (image-compojure/shapes shapes3 {:fill true :paint gradient-paint})
    (image-compojure/shapes shapes1 {:fill true :paint texture})))

(defn text-example []
  (image-compojure/background :black)
  (image-compojure/styled-text 50 200 (image-compojure/create-styled-text
                                         "IMAGE-COMPOJURE"
                                         :times :bold 50 {:foreground (image-compojure/color :yellow )
                                                          :background (image-compojure/color :red)
                                                          :kerning false
                                                          :strikethrough false
                                                          :swap-colors false
                                                          :underline :low-on-pixel
                                                          :weight :weight-demibold
                                                          :width :width-condensed
                                                               })))

(defn render-example [image shape-obj]
  (image-compojure/render-output image {:as :file :path "res/output.png" :clipping shape-obj})
  (image-compojure/render-output image {:as :json})
  (image-compojure/render-output image))


(defn test-me []
  (image-compose.core/render-output (image-compose.core/compose 600 600
                                                                (color-example shapes-vec1 shapes-vec2 shapes-vec3)))
  (image-compose.core/render-output (image-compose.core/compose 600 600 {:text-antialiasing :on}
                                                                (text-example))))