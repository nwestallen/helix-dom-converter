(ns app.html
  (:require [hickory.core :as h]
            [clojure.string :as string]
            [clojure.walk :as walk]))

(defn hick:html->dom
  [html-string & {:keys [alias] :or {alias "dom"}}]
  (let [expr (->> html-string
                  (string/split-lines) ; get rid of newlines
                  (map string/trim) ; trim whitespace
                  (string/join "")
                  (h/parse-fragment)
                  (map h/as-hickory)
                  (walk/postwalk
                   (fn [x]
                     (if (and (map? x) (:type x))
                       ;; you can change the "dom" here to be any ns/alias
                       `(~(if (:tag x) (symbol alias (name (:tag x))) (symbol "comment"))
                         ~@(when (seq (:attrs x)) [(:attrs x)])
                         ~@(:content x))
                       x))))]
    ;; as-fragment returns a vector of one or more elements
    ;; if there's just one element, return that, else wrap in a fragment
    (if (= 1 (count expr))
      (first expr)
      (cons '<> expr))))

(hick:html->dom "<div class=\"foo\">
   <button class=\"asdf\" aria-role=\"jkl\">qwfp</button>
   <input value=\"1\">
   <span><a href=\"/bar\">Baz</a></span>
 </div>")
;; => (dom/div {:class "foo"} (dom/button {:class "asdf", :aria-role "jkl"} "qwfp") (dom/input {:value "1"}) (dom/span (dom/a {:href "/bar"} "Baz")))
