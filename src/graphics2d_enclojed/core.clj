(ns graphics2d-enclojed.core
  (:import (java.awt Color Font Graphics Graphics2D Dimension Composite BasicStroke RenderingHints Component AlphaComposite)
           (java.awt.font TextAttribute)
           (java.awt.image BufferedImage)
           (javax.swing JFrame)
           (java.awt.geom Rectangle2D Rectangle2D$Double)))


(defn create-color
  ([] (. Color Color/white))
  ([key] (cond
           (= key :green) (Color. Color/green)
           (= key :blue) (. Color Color/blue)
           (= key :red) (. Color Color/red)
           (= key :black) (. Color Color/black)
           (= key :yellow) (. Color Color/yellow)
           (= key :pink) (. Color Color/pink)
           (= key :orange) (. Color Color/orange)
           (= key :blue) (. Color Color/blue)
           ))
  ([r g b] (Color. r g b 1))
  ([r g b a] (Color. r g b a)))

(defn create-font
  [key] (cond
          (= key :serif) (. Font Font/SERIF)))

;(def ^:dynamic default-g2d nil)
;(def ^:dynamic default-image nil)

(def ^:dynamic default-image (BufferedImage. 800 600 BufferedImage/TYPE_INT_ARGB))
(def ^:dynamic default-g2d (.createGraphics default-image))
(def ^:dynamic default-shape-settings {:width       1.0
                                       :join        :miter
                                       :miter-limit 10.0
                                       :cap         :square
                                       :dash        nil
                                       :dash-phase  0
                                       :composite   :src
                                       :alpha       1.0
                                       })

(def default-render-settings {:antialiasing         :off
                              :aplpha-interpolation :default
                              :color-rendering      :quality
                              :dithering            :disable
                              :fractional-metrics   :on
                              :interpolatioin       :bicubic
                              :rendering            :quality
                              :stroke-control       :normalize
                              :text-antialiasing    :off})

