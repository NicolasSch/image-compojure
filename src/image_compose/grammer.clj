(ns image-compose.grammer
  (:import (java.awt Color Font)
           (java.awt.image BufferedImage))
  (:require [image-compose.core :as img]))



;BEISPIEL ANWENDUNG ZUR VERANSCHAULICHUNG DER GRAMMATIK

(def colorDef (img/create-color :green))
(def colorRGB (img/create-color [255 255 245 1]))
(def colorHex (img/create-color :#6C7B8B))

;1. und 2. Argument optional

(img/compose (img/load-image "src/test.png")
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
             (img/with-shape-settings {:color     colorHex
                                       :linewidth 0.5
                                       :joinstyle :dash
                                       :composite :source}
                                      (line 1 1 2 2 {:color     colorDef
                                                     :linewidth 0.5
                                                     :joinstyle :dash
                                                     :composite :src})
                                      (rectangle 50 50 100 100 true {:color     (. Color/green)
                                                                     :linewidth 0.5
                                                                     :joinstyle :dash
                                                                     :composite :source
                                                                     :fill      true})
                                      (image posx posy (img/load-image {:source    "src/test.png"
                                                                        :crop      (crop :rectangle x1 y1 h w)
                                                                        :composite :destination}))))




(render-output {:as       :file
                :clipping shape
                :path     "sdkasdlkals.png"})

(render-output {:as :show})

(render-output {:as     :send
                :format :JSON})
