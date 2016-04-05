(ns image-compose.core
  (:require [clojure.data.codec.base64 :as b64])
  (:import (java.awt Color Font Graphics Graphics2D Dimension Composite BasicStroke RenderingHints AlphaComposite Polygon)
           (java.awt.font TextAttribute)
           (java.awt.image BufferedImage RescaleOp)
           (javax.swing JFrame)
           (java.awt.geom Rectangle2D$Double Line2D$Double AffineTransform GeneralPath)
           (javax.imageio ImageIO)
           (java.io File ByteArrayOutputStream)))


(declare set-background)
(declare rectangle)
(declare set-shape-settings)
(declare reset-shape-settings)


(defmacro draw-fill-reset
  "Convenience macro not to call set-shape-setting and reset-shape-settings everytime a shape was drawn to default-g2d-object."
  [settings & body]
  (if (not-empty settings)
    `(do
       (set-shape-settings ~'settings)
       ~@body
       (reset-shape-settings))
    `(do ~@body))
  )

(defmacro when->
  "Similar to -> but checks logical truth of first form. Calls second form on object."
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
  "Returns Java.awt.Color Object. Can be called with key to get predefiend Java Colors or R G B a values 0-1"
  ([] (. Color Color/white))
  ([key] (cond
           (= key :green) (. Color Color/GREEN)
           (= key :blue) (. Color Color/BLUE)
           (= key :red) (. Color Color/RED)
           (= key :black) (. Color Color/BLACK)
           (= key :yellow) (. Color Color/YELLOW)
           (= key :pink) (. Color Color/PINK)
           (= key :orange) (. Color Color/ORANGE)
           (= key :magenta) (. Color Color/MAGENTA)
           (= key :light-grey) (. Color Color/LIGHT_GRAY)
           (= key :dark-gray) (. Color Color/DARK_GRAY)
           (= key :cyan) (. Color Color/CYAN)
           ))
  ([r g b] (Color. r g b 1))
  ([r g b a] (Color. r g b a)))


(def ^:dynamic default-image (BufferedImage. 1920 1080 BufferedImage/TYPE_INT_ARGB))
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
                              :text-antialiasing    :off
                              :background           (create-color :white)})

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


(defn load-image [source]
  "Creates a BufferedImage from a defined source"
  (ImageIO/read (File. source)))

(defn create-font
  "Returns a logical Font which can be used on any Java platform. Font must be defiend in fonts map"
  ([name style size]
   (Font. (name fonts) (style font-styles) size)
    ))