(def frame (proxy [JFrame] []
             (paint [#^Graphics g]
               (.drawImage g default-image 0 0 nil))))

(def stroke-caps {:butt   (BasicStroke/CAP_BUTT)
                  :round  (BasicStroke/CAP_ROUND)
                  :square (BasicStroke/CAP_SQUARE)})
(def stroke-joins {:bevel (BasicStroke/JOIN_BEVEL)
                   :round (BasicStroke/JOIN_ROUND)
                   :miter (BasicStroke/JOIN_MITER)})

(def composite-rules {:src      (AlphaComposite/SRC)
                      :dst_in   (AlphaComposite/DST_IN)
                      :dst_out  (AlphaComposite/DST_OUT)
                      :dst_over (AlphaComposite/DST_OVER)
                      :src_in   (AlphaComposite/DST_OVER)
                      :src_out  (AlphaComposite/SRC_OUT)
                      :src_over (AlphaComposite/SRC_OVER)
                      :clear    (AlphaComposite/CLEAR)})

(def keys-antialiasing {:on      (RenderingHints/VALUE_ANTIALIAS_ON)
                        :off     (RenderingHints/VALUE_ANTIALIAS_OFF)
                        :default (RenderingHints/VALUE_ANTIALIAS_DEFAULT)})

(def keys-alpha-interpolation {:quality (RenderingHints/VALUE_ALPHA_INTERPOLATION_QUALITY)
                               :speed   (RenderingHints/VALUE_ALPHA_INTERPOLATION_SPEED)
                               :default (RenderingHints/VALUE_ALPHA_INTERPOLATION_DEFAULT)})

(def keys-color-rendering {:quality (RenderingHints/VALUE_COLOR_RENDER_QUALITY)
                           :speed   (RenderingHints/VALUE_COLOR_RENDER_SPEED)
                           :dfault  (RenderingHints/VALUE_COLOR_RENDER_DEFAULT)})

(def keys-dithering {:disable (RenderingHints/VALUE_DITHER_DISABLE)
                     :enable  (RenderingHints/VALUE_DITHER_ENABLE)
                     :default (RenderingHints/VALUE_DITHER_DEFAULT)})

(def keys-fractional-metrics {:on      (RenderingHints/VALUE_FRACTIONALMETRICS_ON)
                              :off     (RenderingHints/VALUE_FRACTIONALMETRICS_OFF)
                              :default (RenderingHints/VALUE_FRACTIONALMETRICS_DEFAULT)})

(def keys-interpolation {:bicubic  (RenderingHints/VALUE_INTERPOLATION_BICUBIC)
                         :bilinear (RenderingHints/VALUE_INTERPOLATION_BILINEAR)
                         :neighbor (RenderingHints/VALUE_INTERPOLATION_NEAREST_NEIGHBOR)})

(def keys-rendering {:quality (RenderingHints/VALUE_RENDER_QUALITY)
                     :speed   (RenderingHints/VALUE_RENDER_SPEED)
                     :default (RenderingHints/VALUE_RENDER_DEFAULT)})

(def keys-stroke-control {:normalize (RenderingHints/VALUE_STROKE_NORMALIZE)
                           :default   (RenderingHints/VALUE_STROKE_DEFAULT)
                           :pure      (RenderingHints/VALUE_STROKE_PURE)})

(def keys-text-antialiasing {:on       (RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
                              :off      (RenderingHints/VALUE_TEXT_ANTIALIAS_OFF)
                              :default  (RenderingHints/VALUE_TEXT_ANTIALIAS_DEFAULT)
                              :gasp     (RenderingHints/VALUE_TEXT_ANTIALIAS_GASP)
                              :lcd-hrgb (RenderingHints/VALUE_TEXT_ANTIALIAS_LCD_HRGB)
                              :hbgr     (RenderingHints/VALUE_TEXT_ANTIALIAS_LCD_HBGR)
                              :lcd-vrgb (RenderingHints/VALUE_TEXT_ANTIALIAS_LCD_VRGB)
                              :lcd-vgbr (RenderingHints/VALUE_TEXT_ANTIALIAS_LCD_VBGR)})



(defn create-styled-text [options]
  (let [text ["zu schreibenden texte"]
        textAttributes "hashmap defineiren nach argumenten"]))

;transformation
(defn shear [num num])

(defn tranlate [x y])

(defn scale [num num])

(defn rotate
  ([num])
  ([num, x, y]))

(defn rotate [num])

(defn load-image [source & options])

(defn crop [image method & size])

(defn resize [h w])

(defn hit [x1 y1 x2 y2])

(defn write-string! [bufferedImage x1 y1 x2 y2 text])

(defn line
  ([image x1 y1 x2 y2 options]))

(defn ellipse [bufferedImage x1 y1 x2 y2 options])

(defn polygone [bufferedImage points options])

(defn generalpath [bufferedImage functions options])

(defn image [bufferedImage x y ximageToInsert])



(defn set-stroke
  [width cap join miter-limit dash dash-phase]
  (let [stroke (BasicStroke. width (cap stroke-caps) (join stroke-joins) miter-limit dash dash-phase)]
    (.setStroke default-g2d stroke)))

(defn set-color [color]
  (.setColor default-g2d color))

(defn set-composite [comp alpha]
  (let [alpha-composite (. AlphaComposite getInstance (comp composite-rules) alpha)]
    (.setComposite default-g2d alpha-composite)))

(defn set-shape-settings
  ([{:keys [width cap join miter-limit dash dash-phase composite alpha color] :or
           {width       1.0
            join        :miter
            miter-limit 10.0
            cap         :square
            dash        nil
            dash-phase  0
            composite   :src
            alpha       1.0}}]
   (set-stroke width cap join miter-limit dash dash-phase)
   (set-color color)
   (set-composite composite alpha)
    )
  ([]
   (set-shape-settings {})))


(defn set-rendering-hints [{:keys [antialiasing aplpha-interpolation color-rendering dithering fractional-metrics interpolatioin rendering stroke-control text-antialiasing] :or
                                  {antialiasing :off
                                   aplpha-interpolation :default
                                   color-rendering :quality
                                   dithering :disable
                                   fractional-metrics :on
                                   interpolatioin :bicubic
                                   rendering :quality
                                   stroke-control :normalize
                                   text-antialiasing :off}}]
  (let [rendering-hints {(RenderingHints/KEY_ANTIALIASING) (antialiasing keys-antialiasing)
                         (RenderingHints/KEY_ALPHA_INTERPOLATION) (aplpha-interpolation keys-alpha-interpolation)
                         (RenderingHints/KEY_COLOR_RENDERING) (color-rendering keys-color-rendering)
                         (RenderingHints/KEY_DITHERING) (dithering keys-dithering)
                         (RenderingHints/KEY_FRACTIONALMETRICS) (fractional-metrics keys-fractional-metrics)
                         (RenderingHints/KEY_INTERPOLATION) (interpolatioin keys-interpolation)
                         (RenderingHints/KEY_RENDERING) (rendering keys-rendering)
                         (RenderingHints/KEY_STROKE_CONTROL) (stroke-control keys-stroke-control)
                         (RenderingHints/KEY_TEXT_ANTIALIASING) (text-antialiasing keys-text-antialiasing)}]
    (.setRenderingHints default-g2d rendering-hints)))

(defn repaint []
  (.repaint frame))

(defn show-image []
  (let [dimensison (Dimension. (.getWidth default-image) (.getHeight default-image))]
    (doto frame
      (.setSize dimensison)
      (.setVisible true))))

(defn rectangle
  ([x y w h]
   (let [rectangle (Rectangle2D$Double. x y w h)]
     (.draw default-g2d rectangle))
    )
  ([x y w h fill]
   (let [rectangle (Rectangle2D$Double. x y w h)]
     (if (= fill :fill)
       (.fill default-g2d rectangle)
       (.draw default-g2d rectangle))
     ))
  ([x y w h fill settings]
   (set-shape-settings settings)
   (rectangle x y w h fill)))

(defn set-background [color]
  (rectangle 0 0 (.getWidth default-image) (.getHeight default-image) :fill {:composite :src :color color}))

(defn render-output
  ([{:keys [as path clipping format]}]
   (if (= as :show)
     (do
       (println "Das Bild wird nun in einem JFrame in der größe des BufferedImage angezeigt")
       (show-image))))
  ([]
   (render-output {:as :show}))
  )

;aufruf nur aus macro "draw" möglich
(defmacro transform [& functions])

(defmacro create-image
  "Creates an BufferedImage with given size."
  [w h & body]
  `(let [image# (BufferedImage. ~w ~h BufferedImage/TYPE_INT_ARGB)]
     (binding [default-image image#
               default-g2d (.createGraphics image#)]
       (set-rendering-hints ~default-render-settings)
       (do ~@body))))



;bufferedImage erstellen
;functionen drauf anwenden

(defmacro draw [bufferedImage options & functions]
  ;functionen drauf anwenden
  ())


;Hilfsfunktionen
