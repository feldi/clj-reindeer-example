(defproject clj-reindeer-example "0.1.0"
  :description "clj-reindeer example"
  :url "http://github.com/feldi/clj-reindeer-example"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.vaadin/vaadin-server "7.1.0"]
                 [com.vaadin/vaadin-client-compiled "7.1.0"]
                 [com.vaadin/vaadin-themes "7.1.0"]
                 [clj-reindeer "0.3.1"]
                 ]
  :plugins [ [lein-servlet "0.3.0"]
             [lein-localrepo "0.4.1"]]
  :servlet {
            :deps [[lein-servlet/adapter-jetty9 "0.3.0"]]
            :config {
                     :engine :jetty
                     :host "localhost"
                     :port 3000
                     }
            :webapps {
                      :cljreindeerexample
                      {
                       :web-xml "src/main/webapp/WEB-INF/web.xml"
                       :public "resources"
                       }
                      }
            }
  :aot [clj.reindeer.example.rssapplicationui]
  )