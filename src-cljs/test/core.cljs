(ns test.core
  (:require [test.events :as events]
            [test.views :as views]
            [reagent.dom :as rd]
            [re-frame.core :as rf]))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-component (.getElementById js/document "app")]
    (rd/unmount-component-at-node root-component)
    (rd/render [views/root] root-component)))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (rf/dispatch-sync [::events/fetch-applications])
  (mount-root))

(init)
