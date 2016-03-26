(ns graphics2d-enclojed.core
  (:import (java.awt Color Font Graphics Graphics2D Dimension Composite BasicStroke RenderingHints AlphaComposite)
           (java.awt.font TextAttribute)
           (java.awt.image BufferedImage RescaleOp)
           (javax.swing JFrame)
           (java.awt.geom Rectangle2D$Double Line2D$Double AffineTransform)
           (javax.imageio ImageIO)
           (java.io File)))


(defmacro when->
  {:added "1.0"}
  [x & forms]
  (if-not (= 0 (mod (count forms) 2))
    (throw (RuntimeException.
             "The when-> macro must contain a even number of arguments")))
  (loop [x x, forms forms]
    ;(if-not (= java.lang.Boolean (first forms))
    ;  (throw (RuntimeException.
    ;           "Wrong Type. Use boolean for first und function for second argument")))
    (if forms
      (let [test (first forms)
            form (second forms)
            threaded (if (seq? form)
                       (with-meta `(if ~test (~(first form) ~x ~@(next form))
                                             ~x) (meta form))
                       (list form x))]
        (recur threaded (next (next forms))))
      x)))

(defn create-color
  ([] (. Color Color/white))
  ([key] (cond
           (= key :green) (. Color Color/green)
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



;(def ^:dynamic default-g2d nil)
;(def ^:dynamic default-image nil)

(def ^:dynamic default-image (BufferedImage. 800 600 BufferedImage/TYPE_INT_ARGB))
(def ^:dynamic default-g2d (.createGraphics default-image))
(def ^:dynamic default-shape-values {:width       1.0
                                     :join        :miter
                                     :miter-limit 10.0
                                     :cap         :square
                                     :dash        nil
                                     :dash-phase  0
                                     :composite   :src
                                     :alpha       1.0
                                     :color       (create-color :black)
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

(def font-styles {:plain            (Font/PLAIN)
                  :bold             (Font/BOLD)
                  :italic           (Font/ITALIC)
                  :roman-baseline   (Font/ROMAN_BASELINE)
                  :center-baseline  (Font/CENTER_BASELINE)
                  :hanging-baseline (Font/HANGING_BASELINE)
                  :true-type-font   (Font/TRUETYPE_FONT)
                  :type1-font       (Font/TYPE1_FONT)})

(def fonts {:dialog       (Font/DIALOG)
            :dialog-input (Font/DIALOG_INPUT)
            :sans-serif   (Font/SANS_SERIF)
            :serif        (Font/SERIF)
            :momospaced   (Font/MONOSPACED)
            :times        "Times New Roman"})

(def text-weight {
                  :extra-light (TextAttribute/WEIGHT_EXTRA_LIGHT)
                  :light       (TextAttribute/WEIGHT_LIGHT)
                  :demilight   (TextAttribute/WEIGHT_DEMILIGHT)
                  :regular     (TextAttribute/WEIGHT_REGULAR)
                  :medium      (TextAttribute/WEIGHT_MEDIUM)
                  :semibold    (TextAttribute/WEIGHT_SEMIBOLD)
                  :demibold    (TextAttribute/WEIGHT_DEMIBOLD)
                  :bold        (TextAttribute/WEIGHT_BOLD)
                  :heavy       (TextAttribute/WEIGHT_HEAVY)
                  :extrabold   (TextAttribute/WEIGHT_EXTRABOLD)
                  :ultrabold   (TextAttribute/WEIGHT_ULTRABOLD)})
(def text-width {
                 :condensed      (TextAttribute/WIDTH_CONDENSED)
                 :semi-condensed (TextAttribute/WIDTH_SEMI_CONDENSED)
                 :regular        (TextAttribute/WIDTH_REGULAR)
                 :semi-extended  (TextAttribute/WIDTH_SEMI_EXTENDED)
                 :extended       (TextAttribute/WIDTH_EXTENDED)})

(def text-posture {:regular (TextAttribute/POSTURE_REGULAR)
                   :onlique (TextAttribute/POSTURE_OBLIQUE)})

(def text-underline {:low-on-pixel  (TextAttribute/UNDERLINE_LOW_ONE_PIXEL)
                     :low-two-pixel (TextAttribute/UNDERLINE_LOW_TWO_PIXEL)
                     :low-dotted    (TextAttribute/UNDERLINE_LOW_DOTTED)
                     :low-gray      (TextAttribute/UNDERLINE_LOW_GRAY)
                     :low-dashed    (TextAttribute/UNDERLINE_LOW_DASHED)})


(def keys-text-antialiasing {:on       (RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
                             :off      (RenderingHints/VALUE_TEXT_ANTIALIAS_OFF)
                             :default  (RenderingHints/VALUE_TEXT_ANTIALIAS_DEFAULT)
                             :gasp     (RenderingHints/VALUE_TEXT_ANTIALIAS_GASP)
                             :lcd-hrgb (RenderingHints/VALUE_TEXT_ANTIALIAS_LCD_HRGB)
                             :hbgr     (RenderingHints/VALUE_TEXT_ANTIALIAS_LCD_HBGR)
                             :lcd-vrgb (RenderingHints/VALUE_TEXT_ANTIALIAS_LCD_VRGB)
                             :lcd-vgbr (RenderingHints/VALUE_TEXT_ANTIALIAS_LCD_VBGR)})


(defn create-font
  [name style size]
  (Font. (name fonts) (style font-styles) size)
  )

(defn create-styled-text [text font {:keys [weight width underline foreground background strike-through swap-colors kerning]}]
  (println kerning)
  (let [styled-font (when-> {}
                            kerning (assoc (TextAttribute/KERNING) (TextAttribute/KERNING_ON))
                            strike-through (assoc (TextAttribute/STRIKETHROUGH) (TextAttribute/STRIKETHROUGH_ON))
                            swap-colors (assoc (TextAttribute/SWAP_COLORS) (TextAttribute/SWAP_COLORS_ON))
                            width (assoc (TextAttribute/WIDTH) (width text-width))
                            weight (assoc (TextAttribute/WEIGHT) (weight text-weight))
                            underline (assoc (TextAttribute/UNDERLINE) (underline text-underline))
                            foreground (assoc (TextAttribute/FOREGROUND) foreground)
                            background (assoc (TextAttribute/BACKGROUND) background))]

    (-> {}
        (assoc :text text)
        (assoc :font (.deriveFont font styled-font)))))


(defn set-stroke
  [width cap join miter-limit dash dash-phase]
  (let [stroke (BasicStroke. width (cap stroke-caps) (join stroke-joins) miter-limit dash dash-phase)]
    (.setStroke default-g2d stroke)))

(defn set-color [color]
  (.setColor default-g2d color))

(defn set-font [font]
  (.setFont default-g2d font))

(defn set-composite [comp alpha]
  (let [alpha-composite (. AlphaComposite getInstance (comp composite-rules) alpha)]
    (.setComposite default-g2d alpha-composite)))

(defn set-shape-settings
  ([{:keys [width cap join miter-limit dash dash-phase composite alpha color]}]
   (if (or width cap join miter-limit dash dash-phase)
     (let [width (or width (:width default-shape-values))
           cap (or cap (:cap default-shape-values))
           join (or join (:join default-shape-values))
           miter-limit (or miter-limit (:miter-limit default-shape-values))
           dash (or dash (:dash default-shape-values))
           dash-phase (or dash-phase (:dash-phase default-shape-values))]
       (println width cap join miter-limit dash dash-phase composite alpha color)
       (set-stroke width cap join miter-limit dash dash-phase)
       ))
   (if color
     (set-color color))
   (if composite
     (let [alpha (or alpha (:alpha default-shape-values))]
       (set-composite composite alpha)))
    )
  ([]
   (set-shape-settings default-shape-values)))

(defn reset-shape-settings []
  (set-shape-settings))

(defn set-rendering-hints [{:keys [antialiasing aplpha-interpolation color-rendering dithering fractional-metrics interpolatioin rendering stroke-control text-antialiasing] :or
                                  {antialiasing         :off
                                   aplpha-interpolation :default
                                   color-rendering      :quality
                                   dithering            :disable
                                   fractional-metrics   :on
                                   interpolatioin       :bicubic
                                   rendering            :quality
                                   stroke-control       :normalize
                                   text-antialiasing    :off}}]
  (let [rendering-hints {(RenderingHints/KEY_ANTIALIASING)        (antialiasing keys-antialiasing)
                         (RenderingHints/KEY_ALPHA_INTERPOLATION) (aplpha-interpolation keys-alpha-interpolation)
                         (RenderingHints/KEY_COLOR_RENDERING)     (color-rendering keys-color-rendering)
                         (RenderingHints/KEY_DITHERING)           (dithering keys-dithering)
                         (RenderingHints/KEY_FRACTIONALMETRICS)   (fractional-metrics keys-fractional-metrics)
                         (RenderingHints/KEY_INTERPOLATION)       (interpolatioin keys-interpolation)
                         (RenderingHints/KEY_RENDERING)           (rendering keys-rendering)
                         (RenderingHints/KEY_STROKE_CONTROL)      (stroke-control keys-stroke-control)
                         (RenderingHints/KEY_TEXT_ANTIALIASING)   (text-antialiasing keys-text-antialiasing)}]
    (.setRenderingHints default-g2d rendering-hints)))

(defn repaint []
  (.repaint frame))

(defn show-image []
  (let [dimensison (Dimension. (.getWidth default-image) (.getHeight default-image))]
    (doto frame
      (.setSize dimensison)
      (.setVisible true))))

(defn styled-text [x y styled-text]
  (let [old-font (.getFont default-g2d)]
    (.setFont default-g2d (:font styled-text))
    (.drawString default-g2d (:text styled-text) x y)
    (.setFont default-g2d old-font)
    ))


(defn line
  ([x1 y1 x2 y2]
   (.draw default-g2d (Line2D$Double. x1 y1 x2 y2)))
  ([x1 y1 x2 y2 settings]
   (set-shape-settings settings)
   (line x1 y1 x2 y2)
   (reset-shape-settings))
  )

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
   (rectangle x y w h fill)
   (reset-shape-settings)))

(defn set-background [color]
  (rectangle 0 0 (.getWidth default-image) (.getHeight default-image) :fill {:composite :src :color color}))

(defn create-scaleOp [& rgba]
  (RescaleOp. (float-array rgba) (float-array 4) nil))

(defn image
  ([x y img]
   (image x y img {}))

  ([x y img settings]
   (if (:composite settings)
     (set-shape-settings settings))
   (if filter
     (.drawImage default-g2d img (:filter settings) x y)
     (.drawImage default-g2d img x y nil))
   (reset-shape-settings))

  ([img x1dest y1dest x2dest y2dest x1src y1src x2src y2src]
   (image x1dest y1dest x2dest y2dest x1src y1src x2src y2src img {}))

  ([img x1dest y1dest x2dest y2dest x1src y1src x2src y2src settings]
   (if (:composite settings)
     (set-shape-settings settings))
   (.drawImage default-g2d x1dest y1dest x2dest y2dest x1src y1src x2src y2src img nil)
   (reset-shape-settings)))

(defn load-image [source]
  (ImageIO/read (File. source)))


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

(defmacro transform-with-settings [])

(defmacro draw-with-settings [bufferedImage options & functions]
  ;functionen drauf anwenden
  ())


;transformation
(defn shear [num num])

(defn tranlate [x y])

(defn scale [num num]
  )

(defn rotate
  ([num])
  ([num, x, y]))

(defn rotate [num])

(defn crop [image method & size])

(defn resize [h w])

(defn hit [x1 y1 x2 y2])

(defn write-string! [bufferedImage x1 y1 x2 y2 text])

(defn ellipse [bufferedImage x1 y1 x2 y2 options])

(defn polygone [bufferedImage points options])

(defn generalpath [bufferedImage functions options])



