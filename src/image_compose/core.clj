(ns image-compose.core
  (:require [clojure.data.codec.base64 :as b64])
  (:import (java.awt Color Font Graphics Graphics2D Dimension BasicStroke RenderingHints AlphaComposite GradientPaint TexturePaint)
           (java.awt.font TextAttribute)
           (java.awt.image BufferedImage RescaleOp)
           (javax.swing JFrame)
           (java.awt.geom AffineTransform Rectangle2D$Double)
           (javax.imageio ImageIO)
           (java.io File ByteArrayOutputStream)))


(declare set-background rectangle set-shape-attributes reset-shape-attributes)

(defmacro draw-fill-reset
  "Convenience macro not to call set-shape-setting and reset-shape-settings everytime a shape was drawn to default-g2d-object."
  [atrributes & body]
  `(if (not-empty ~atrributes)
     (do
       (set-shape-attributes ~atrributes)
       ~@body
       (reset-shape-attributes))
     (do ~@body)))

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


(defn color
  "Returns Java.awt.Color object. Can be called with key to get predefiend Java Colors or R G B a values 0-1"
  ([] (. Color Color/white))
  ([key] (cond
           (= key :white) (. Color Color/WHITE)
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

(defn gradient-paint
  ([x1 y1 color1 x2 y2 color2 cyclic]
   (GradientPaint. x1 y1 color1 x2 y2 color2 cyclic))
  ([x1 y1 color1 x2 y2 color2]
   (GradientPaint. x1 y1 color1 x2 y2 color2)))

(defn texture-paint
  [txtr anchor-x1 anchor-y1 anchor-x2 anchor-y2]
  (let [rec (Rectangle2D$Double. anchor-x1 anchor-y1 anchor-x2 anchor-y2)]
    (TexturePaint. txtr rec)
    ))


(def ^:dynamic default-image (BufferedImage. 1920 1080 BufferedImage/TYPE_INT_ARGB))
(def ^:dynamic default-g2d (.createGraphics default-image))
(def ^:dynamic default-shape-attributes {
                                       :width       1.0
                                       :join        :miter
                                       :miter-limit 10.0
                                       :cap         :square
                                       :dash        nil
                                       :dash-phase  0
                                       :composite   :src_over
                                       :alpha       1.0
                                       :paint       :black
                                       :xor-mode    nil
                                       })

(def ^:dynamic default-render-settings {:antialiasing         :off
                                        :aplpha-interpolation :default
                                        :color-rendering      :quality
                                        :dithering            :disable
                                        :fractional-metrics   :on
                                        :interpolatioin       :bicubic
                                        :rendering            :default
                                        :stroke-control       :default
                                        :text-antialiasing    :default
                                        })

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
                      :src_in   (AlphaComposite/SRC_IN)
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

(defn set-transform
  [trans]
  (.setTransform default-g2d trans))

(defn concat-transforms
  ([at new-at]
   (.concatenate at new-at)
   at))

(defn translate
  ([at x y]
   (.translate at x y) at))

(defn shear
  ([at shx shy]
   (.shear at shx shy) at))

(defn rotate
  ([at theta]
   (.rotate at theta) at)
  ([at vecx vecy]
   (.rotate at vecx vecy) at)
  ([at theta anchorx anchory]
   (.rotate at theta anchorx anchory) at)
  ([at vecx vecy anchorx anchory]
   (.rotate at vecx vecy anchorx anchory) at))

(defn scale
  [at scalex scaley]
  (.scale at scalex scaley) at)

(defmacro transform
  ([& forms]
   `(let [at# (AffineTransform.)]
      (-> at#
          ~@forms))))

