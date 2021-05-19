(ns test.index
  (:require [hiccup.page :refer [html5,
                                 include-js,
                                 include-css]]))

(defn index-page []
  (html5
    [:head
     [:title "Test clojure"]
     (include-css "css/main.css")]
    [:body
     [:div {:id "app"}]
     (include-js "js/main.js")
     ;[:script "test.core.init()"]
     ]))


