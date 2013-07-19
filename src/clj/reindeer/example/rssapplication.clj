(ns clj.reindeer.example.rssapplication
  (import [com.vaadin.ui 
           Table
           Table$ColumnHeaderMode
           Label]
          [com.vaadin.event
           ItemClickEvent$ItemClickListener]
          [com.vaadin.data.util
           IndexedContainer]
         )
  (require [clj.reindeer.example.rss :as rss])
  (:use [clj.reindeer.core])
  )

(defn- create-item-click-listener
  [content-label link-label]
  (reify ItemClickEvent$ItemClickListener
    (itemClick
     [_ evt]
     (config! content-label :value (.getValue (.getItemProperty (.getItem evt) "Description")))
     (config! link-label :caption  (.getValue (.getItemProperty (.getItem evt) "Link")))
     (config! link-label :resource (ext-res (.getValue (.getItemProperty (.getItem evt) "Link")))))))

(defn- create-feed-table
  [content-label link-label]
  (doto (Table.)
    (.setWidth "100%")
    (.setHeight "50%")
    (.setSelectable true)
    (.setImmediate true)
    (.setColumnHeaderMode Table$ColumnHeaderMode/HIDDEN)
    (.addItemClickListener (create-item-click-listener content-label link-label))))

 (defn- create-feed-content-label
  []
  (label :value "Please select a feed item in the table above"
         :content-mode Label/CONTENT_XHTML
         :width "100%"
         :height "50%"))

(defn- create-url-field
  []
  (text-field :width "100%"
              :input-prompt "Feed URL" ; example: http://jaxenter.de/all-rss.xml
  ))

(defn- create-container
  [items]
  (let [c (IndexedContainer.)]
    (.addContainerProperty c "Title" String nil)
    (.addContainerProperty c "Link" String nil)
    (.addContainerProperty c "Description" String nil)
    (doseq [item items]
      (let [i (.addItem c (Object.))]
        (.setValue (.getItemProperty i "Title") (:title item))
        (.setValue (.getItemProperty i "Link") (:link item))
        (.setValue (.getItemProperty i "Description") (:description item))))
    c))

(defn- display-feed
  [url table]
  (.setContainerDataSource table (create-container (rss/fetch-feed url))
    (java.util.ArrayList. ["Title"])))

(defn- create-content
  []
  (let [content-label (create-feed-content-label)
        url-field     (create-url-field)
        link-label    (link :target-name  "_blank")
        feed-table    (create-feed-table content-label link-label)
        fetch-button  (button :caption "Fetch"
                              :on-click (fn [ev] 
                                          (display-feed 
                                            (.getValue url-field) 
                                            feed-table)))
        feed-select   (h-l :width "100%" 
                           :spacing true
                           :items [
                                   url-field
                                   ;; "Example: http://jaxenter.de/all-rss.xml" 
                                   fetch-button
                                   ]) 
        ui-content    (v-l :spacing true
                           :margin-all true
                           :items [
                                   feed-select
                                   feed-table
                                   content-label
                                   link-label
                                   (print-page-button "Diese Seite drucken") 
                                   ])
        ]
    (set-expand-ratio! feed-select url-field 1)	
    ui-content
   ))

(defn init 
   "This is the 'main entry point' to the Vaadin application."
  [main-ui vaadin-request]
  (config-ui! :title "RSS Reader built with Vaadin in Clojure"
              :content (create-content)))

