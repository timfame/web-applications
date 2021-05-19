(defproject test "0.1.0-SNAPSHOT"
  :description "Clojure web app"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.844"
                  :exclusions [org.apache.ant/ant]]
                 [com.datomic/datomic-free "0.9.5697"
                  :exclusions [com.google.guava/guava]]
                 [ring "1.9.3"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-defaults "0.3.2"]
                 [compojure "1.6.2"]
                 [hiccup "1.0.4"]
                 [reagent "1.0.0"]
                 [reagent-forms "0.5.44"]
                 [re-frame "1.2.0"]
                 [day8.re-frame/http-fx "0.2.3"]
                 [cljs-ajax "0.7.5"]]
  :source-paths ["src-clj"]
  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-figwheel "0.5.20"]]
  :uberjar-name "testapp.jar"
  :profiles {:dev {
                   :figwheel {:http-server-root "public"
                              :css-dirs         ["resources/public/css"]}
                   :cljsbuild {:builds {:dev {:source-paths ["src-cljs"]
                                              :figwheel {:on-jsload "test.core/init"}
                                              :compiler {:main          "test.core"
                                                         :output-to     "resources/public/js/main.js"
                                                         :output-dir    "resources/public/js/out"
                                                         :asset-path    "js/out"
                                                         :pretty-print  true
                                                         :optimizations :none}}}}}
             :uberjar {:hooks       [leiningen.cljsbuild]
                       :aot         :all
                       :omit-source true
                       :cljsbuild {:jar true
                                   :main "test.core"
                                   :builds {:app {:source-paths ["src-cljs"]
                                                  :compiler {:optimizations :advanced
                                                             :pretty-print  false}}}}}}
  :jvm-opts ["-Xmx1024m"]
  :main test.core
  :aot [test.core])
