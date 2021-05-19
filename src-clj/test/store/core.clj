(ns test.store.core)

(defprotocol ApplicationStore
  (get-applications [store])
  (put-application! [store application])
  (delete-application! [store id]))

(defn sort-by-id [applications]
  (sort-by :id applications))

(defn wrap-store-middleware [handler store]
  (fn [request]
    (handler (assoc request :store store))))
