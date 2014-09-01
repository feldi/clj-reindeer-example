(defproject clj-reindeer-example "0.1.0"
  :description "clj-reindeer example"
  :url "http://github.com/feldi/clj-reindeer-example"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-reindeer "0.3.1"]]
  :plugins [[lein-servlet "0.4.0"]
            [lein-localrepo "0.4.1"]]
  :servlet {:deps [[lein-servlet/adapter-jetty9 "0.4.0"]
                   ;;;[lein-servlet/adapter-tomcat7 "0.4.0"]
                   ]
            :config {:engine :jetty
                     ;;;:engine :tomcat
                     :host "localhost"
                     :port 3000}
            :webapps {:cljreindeerexample
                       {:web-xml "src/main/webapp/WEB-INF/web.xml"
                        :public "resources"}}}
  :profiles {:dev
              {:dependencies
                [[javax.servlet/javax.servlet-api "3.1.0"]
                 [midje "1.5.1"]
                 [com.vaadin/vaadin-push "7.1.10"]]}}
  :aot [clj.reindeer.example.rssapplicationui])
