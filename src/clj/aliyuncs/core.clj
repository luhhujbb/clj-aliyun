(ns aliyuncs.core
  (:import [com.linkfluence.aliyun BasicICredentialProvider]
           [com.aliyuncs.profile DefaultProfile]
           [com.aliyuncs DefaultAcsClient AcsResponse]
           [com.aliyuncs.exceptions ClientException ServerException])
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [cheshire.core :refer :all]))
        
(def catch-errors (atom true))

(defn catch-errors?
    ([] @catch-errors)
    ([tf]
        (reset! catch-errors tf)))

(defn string-json-array
  [coll]
  (generate-string coll))

(defn mk-creds-provider
  [access-key secret-key]
  (let [^BasicICredentialProvider  provider (BasicICredentialProvider. access-key secret-key)]
    provider))

(defn mk-acs-profile
  [^String region access-key secret-key]
  (let [^BasicICredentialProvider creds-provider (mk-creds-provider access-key secret-key)
        ^DefaultProfile profile (DefaultProfile/getProfile region)]
        (.setCredentialsProvider profile creds-provider)
    profile))

(defn mk-acs-client
  [region access-key secret-key]
    (let [^DefaultProfile profile (mk-acs-profile region access-key secret-key)
          ^DefaultAcsClient client (DefaultAcsClient. profile)]
    client))

(defn get-response
  [^DefaultAcsClient client request]
  (if @catch-errors
    (try
        (.getAcsResponse client request)
        (catch ServerException e
            (log/error "Server Exception :" e))
        (catch ClientException e
            (log/error "Client Exception :" e)))
    (.getAcsResponse client request)))

(defn do-action
  [^DefaultAcsClient client action-request]
  (if @catch-errors
    (try
        (.doAction client action-request)
            (catch ServerException e
                (log/error "Server Exception :" e))
            (catch ClientException e
                (log/error "Client Exception :" e)))
    (.doAction client action-request)))
