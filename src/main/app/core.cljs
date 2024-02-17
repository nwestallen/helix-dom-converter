(ns app.core
  (:require [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            [app.html :as html]
            ["@uiw/react-codemirror" :default CodeMirror]
            ["@codemirror/lang-html" :refer (html)]
            ["@nextjournal/lang-clojure" :refer (clojure)]
            [clojure.string :as s]
            ))

(def default-input "<div class=\"foo\">
  <button class=\"asdf\" aria-role=\"jkl\">qwfp</button>
  <input value=\"1\">
  <span><a href=\"/bar\">Baz</a></span>
  </div>")

(defn autowrap [s]
  (apply str
   (first s)
   (s/replace (apply str (rest s)) #"\(" "\n (")
   )
  )

(defn reclass [s]
  (s/replace s #"class" "class-name"))

(def transform (comp autowrap reclass))

(comment (transform "(div class=frog (p dude))"))
(defnc app []
  {:helix/features {:fast-refresh true}}
  (let [[rawinput set-rawinput] (hooks/use-state default-input)
        [domalias set-domalias] (hooks/use-state "dom")]
    (d/div {:class-name "bg-stone-300 min-h-screen"}
      (d/div {:class-name "flex flex-row h-24 bg-stone-700 justify-start"}
        (d/div {:class-name "m-4"}
        (d/img {:src "Clojure_logo.svg" :class-name "object-contain w-[100%] h-[100%]"}))
        (d/div {:class-name "flex items-center w-full"}
        (d/h1 {:class-name "text-4xl text-slate-50 font-bold drop-shadow-lg w-1/2"} "HTML to helix.dom")))
     (d/p {:class-name "mx-6 mt-2 font-bold"}"helix.dom alias")
     (d/input {:id "alias-input":class-name "mx-6" :value domalias :on-change #(set-domalias (.. % -target -value))})
     (d/div {:class-name "flex w-5/6 m-4"}
       (d/div {:class-name "p-2 w-1/2 bg-[#62B132] m-2 rounded-md drop-shadow-xl h-fit"}
         (d/div {:class-name "flex m-1 justify-between"}
             (d/h2 {:class-name "text-white font-bold text-xl"}"HTML")
             ;;(d/button {:class-name "bg-[#91DC47] text-white rounded px-2"} "copy to clipboard")
           )
             ($ CodeMirror {
                          :value rawinput
                          :extensions (html)
                          :onChange #(set-rawinput %)}))
       (d/div {:class-name "p-2 w-1/2 bg-[#5881D8] m-2 rounded-md drop-shadow-xl h-fit"}
         (d/div {:class-name "flex m-1 justify-between"}
             (d/h2 {:class-name "text-white font-bold text-xl"} "helix.dom")
         ;;(d/button {:class-name "bg-[#8FB5FE] text-white rounded px-2" :on-click #(js/navigator.clipboard.writeText("copied text!"))} "copy to clipboard")
           )
             ($ CodeMirror {
                          :value (transform (str (html/hick:html->dom rawinput :alias domalias)))
                          :extensions (clojure)}))))))

;;TODO modify html/hick:html->dom to handle html comments
;;TODO general-error handling, edge cases
;;TODO add copy button functionality
;;TODO onlick -> on-click

(defn ^:export init []
  (let [root (rdom/createRoot (js/document.getElementById "root"))]
    (.render root ($ app))))
