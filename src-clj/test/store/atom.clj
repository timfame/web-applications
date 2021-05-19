(ns test.store.atom
  (:require [test.store.core :as s]))

(defrecord AtomStore [data])

(extend-protocol s/ApplicationStore
  AtomStore
  (get-applications [store]
    (s/sort-by-id (get @(:data store) :applications [])))
  (put-application! [store application]
    (swap! (:data store)
           update :applications
           conj (assoc application :id (:id (swap! (:data store) update :id inc)))))
  (delete-application! [store id]
    (swap! (:data store)
           update :applications
           (fn [old-applications]
             (vec (remove #(= (:id %) id) old-applications))))))

(defn get-store []
  (->AtomStore (atom {:id           0
                      :applications []})))