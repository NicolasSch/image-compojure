(ns graphics2d-enclojed.grammer
  (:import (java.awt Color Font)
           (java.awt.image BufferedImage)))



;BEISPIEL ANWENDUNG ZUR VERANSCHAULICHUNG DER GRAMMATIK

(def colorDef (create-color :green))
(def colorRGB (create-color [255 255 245 1]))
(def colorHex (create-color :#6C7B8B))
(def font (create-font :serif))


;1. und 2. Argument optional

(create-image (load-image {:size (resize 600 400)
                           :source    "src/test.png"
                           :composite :destination})
              {
               :background          (color :green)
               :antialiasing        :default
               :alpha-interpolation :quality
               :color-rndering      :quality
               :dithering           :enable
               :fractional-metrics  :on
               :image-interpolation :bicubic
               :rendering           :quality
               :stroke-control      :default
               :text-antialiasing   :on

               }

              ;hier angegeben settings werden auf alle folgenden shapes angewendet
              ;kann eine option nicht angewendet werden, wir diese ignoriert z.b. :jojnstyle image
              (draw {:color     colorHex
                     :color     colorRGB
                     :linewidth 0.5
                     :joinstyle :dash
                     :composite :source
                     :fill      false
                     :transform [(shear num 5 / num)
                                 (tranlate x y)
                                 (scale num num)
                                 (rotate num, x, y)
                                 (rotate num)]}
                    (line 1 1 2 2 {:color     colorDef
                                   :linewidth 0.5
                                   :joinstyle :dash
                                   :composite :ource})
                    (rectangle 1 2 5 10 {:color     (. Color/green)
                                         :linewidth 0.5
                                         :joinstyle :dash
                                         :composite :source
                                         :fill      true})
                    (ellipse [20.0, 200.0, 100.0, 100.0])
                    (general-path [(line 1 1 2 2)
                                   (line 2 2 3 3)
                                   (line 3 3 4 4)])
                    (polygon [[x0 xn] [y0 yn] xNum yNum])

                    (string [posx posy] (create-styled-text
                                          {:text         ["DiesIstEinTestText" "Der später über mehrer Zeilen angeziegt" "werden soll"]
                                           :font         (. Font/SERIF)
                                           :strikthrouh  "on"
                                           :underline    "on"
                                           :kerning      "on"
                                           :color        (. Color/black)
                                           :linebreaks   "on"
                                           :antialiasing "on"
                                           :size         5.0}))
                    (image posx posy (load-Image {:source    "src/test.png"
                                                  :crop      (crop :rectangle x1 y1 h w)
                                                  :composite :destination}))))




(render-output {:as   :file
         :clipping shape
         :path "sdkasdlkals.png"})

(render-output  {:as :show})

(render-output  {:as     :send
         :format :JSON})
