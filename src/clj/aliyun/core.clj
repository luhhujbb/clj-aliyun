(ns aliyun.core
  (:import [com.linkfluence.aliyun BasicICredentialProvider]
           [com.aliyuncs.profile DefaultProfile]
           [com.aliyuncs DefaultAcsClient AcsResponse]
           [com.aliyuncs.exceptions ClientException ServerException])
  (:require [clojure.tools.logging :as log]))

(defn mk-creds-provider
  [access-key secret-key]
  (let [^BasicICredentialProvider  provider(.BasicICredentialProvider access-key secret-key)]
    provider))

(defn mk-acs-profile
  [region access-key secret-key]
  (let [^BasicICredentialProvider creds-provider (mk-creds-provider access-key secret-key)
        ^DefaultProfile profile (DefaultProfile/getProfile region creds-provider)]
    profile))

(defn mk-acs-client
  [region access-key secret-key]
    (let [^DefaultProfile profile (mk-acs-profile region access-key secret-key)
          ^DefaultAcsClient client (DefaultAcsClient. profile)]
    client))

(defn get-response
  [^DefaultAcsClient client request]
  (try
    (.getAcsResponse client request)
    (catch ServerException e
      (log/error "Server Exception :" e))
    (catch ClientException e
      (log/error "Client Exception :" e))))

(defn do-action
  [^DefaultAcsClient client action-request]
  (try
    (.doAction client action-request)
    (catch ServerException e
      (log/error "Server Exception :" e))
    (catch ClientException e
      (log/error "Client Exception :" e))))
