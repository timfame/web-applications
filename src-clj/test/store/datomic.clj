(ns test.store.datomic
  (:require [test.store.core :as s]
            [datomic.api :as d]))

(defrecord DatomicStore [conn])

(def schema [{:db/ident       :application/title
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The title of the application"}
             {:db/ident       :application/description
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The description of the application"}
             {:db/ident       :application/applicant
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The applicant of the application"}
             {:db/ident       :application/executor
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The executor of the application"}
             {:db/ident       :application/exec-year
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "Year of the execution date of the application"}
             {:db/ident       :application/exec-month
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "Month of the execution date of the application"}
             {:db/ident       :application/exec-day
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "Day of the execution date of the application"}])

(defn map->db [e]
  (let [date (:exec-date e)]
    {:application/title       (:title e)
     :application/description (:description e)
     :application/applicant   (:applicant e)
     :application/executor    (:executor e)
     :application/exec-year   (:year date)
     :application/exec-month  (:month date)
     :application/exec-day    (:day date)}))

(defn db->map [e]
  {:id          (:db/id e)
   :title       (:application/title e)
   :description (:application/description e)
   :applicant   (:application/applicant e)
   :executor    (:application/executor e)
   :exec-date   {:year  (:application/exec-year e)
                 :month (:application/exec-month e)
                 :day   (:application/exec-day e)}})

(def find-all-q '[:find (pull ?e [*])
                  :where [?e :application/title]])

(defn find-all [conn]
  (d/q find-all-q (d/db conn)))

(extend-protocol s/ApplicationStore
  DatomicStore
  (get-applications [store]
    (s/sort-by-id (map (comp db->map first) (find-all (:conn store)))))
  (put-application! [store application]
    @(d/transact (:conn store) [(map->db application)]))
  (delete-application! [store id]
    @(d/transact (:conn store) [{:db/excise id}])))

(def db-uri "datomic:free://localhost:4334/testdb")

(defn get-store [options]
  (let [uri (get options :db-uri db-uri)]
    (do
      (d/create-database uri)
      (let [conn (d/connect uri)]
        @(d/transact conn schema)
        (->DatomicStore conn)))))
