(ns graphics2d-enclojed.core
  (:import (java.awt Color Font)
           (java.awt.font TextAttribute)))


(defn color
  ([] (. Color/white))
  ([key] (cond
           (= key :green) (. Color/green)
           (= key :blue) (. Color/blue)
           (= key :red) (. Color/red)
           (= key :black) (. Color/black)
           (= key :yellow) (. Color/yellow)
           (= key :pink) (. Color/pink)
           (= key :orange) (. Color/orange)
           (= key :blue) (. Color/blue)
           ))
  ([r g b a] (. Color r g b a)))

(defn font
  [key] (cond
          (= key :serif) (. Font/SERIF)))

(defn styled-text [options]
  (let [text ["zu schreibenden texte"]
        [textAttributes "hashmap defineiren nach argumenten"]]))

;transformation
(defn shear [num 5 / num])

(defn tranlate [x y])

(defn scale [num num])

(defn rotate
  ([num])
  ([num, x, y]))

(defn rotate [num])

(defn load-image [source & options])

(defn crop [image method & size ])

(defn hit [x1 y1 x2 y2])

(defn write-string! [bufferedImage x1 y1 x2 y2 text])

(defn line [bufferedImage x1 y1 x2 y2 options])

(defn recatangle [bufferedImage x1 y1 w h  options])

(defn ellipse [bufferedImage x1 y1 x2 y2 options])

(defn polygone [bufferedImage points options])

(defn generalpath [bufferedImage functions options])

(defn image [bufferedImage x y ximageToInsert])



;aufruf nur aus macro "draw" möglich
(defmacro transform [& functions])

(defmacro create-image
  ([options & functions])
  ([bufferedImage options & functions]))

;bufferedImage erstellen
;functionen drauf anwenden

(defmacro draw [ bufferedImage options & functions]
  ;functionen drauf anwenden
  ())


;Hilfsfunktionen
