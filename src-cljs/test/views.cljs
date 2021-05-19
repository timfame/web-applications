(ns test.views
  (:require [reagent.core :as r]
            [reagent-forms.core :refer [bind-fields]]
            [reagent-forms.datepicker :refer [format-date]]
            [re-frame.core :as rf]
            [test.events :as reg-events]
            [test.subs :as reg-subs]))

(defn expandable [header body]
  (let [expanded (r/atom false)]
    (fn []
      [:div.expandable
       [:div.expand-header {:on-click #(swap! expanded not)}
        header]
       (when @expanded
         [:div.expand-body body])])))

(defn display-application [application]
  ^{:key (:id application)}
  [:div.application
   [expandable
    [:div#title.item (:title application)]
    [:div#content.item
     [:div#persons.item
      [:div.person.field "Applicant: " (:applicant application)]
      [:div.person.field "Executor: " (:executor application)]]
     [:div#deadline.item "Deadline: "
      (format-date (:exec-date application)
                   {:separator "."
                    :parts [:d :m :yyyy]})]
     [:div#description.item (:description application)]
     [:div#delete.item
      [:button#delete.item
       {:on-click #(rf/dispatch [::reg-events/delete-application (:id application)])}
       "Delete"]]]]])

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn row-no-label [input]
  [:div.row
   [:div.col-md-2]
   [:div.col-md-5 input]])

(def form-template
  [:div
   (row "Title" [:input {:field :text :id :title}])
   (row "Applicant" [:input {:field :text :id :applicant}])
   (row "Executor" [:input {:field :text :id :executor}])
   (row "Execution date" [:div {:field       :datepicker
                                :id          :exec-date
                                :date-format "yyyy/mm/dd"
                                :auto-close? true}])
   (row "Description" [:textarea {:field :textarea :id :description :rows 8}])
   (row-no-label [:div.alert {:field :alert :id :error}])])

(rf/reg-sub
  ::just-deleted-sub
  (fn [db]
    (:just-deleted db)))

(defn root []
  (let [loading (rf/subscribe [::reg-subs/loading])
        applications (rf/subscribe [::reg-subs/applications])
        just-deleted (rf/subscribe [::just-deleted-sub])
        doc (r/atom {:title       "Sample title"
                     :applicant   "Ivanov I.I."
                     :executor    "Orlov. A.A"
                     :exec-date   "2021/05/19"
                     :description "Sample description"})]
    (fn []
      [:div.root
       [:div.outer-handler
        [:div.inner-handler
         [bind-fields form-template doc]
         [:button.create
          {:on-click
           (let [current-doc @doc
                 current-date (:exec-date current-doc)]
             (if (or (empty? (:title current-doc))
                     (empty? (:description current-doc))
                     (empty? (:applicant current-doc))
                     (empty? (:executor current-doc))
                     (nil? (:year current-date))
                     (nil? (:month current-date))
                     (nil? (:day current-date)))
               #(swap! doc assoc :error "All fields must be set")
               #(rf/dispatch [::reg-events/create-application current-doc])))}
          "Create"]]]
       [:div.outer-handler
        [:div.inner-handler
         (when @loading "Loading...")
         [:div.list
          (doall (map
                   display-application
                   (filter #(let [jd @just-deleted]
                              (or (<= (:fetch-left jd) 0)
                                  (not= (:id jd) (:id %))))
                           @applications)))]]]])))
