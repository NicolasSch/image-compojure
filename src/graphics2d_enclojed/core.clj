(ns graphics2d-enclojed.core
  (:import (java.awt Color Font Graphics Graphics2D Dimension Composite)
           (java.awt.font TextAttribute)
           (java.awt.image BufferedImage)
           (javax.swing JFrame)
           (java.awt.geom Rectangle2D Rectangle2D$Double)))


(def default-g2d (ref nil))
(def default-image (ref nil))
(def default-render-settings (ref {:antialiasing         :off
                                   :aplpha-interpolation :default
                                   :color-rendering      :quality
                                   :dithering            :disable
                                   :fractional-metrics   :on
                                   :interpolatioin       :bicubic
                                   :rendering            :quality
                                   :stroke-control       :normalize
                                   :text-antialiasing    :off}))
(def default-shape-settings (ref {:join      :bevel
                                  :cap       :butt
                                  :width     0.5
                                  :dash      0
                                  :composite :src
                                  :fill      false}))

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
  ([r g b a] (Color. r g b a)))

(defn create-font
  [key] (cond
          (= key :serif) (. Font Font/SERIF)))

(defn create-styled-text [options]
  (let [text ["zu schreibenden texte"]
        textAttributes "hashmap defineiren nach argumenten"]))

(defn setOptions [options])

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

(defn draw-recatangle
  ([x y w h settings]
   (let [g2d (deref default-g2d)
         rectangle (Rectangle2D$Double. x y w h)]
     (.setColor g2d (Color. 0.0 0.0 1.0 1.0))
     (if (:fill settings)
       (.fill g2d rectangle)
       (.draw g2d rectangle))
     )))

(defn ellipse [bufferedImage x1 y1 x2 y2 options])

(defn polygone [bufferedImage points options])

(defn generalpath [bufferedImage functions options])

(defn image [bufferedImage x y ximageToInsert])

(defn set-stroke[join cap width dash]
  (let [stroke (. Basis)]))
(defn set-color[color])
(defn set-composite [comp])


(defn set-shape-settings [{:keys [join cap width dash composite fill color] :or {join :bevel cap :butt width 0.2 dash 0 composite :src fill false}}]
  (println "setze optionen für shape")
  (set-stroke join cap width dash)
  (set-color color)
  (set-composite composite)
  )


(defn set-render-settings [render-settings]
  (println "setze settings für Rendering")
  (println render-settings))

(defn paint
  [image]
  (let [g2d (deref default-g2d)]
    (.drawImage g2d image nil 0 0)))

(defn show-image [image]
  (let [image (deref image)
        frame (new JFrame)
        dimensison (Dimension. (.getWidth image) (.getHeight image))]
    (paint image)
    (doto frame
      (.setSize dimensison)
      (.setVisible true))))

(defn render-output
  ([image {:keys [as path clipping format]}]
   (if (= as :show)
     (do
       (println "Das Bild wird nun in einem JFrame in der größe des BufferedImage angezeigt")
       (show-image image))))
  ([options]
   (render-output default-image options))
  )

;aufruf nur aus macro "draw" möglich
(defmacro transform [& functions])

(defmacro create-image
  "Creates an BufferedImage with given size. Can be used with existing image"
  ([image]
   (dosync
     (ref-set default-image image)
     (ref-set default-g2d (.createGraphics image))))
  ([w h & render-settings]
   (let [image (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
         render-settings (last render-settings)]
     `(do
        (create-image ~image)
        (if (nil? ~render-settings)
          (set-render-settings ~(deref default-render-settings))
          (set-render-settings ~render-settings)
          )))
    ))



;bufferedImage erstellen
;functionen drauf anwenden

(defmacro draw [bufferedImage options & functions]
  ;functionen drauf anwenden
  ())


;Hilfsfunktionen
