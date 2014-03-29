(ns clj.reindeer.example.rssapplication
  (require [clj.reindeer.example.rss :as rss])
  (:use [clj.reindeer.core]))

;; (start-nrepl-server)

(def LATEST_RSS_FEED_URL "latest_rss_feed_url")

(defn- create-container
  []
  (indexed-container :properties [(container-property :pid "Title" :type String)
                                  (container-property :pid "Link" :type String)
                                  (container-property :pid "Description" :type String)]))

(defn- fill-table
  [table feed-lines]
  (remove-all-items table)
  (doseq [feed-line feed-lines]
    (set-item-property-values (add-container-item table)
                              {"Title"       (:title feed-line)
                               "Link"        (:link feed-line)
                               "Description" (:description feed-line)} )))

(defn- create-item-click-listener
  [content-label link-label]
  (fn [evt] 
     (config! content-label :value (get-item-property-value (get-item-from-item-click-event evt) "Description"))
     (config! link-label :caption  (get-item-property-value (get-item-from-item-click-event evt) "Link")
                         :resource (external-resource (get-item-property-value (get-item-from-item-click-event evt) "Link")))))

(defn- create-feed-table
  [content-label link-label]
  (table :width "100%",
         :height "50%" 
         :selectable? true, 
         :immediate? true, 
         :striped? true
         :column-header-mode :hidden
         :on-item-click (create-item-click-listener content-label link-label)
         :container-datasource (create-container)
         :visible-columns ["Title"]))

 (defn- create-feed-content-label
  []
  (label :value "Please select a feed item in the table above"
         :content-mode :html
         :width "100%"
         :height "50%"))

(defn- create-url-field
  []
  (text-field :width "100%"
              :value (or (get-cookie-value LATEST_RSS_FEED_URL) "") 
              :input-prompt "Feed URL")) ; example: http://jaxenter.de/all-rss.xml

(defn- display-feed
  [url table]
  (set-cookie (cookie LATEST_RSS_FEED_URL url :path "/cljreindeerexample" :max-age 1000000))
  (fill-table table (rss/fetch-feed url))
  ) 

(defn- create-content
  []
  (let [content-label (create-feed-content-label)
        url-field     (create-url-field)
        example-label (label "Example: http://jaxenter.de/all-rss.xml") 
        link-label    (link :target-name  "_blank")
        feed-table    (create-feed-table content-label link-label)
        fetch-button  (button :caption "Fetch"
                              :on-click (fn [_] 
                                          (display-feed 
                                            (config url-field :value) 
                                            feed-table)))
        feed-select   (h-l :width "100%" 
                           :spacing true
                           :items [
                                   url-field
                                   "Example: http://jaxenter.de/all-rss.xml"
                                   fetch-button
                                   ]) 
        ui-content    (v-l :spacing true
                           :margin-all true
                           :items [
                                   feed-select
                                   feed-table
                                   content-label
                                   link-label
                                   (print-page-button "Print this page") ; just a test
                                   ])
        ]
    (set-expand-ratio! feed-select url-field 3.0)	
    (set-expand-ratio-at-index! feed-select 1 1.0)	
    ui-content
   ))

(defn init 
   "This is the 'main entry point' to the Vaadin application."
  [main-ui vaadin-request]
  (config-ui! :title "RSS Reader built with Vaadin in Clojure"
              :content (create-content)
              ;; in push mode the cookies are not functioning - a known Vaadin defect... 
;              :push-mode :manual
;              :push-transport :streaming
              ))

