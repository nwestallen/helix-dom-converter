(ns app.core
  (:require [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            [app.html :as html]))

(def input-format "border border-black p-2 w-full h-[96%] resize-none rounded-md")
(def default-input "<div class=\"foo\">
  <button class=\"asdf\" aria-role=\"jkl\">qwfp</button>
  <input value=\"1\">
  <span><a href=\"/bar\">Baz</a></span>
  </div>")

(defnc app []
  {:helix/features {:fast-refresh true}}
  (let [[rawinput set-rawinput] (hooks/use-state default-input)]
    (d/div {:class-name "bg-stone-300 h-screen"}
     (d/h1 {:class-name "text-4xl text-slate-50 font-bold p-7 bg-stone-700 drop-shadow-lg"} "HTML to Helix-Dom")
     (d/div {:class-name "flex w-5/6 h-[75vh] m-4"}
            (d/div {:class-name "p-2 w-1/2 bg-blue-400 m-2 rounded-md drop-shadow-xl"}
             (d/h2 {:class-name "font-bold"}"HTML")
             (d/textarea {:class-name input-format
                          :value rawinput
                          :on-change #(set-rawinput (.. % -target -value))}))
            (d/div {:class-name "p-2 w-1/2 bg-teal-400 m-2 rounded-md drop-shadow-xl"}
             (d/h2 {:class-name "font-bold"} "Helix-Dom")
             (d/textarea {:class-name input-format
                          :value (str (html/hick:html->dom rawinput))}))))))

;;TODO format output text; newline & indent nested tags/components/parentheses
;;TODO modify html/hick:html->dom to handle html comments
;;TODO enable users to select helix-dom abbreviation/prefix e.g. "d/" instead of "dom/"
;;TODO map keywords that are different from html e.g. "class" -> ":class-name"

(defn ^:export init []
  (let [root (rdom/createRoot (js/document.getElementById "root"))]
    (.render root ($ app))))
