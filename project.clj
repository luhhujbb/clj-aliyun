(defproject luhhujbb/clj-aliyun "0.1.0"
  :description "Basic clojure lib to interact with aliyun ECS API"
  :url "https://github.com/luhhujbb/clj-aliyun"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.data "0.1.1"]
                 ;; logging stuff
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.19"]
                 [org.slf4j/slf4j-log4j12 "1.7.19"]
                 [org.slf4j/jcl-over-slf4j "1.7.19"]
                 [log4j "1.2.17"]
                 [cheshire "5.6.3"]
                  ;;alicloud
                 [com.aliyun/aliyun-java-sdk-core "3.0.8"]
                 [com.aliyun/aliyun-java-sdk-ecs "3.0.0"]]
 :source-paths ["src/clj"]
 :java-source-paths ["src/java"]
 :profiles {:uberjar {:aot :all}
            :dev {:aot :all}})
