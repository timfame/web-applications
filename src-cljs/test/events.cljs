(ns test.events
  (:require [test.db :as db]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(rf/reg-event-db
  ::initialize-db
  (fn [_ _] db/default-db))

(rf/reg-event-db
  ::fetch-applications-success
  (fn
    [db [_ response]]
    (-> db
          (update-in [:just-deleted :fetch-left] #(max 0 (dec %)))
          (assoc :loading false)
          (assoc :apps-response (js->clj response)))))

(rf/reg-event-db
  ::bad-response
  (fn
    [_ _]
    (js/alert "Bad response")))

(rf/reg-event-fx
  ::create-application-success
  (fn
    [{:keys [db]} _]
    {:db       (assoc db :loading false)
     :dispatch [::fetch-applications]}))

(rf/reg-event-fx
  ::delete-application-success
  (fn
    [{:keys [db]} _]
    {:db       (assoc db :loading false)
     :dispatch [::fetch-applications]}))

(def applications-server-uri "http://localhost:8080/applications")

(rf/reg-event-fx
  ::fetch-applications
  (fn
    [{:keys [db]} _]
    {:db         (assoc db :loading true)
     :http-xhrio {:method          :get
                  :uri             applications-server-uri
                  :mode            :cors
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::fetch-applications-success]
                  :on-failure      [::bad-response]}}))

(rf/reg-event-fx
  ::create-application
  (fn
    [{:keys [db]} [_ data]]
    {:db         (assoc db :loading true)
     :http-xhrio {:method          :post
                  :uri             applications-server-uri
                  :mode            :cors
                  :params          data
                  :format          (ajax/json-request-format)
                  :response-format (ajax/text-response-format)
                  :on-success      [::create-application-success]
                  :on-failure      [::bad-response]}}))

(rf/reg-event-fx
  ::delete-application
  (fn
    [{:keys [db]} [_ id]]
    {:db         (-> db
                     (assoc :loading true)
                     (assoc :just-deleted {:id id
                                           :fetch-left 2}))
     :http-xhrio {:method          :delete
                  :uri             (str applications-server-uri "/" id)
                  :mode            :cors
                  :format          (ajax/json-request-format)
                  :response-format (ajax/text-response-format)
                  :on-success      [::delete-application-success]
                  :on-failure      [::bad-response]}}))