(defn create-styled-text [text {:keys [name style size weight width underline foreground background strike-through swap-colors kerning]}]
  "Creates a map containing the text which shall be drawn to the default-g2d object as String and a font defined by
  FontfamilyName, Style, Size and Textattributes
  For available FontFamilys and styles see defiend maps 'fonts' and 'font-styles'
  Available Textatrributes can be seen in maps: text-weight, text-width, text-posture, text-underline.
  Furthermore fonts can be defiend by the keys: :kerning bool, :swap-colors bool , :foreground Color, :background Color,
  :name String(FontFamilyName), style int"
  (let [font (create-font name style size)
        styled-font (when-> {}
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

(defn create-scaleOp [& rgba]
  "Creates a RescaleOp which can be used to change transparecny of an image when passed to image function"
  (RescaleOp. (float-array rgba) (float-array 4) nil))

(defn set-background [color]
  "Is called on compose macro when background key in settings map is defiend"
  (rectangle 0 0 (.getWidth default-image) (.getHeight default-image) true {:composite :src :color color}))

(defn set-stroke
  "Sets stroke attributes on default-g2d object"
  [width cap join miter-limit dash dash-phase]
  (let [stroke (BasicStroke. width (cap stroke-caps) (join stroke-joins) miter-limit dash dash-phase)]
    (.setStroke default-g2d stroke)))

(defn set-color [color]
  "Sets paint color on default-g2d object "
  (.setColor default-g2d color))

(defn set-font [font]
  "Sets font on default-g2d object"
  (.setFont default-g2d font))

(defn set-composite [comp alpha]
  "Sets composite attribute on default-g2d object"
  (let [alpha-composite (. AlphaComposite getInstance (comp composite-rules) alpha)]
    (.setComposite default-g2d alpha-composite)))

(defn set-rendering-hints
  "Is called on compose macro to set renderinghints on default-g2d object"
  [{:keys [antialiasing aplpha-interpolation color-rendering dithering fractional-metrics interpolatioin rendering stroke-control text-antialiasing background] :or
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
    (.setRenderingHints default-g2d rendering-hints)
    (if background
      (set-background background))))

(defn set-shape-settings
  "Sets attributes for stroke, color and composite to default-g2d object."
  ([{:keys [width cap join miter-limit dash dash-phase composite alpha color]}]
   (if (or width cap join miter-limit dash dash-phase)
     (let [width (or width (:width default-shape-values))
           cap (or cap (:cap default-shape-values))
           join (or join (:join default-shape-values))
           miter-limit (or miter-limit (:miter-limit default-shape-values))
           dash (if dash
                  (float-array dash)
                  (:dash default-shape-values))
           dash-phase (or dash-phase (:dash-phase default-shape-values))]
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
  "Calls on set-shape-settings to reset shape-settings back to defined default-shape-settings"
  (set-shape-settings))


(defn repaint []
  "Repaint default-image in displayed JFrame"
  (.repaint frame))

(defn show-image []
  "Show default-image in Jframe"
  (let [dimensison (Dimension. (.getWidth default-image) (.getHeight default-image))]
    (doto frame
      (.setSize dimensison)
      (.setVisible true))))

(defn styled-text [x y styled-text]
  "Draws previously created styledtext map to default-g2d object"
  (let [old-font (.getFont default-g2d)]
    (.setFont default-g2d (:font styled-text))
    (.drawString default-g2d (:text styled-text) x y)
    (.setFont default-g2d old-font)
    ))


(defn line
  "Draws line to default-g2d object. May be called with settings map to set shape settings. Settings will be restored to
  default-shape-values after drawing"
  ([x1 y1 x2 y2]
   (.draw default-g2d (Line2D$Double. x1 y1 x2 y2)))
  ([x1 y1 x2 y2 settings]
   (set-shape-settings settings)
   (line x1 y1 x2 y2)
   (reset-shape-settings))
  )

(defn rectangle
  "Draws a rectangle to defaul-g2d object. May be called with settings map to set shape settings. Settings will be restored to
  default-shape-values after drawing process finished."
  ([x y w h fill]
   (let [rectangle (Rectangle2D$Double. x y w h)]
     (if (= fill true)
       (.fill default-g2d rectangle)
       (.draw default-g2d rectangle))
     ))
  ([x y w h fill settings]
   (set-shape-settings settings)
   (rectangle x y w h fill)
   (reset-shape-settings)))


(defn oval
  "Draws an oval to default-g2d-object. May be called with settings map to set shape settings. Settings will be restored to
  default-shape-values after drawing process finished."
  ([x y w h fill settings]
   (draw-fill-reset (eval settings)
                    (if fill
                      (.fillOval default-g2d x y w h)
                      (.drawOval default-g2d x y w h))))
  ([x y w h fill]
   (oval x y w h fill {})))


(defn polygon
  "Draws a polygon to the default-g2d object. May be called with shape settings. Settings will be restored after drawing process.
  Takes sequences of x, y coordinates and the number of points the polygon consists of"
  ([x y fill settings]
   (if-not (= (count x) (count y))
     (throw (RuntimeException.
              "Number of x and y coordinates must be equal")))
   (draw-fill-reset (eval settings)
                    (if fill
                      (.drawPolygon default-g2d (int-array x) (int-array y) (count x))
                      (.fillPolygon default-g2d x y (count x)))))
  ([x y fill]
   (polygon x y fill {})))


(defn shape
  "Draws shape object to default-g2d object. Can be called with shape settings map to overide defualt-shape-settings.
  Shape settings will be restored when passed to function."
  ([shape fill settings]
   (draw-fill-reset (eval settings)
                    (if fill
                      (.fill default-g2d shape)
                      (.draw default-g2d shape))))
  ([shape fill]
   (shape shape fill {})))


(defn image
  "Draws a BufferedImage into default-image. Can be called with a settings map to define composite attribute.
   Setting will be restored to default-shape-settings
   May also scale image to fit into wanted area.
   Settings attribute :filter takes an ScaleOp Object which can be defined by function create-scaleop"
  ([x y img]
   (image x y img {}))

  ([x y img settings]
   (let [filter (:filter settings)]
     (draw-fill-reset (eval settings)
                      (if filter
                        (.drawImage default-g2d img filter x y)
                        (.drawImage default-g2d img x y nil)))))

  ([img x1dest y1dest x2dest y2dest x1src y1src x2src y2src]
   (image x1dest y1dest x2dest y2dest x1src y1src x2src y2src img {}))

  ([img x1dest y1dest x2dest y2dest x1src y1src x2src y2src settings]
   (draw-fill-reset (eval settings)
                    (.drawImage default-g2d x1dest y1dest x2dest y2dest x1src y1src x2src y2src img nil))))

(defn save-image [path]
  "Stores default-image into file. Available formats are gif jpeg and png"
  (let [file (File. path)
        format (last (clojure.string/split path #"\."))]
    (ImageIO/write default-image format file)))

(defn to-json
  "Converts default-iamge to Base64 JSON"
  []
  (let [baos (new ByteArrayOutputStream)]
    (ImageIO/write default-image "jpeg" baos)
    (let [bytes (.toByteArray baos)]
      (clojure.string/replace (String. (b64/encode bytes)) #"\+|/" {"+" "-" "/" "_"}))
    ))

(defn render-output
  ":as --> :file; :path PATH = Renders the default-image and stores it as file to the defiend path
  :as --> :show = Renders the default-image and displays it in a JFrame
  :as --> :json = Renders the default-image and converts it to JSON"
  ([{:keys [as path clipping]}]
   (if (= as :show)
     (do
       (println "Das Bild wird nun in einem JFrame in der größe des BufferedImage angezeigt")
       (show-image)))
   (if (= as :file)
     (do
       (println (str "Saved image to " path))
       (save-image path)))
   (if (= as :json)
     (do
       (to-json))))
  ([]
   (render-output {:as :show}))
  )

(defmacro compose
  [w h settings & body]
  "May be called with existing BufferedImage or with width and height argument to create a BufferedImage with given size.
   Binds the the new image and its Graphics2D object to default-image and default-g2d."
  `(let [image# (BufferedImage. ~w ~h BufferedImage/TYPE_INT_ARGB)]
     (binding [default-image image#
               default-g2d (.createGraphics image#)
               default-render-settings ~(merge default-render-settings settings)]
       (set-rendering-hints ~default-render-settings)
       (do ~@body))))

(defmacro draw
  [settings & body]
  `(binding [default-shape-values ~(merge default-shape-values settings)]
     (do
       (set-shape-settings)
       ~@body
       (reset-shape-settings))))