(defmacro with-transform
  ""
  ([trans & forms]
   `(let [current-trans# (.getTransform default-g2d)
          new-trans# (concat-transforms (.getTransform default-g2d) ~trans)]
      (do
        (.transform default-g2d new-trans#)
        ~@forms
        (set-transform current-trans#))
      )))

(defn load-image [source]
  "Creates a BufferedImage from a defined source"
  (ImageIO/read (File. source)))

(defn create-font
  "Returns a logical Font which can be used on any Java platform. Font must be defiend in fonts map"
  ([name style size]
   (Font. (name fonts) (style font-styles) size)
    ))

(defn create-styled-text
  "Creates a map containing the text which shall be drawn to the default-g2d object as String and a font defined by
  FontfamilyName, Style, Size and Textattributes
  For available FontFamilys and styles see defiend maps 'fonts' and 'font-styles'
  Available Textatrributes can be seen in maps: text-weight, text-width, text-posture, text-underline.
  Furthermore fonts can be defiend by the keys: :kerning bool, :swap-colors bool , :foreground Color, :background Color,
  :name String(FontFamilyName), style int"
  ([text name style size {:keys [weight width underline foreground background strike-through swap-colors kerning posture]}]
   (let [font (create-font name style size)
         styled-font (when-> {}
                             kerning (assoc (TextAttribute/KERNING) (TextAttribute/KERNING_ON))
                             posture (assoc (TextAttribute/POSTURE) (posture text-posture))
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
  ([text name style size]
   (create-styled-text text name style size {}))
  ([text name style]
   (create-styled-text text name style 20 {}))
  ([text name]
   (create-styled-text text name :plain 20 {}))
  ([text]
   (create-styled-text text :dialog :plain 20 {})))

(defn draw-fill [atrributes fill-func draw-func]
  (let [fill (:fill atrributes)
        atrributes (dissoc atrributes :fill)]
    (draw-fill-reset atrributes
                     (if fill
                       (fill-func)
                       (draw-func)))))

(defn create-scale-op [& rgba]
  "Creates a RescaleOp which can be used to change transparecny of an image when passed to image function"
  (RescaleOp. (float-array rgba) (float-array 4) nil))

(defn background [color]
  "Overwrites the whole image with the given color"
  (rectangle 0 0 (.getWidth default-image) (.getHeight default-image) {:fill true :composite :src :paint color}))

(defn set-stroke
  "Sets stroke attributes on default-g2d object"
  [width cap join miter-limit dash dash-phase]
  (let [stroke (BasicStroke. width (cap stroke-caps) (join stroke-joins) miter-limit dash dash-phase)]
    (.setStroke default-g2d stroke)))

(defn set-xor-mode [paint]
  (if (keyword? paint)
    (.setXORMode default-g2d (color paint))
    (.setXORMode default-g2d paint)))

(defn set-paint-mode []
  (.setPaintMode default-g2d))


(defn set-paint [color]
  "Sets paint color on default-g2d object "
  (.setPaint default-g2d color))

(defn set-font [font]
  "Sets font on default-g2d object"
  (.setFont default-g2d font))

(defn set-clip
  ([x1 y1 x2 y2]
   (.clipRect default-g2d x1 y1 x2 y2))
  ([shape]
   (.clip default-g2d shape)))

(defn set-composite [comp alpha]
  "Sets composite attribute on default-g2d object"
  (let [alpha-composite (. AlphaComposite getInstance (comp composite-rules) alpha)]
    (.setComposite default-g2d alpha-composite)))

(defn set-render-settings
  "Is called on compose macro to set renderinghints on default-g2d object"
  [{:keys [antialiasing aplpha-interpolation paint dithering fractional-metrics interpolatioin rendering stroke-control text-antialiasing background]}]
  (let [rendering-hints (when-> {}
                                antialiasing (assoc (RenderingHints/KEY_ANTIALIASING) (antialiasing keys-antialiasing))
                                aplpha-interpolation (assoc (RenderingHints/KEY_ALPHA_INTERPOLATION) (aplpha-interpolation keys-alpha-interpolation))
                                paint (assoc (RenderingHints/KEY_COLOR_RENDERING) (paint keys-color-rendering))
                                dithering (assoc (RenderingHints/KEY_DITHERING) (dithering keys-dithering))
                                fractional-metrics (assoc (RenderingHints/KEY_FRACTIONALMETRICS) (fractional-metrics keys-fractional-metrics))
                                interpolatioin (assoc (RenderingHints/KEY_INTERPOLATION) (interpolatioin keys-interpolation))
                                rendering (assoc (RenderingHints/KEY_RENDERING) (rendering keys-rendering))
                                stroke-control (assoc (RenderingHints/KEY_STROKE_CONTROL) (stroke-control keys-stroke-control))
                                text-antialiasing (assoc (RenderingHints/KEY_TEXT_ANTIALIASING) (text-antialiasing keys-text-antialiasing)))]
    (.setRenderingHints default-g2d rendering-hints)
    (if background
      (background background))))

(defn set-shape-attributes
  "Sets attributes for stroke, color and composite to default-g2d object."
  ([{:keys [width cap join miter-limit dash dash-phase composite alpha paint xor-mode]}]
   (if (or width cap join miter-limit dash dash-phase)
     (let [width (or width (:width default-shape-attributes))
           cap (or cap (:cap default-shape-attributes))
           join (or join (:join default-shape-attributes))
           miter-limit (or miter-limit (:miter-limit default-shape-attributes))
           dash (if dash
                  (float-array dash)
                  (:dash default-shape-attributes))
           dash-phase (or dash-phase (:dash-phase default-shape-attributes))]
       (set-stroke width cap join miter-limit dash dash-phase)
       ))
   (if paint
     (if (keyword? paint)
       (set-paint (color paint))
       (set-paint (eval paint))))
   (if composite
     (let [alpha (or alpha (:alpha default-shape-attributes))]
       (set-composite composite alpha)))
   (if xor-mode
     (set-xor-mode xor-mode))
    )
  ([]
   (set-shape-attributes default-shape-attributes)))

(defn reset-shape-attributes []
  "Calls on set-shape-settings to reset shape-settings back to defined default-shape-settings"
  (set-shape-attributes)
  (set-paint-mode))



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
  ([x1 y1 x2 y2 attributes]
   (draw-fill-reset attributes
                    (.drawLine default-g2d x1 y1 x2 y2)
                    ))
  ([x1 y1 x2 y2]
   (line x1 y1 x2 y2 {})))

(defn polyline
  "Draws multiple lines to default-g2d object. May be called with settings map to set shape settings. Settings will be restored to
  default-shape-values after drawing.
  Takes a sequnce for x and y coordinates a the number of lines"
  ([x y number attributes]
   (draw-fill-reset attributes
                    (.drawPolyline default-g2d x y number)))
  ([x y number]
   (polyline x y number {})))


(defn rectangle
  "Draws a rectangle to defaul-g2d object. May be called with settings map to set shape settings. Settings will be restored to
  default-shape-values after drawing process finished."
  ([x y w h attributes]
   (draw-fill attributes
              #(.fillRect default-g2d x y w h)
              #(.drawRect default-g2d x y w h)))
  ([x y w h]
   (rectangle x y w h {:fill false})))


(defn round-rectangle
  "Draws a rectangle to defaul-g2d object. May be called with settings map to set shape settings. Settings will be restored to
  default-shape-values after drawing process finished."
  ([x y w h arcW arcH attributes]
   (draw-fill attributes
              #(.fillRoundRect default-g2d x y w h arcW arcH)
              #(.drawRoundRect default-g2d x y w h arcW arcH)))
  ([x y w h arcW arcH]
   (round-rectangle x y w h arcW arcH {:fill false})))


(defn oval
  "Draws an oval to default-g2d-object. May be called with settings map to set shape settings. Settings will be restored to
  default-shape-values after drawing process finished."
  ([x y w h attributes]
   (draw-fill attributes
              #(.fillOval default-g2d x y w h)
              #(.drawOval default-g2d x y w h)))
  ([x y w h]
   (oval x y w h {:fill false})))


(defn polygon
  "Draws a polygon to the default-g2d object. May be called with shape settings. Settings will be restored after drawing process.
  Takes sequences of x, y coordinates and number of points the polygon consists of"
  ([x y attributes]
   (if-not (= (count x) (count y))
     (throw (RuntimeException.
              "Number of x and y coordinates must be equal")))
   (draw-fill attributes
              #(.fillPolygon default-g2d x y (count x))
              #(.drawPolygon default-g2d (int-array x) (int-array y) (count x))))
  ([x y]
   (polygon x y {:fill false})))


(defn shapes
  "Draws shape object to default-g2d object. Can be called with shape settings map to overide defualt-shape-settings.
  Shape settings will be restored when passed to function."
  ([shapes-vec attributes]
   (draw-fill attributes
              #(doseq [shape-obj shapes-vec] (.fill default-g2d shape-obj))
              #(doseq [shape-obj shapes-vec] (.draw default-g2d shape-obj))))
  ([shapes-vec]
   (shapes shapes-vec {:fill false})))


(defn image
  "Draws a BufferedImage into default-image. Can be called with a settings map to define composite attribute.
   Setting will be restored to default-shape-settings
   May also scale image to fit into wanted area.
   Settings attribute :filter takes an ScaleOp Object which can be defined by function create-scaleop"
  ([x y img]
   (image x y img {}))

  ([x y img attributes]
   (let [filter (:filter attributes)]
     (draw-fill-reset attributes
                      (if filter
                        (.drawImage default-g2d img filter x y)
                        (.drawImage default-g2d img x y nil)))))

  ([x1dest y1dest x2dest y2dest x1src y1src x2src y2src img]
   (image x1dest y1dest x2dest y2dest x1src y1src x2src y2src img {} default-g2d))

  ([x1dest y1dest x2dest y2dest x1src y1src x2src y2src img attributes]
   (image x1dest y1dest x2dest y2dest x1src y1src x2src y2src img attributes default-g2d))

  ([x1dest y1dest x2dest y2dest x1src y1src x2src y2src img attributes g2d]
   (draw-fill-reset attributes
                    (.drawImage g2d img x1dest y1dest x2dest y2dest x1src y1src x2src y2src nil))))

(defn resize
  [img-to-scale w h]
  (let [new-img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)]
    (image 0 0 w h 0 0 (.getWidth img-to-scale) (.getHeight img-to-scale) img-to-scale {} (.createGraphics new-img))
    new-img))

(defn crop [])

(defn blur [])

(defn save-image [image path]
  "Stores default-image into file. Available formats are gif jpeg and png"
  (let [file (File. path)
        format (last (clojure.string/split path #"\."))]
    (ImageIO/write image format file)))

(defn to-b64
  "Converts default-iamge to Base64"
  []
  (let [baos (new ByteArrayOutputStream)]
    (ImageIO/write default-image "jpeg" baos)
    (let [bytes (.toByteArray baos)]
      (clojure.string/replace (String. (b64/encode bytes)) #"\+|/" {"+" "-" "/" "_"}))
    ))

(defn show-image [image]
  "Show default-image in Jframe"
  (let [frame (proxy [JFrame] []
                (paint [#^Graphics g]
                  (.drawImage g image 0 0 nil)))
        dimensison (Dimension. (.getWidth image) (.getHeight image))]
    (doto frame
      (.setSize dimensison)
      (.setVisible true))
    frame))

(defn repaint [frame]
  "Repaint default-image in displayed JFrame"
  (.repaint frame))

(defn render
  ":as --> :file; :path PATH = Renders the default-image and stores it as file to the defiend path
  :as --> :show = Renders the default-image and displays it in a JFrame
  :as --> :json = Renders the default-image and converts it to JSON"
  ([image {:keys [as path clipping]}]
   (if clipping
     (do (if (seq? clipping)
           (set-clip (first clipping) (second clipping) (nth clipping 2) (last clipping))
           (set-clip clipping)))
     (do
       (if (= as :show)
         (show-image image))
       (if (= as :file)
         (save-image image path))
       (if (= as :b64)
         (to-b64)))))
  ([image]
   (render image {:as :show}))
  ([]
   (render default-image))
  )


(defmacro compose
  [w h & forms]
  "May be called with existing BufferedImage or with width and height argument to create a BufferedImage with given size.
   Binds the the new image and its Graphics2D object to default-image and default-g2d."
  (loop [settings (first forms)
         body (next forms)]
    (if (map? settings)
      `(let [image# (BufferedImage. ~w ~h BufferedImage/TYPE_INT_ARGB)]
         (binding [default-image image#
                   default-g2d (.createGraphics image#)
                   default-render-settings ~(merge default-render-settings settings)]
           (set-render-settings default-render-settings)
           (do
             ~@body
             default-image)))
      (recur {} forms))))

(defmacro with-attributes
  [attributes & body]
  `(do
     (binding [default-shape-attributes ~(merge default-shape-attributes attributes)]
         (do
           (set-shape-attributes)
           ~@body))
     (reset-shape-attributes)))
