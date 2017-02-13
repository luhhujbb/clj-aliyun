(ns aliyun.core
  (:import [com.linkfluence.aliyun BasicICredentialProvider])
  (:require [clojure.tools.logging :as log])
  (:gen-class))

(defn mk-credential
  [access-key secret-key]
  (.BasicICredentialProvider access-key secret-key))
