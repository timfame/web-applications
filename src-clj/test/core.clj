(ns test.core
  (:use test.index)
  (:require [test.store.core :as store]
            [test.store.atom :as a-store]
            [test.store.datomic :as d-store]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response, wrap-json-body, wrap-json-params]]
            [ring.middleware.defaults :refer [wrap-defaults, api-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [response]]
            [compojure.core :refer [defroutes GET POST DELETE]]
            [compojure.route :as route])
  (:gen-class))

(defroutes handler
           (GET "/testapp" [] (index-page))
           (GET "/applications" {store :store}
             (response (store/get-applications store)))
           (POST "/applications" {store :store body :body}
             (store/put-application! store body)
             "created")
           (DELETE "/applications/:id" {store :store {id :id} :route-params}
             (store/delete-application! store (Long/parseLong id))
             "deleted")
           (route/resources "/")
           (route/not-found "404 PAGE NOT FOUND"))

(defn app [store]
  (-> handler
      (store/wrap-store-middleware store)
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-defaults api-defaults)
      wrap-reload))

(defn -main []
  (run-jetty (app (d-store/get-store {})) {:port 8080}))
