(ns test.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::loading
  (fn [db]
    (:loading db)))

(rf/reg-sub
  ::applications
  (fn [db]
    (:apps-response db)))
