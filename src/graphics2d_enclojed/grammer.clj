(ns graphics2d-enclojed.grammer
  (:import (java.awt Color Font)
           (java.awt.image BufferedImage)))



;BEISPIEL ANWENDUNG ZUR VERANSCHAULICHUNG DER GRAMMATIK

(def colorDef (color :green))
(def colorRGB (color [255 255 245 1]))
(def colorHex (color :#6C7B8B))
(def font (font :serif))


;1. und 2. Argument optional

(create-image (load-image {:source    "src/test.png"
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
               :clipping            shape
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
                    (oval)
                    (arc)
                    (polygon)
                    (poly-line)
                    (round-rectangle [1 2 5 10])
                    (oval)
                    (arc)
                    (clear-rectangle)


                    (string [posx posy] (styled-text
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



;definiert den Anzeigebereich der Leinwand
(clip shape)

;hit detection
(hit [x y])

(crop)

(render {:as   :file
         :path "sdkasdlkals.png"})

(render {:as :show})

(render {:as     :send
         :format :JSON})
