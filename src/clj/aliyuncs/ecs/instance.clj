(ns aliyuncs.ecs.instance
  (:import [com.aliyuncs.ecs.model.v20140526
            DescribeInstancesRequest
            DescribeInstancesResponse
            DescribeInstanceAttributeRequest
            DescribeInstanceAttributeResponse
            DescribeInstanceStatusRequest
            DescribeInstanceStatusResponse
            CreateInstanceRequest
            CreateInstanceResponse
            DeleteInstanceRequest
            DeleteInstanceResponse
            StartInstanceRequest
            StopInstanceRequest])
    (:require [aliyuncs.core :as acs]
              [clojure.tools.logging :as log]
              [clojure.string :as str])
    (:use [clojure.java.data]))

(def internet-charge
  {:bandwidth "PayByBandwidth" ;;not really by default, provoke API error, seems to be deprecated ??
   :traffic "PayByTraffic"})

(defn set-tag
  [^CreateInstanceRequest create-req i key value]
  (condp = i
    1 (do
        (.setTag1Key create-req key)
        (.setTag1Value create-req value))
    2  (do
        (.setTag2Key create-req key)
        (.setTag2Value create-req value))
    3  (do
        (.setTag3Key create-req key)
        (.setTag3Value create-req value))
    4  (do
        (.setTag4Key create-req key)
        (.setTag4Value create-req value))
    5  (do
        (.setTag5Key create-req key)
        (.setTag5Value create-req value))
    nil))

(defn set-tags
  [create-req tags]
  (loop [tgs tags
         tn 1]
    (when-let [tag (first tgs)]
      (let [[k v] tag]
        (set-tag create-req tn (name k) v)
        (recur (rest tgs) (inc tn))))))

(defn create-instance
  "Create an ecs instance and return instance-id"
  [client {:keys [image-id
                  instance-type
                  security-group-id
                  vswitch-id
                  name
                  hostname
                  internet-charge-type
                  internet-max-bandwidth-in
                  internet-max-bandwidth-out
                  tags] ;; a kv map : {:key value}
            ;;by default internet is disabled
           :or {internet-charge-type (:traffic internet-charge)
                internet-max-bandwidth-in 200
                internet-max-bandwidth-out 0
                }}]
  (let [^CreateInstanceRequest create-req
          (doto (CreateInstanceRequest.)
            (.setImageId image-id)
            (.setInstanceType instance-type)
            (.setSecurityGroupId security-group-id)
            (.setInternetChargeType internet-charge-type)
            (.setInternetMaxBandwidthIn (int internet-max-bandwidth-in))
            (.setInternetMaxBandwidthOut (int internet-max-bandwidth-out)))]
          ;;optional but usefull stuffs
          (when-not (nil? vswitch-id) ;;for vpc
            (.setVSwitchId create-req vswitch-id))
          (when-not (nil? name)
            (.setInstanceName create-req name))
          (when-not (nil? hostname)
            (.setHostName create-req hostname))
          (when-not (and (nil? tags) (>= 5 (count tags)))
           (set-tags create-req tags))
      (let [^CreateInstanceResponse create-resp (acs/get-response client create-req)]
        (log/info "Instance with id" (.getInstanceId create-resp) "successfully created")
       (.getInstanceId create-resp))))

(defn describe-instances
  "Describe instances"
  [client & [{:keys [page page-size]}]]
  (let [describe-req (DescribeInstancesRequest.)]
        (when page
          (.setPageNumber describe-req (int page)))
        (when (and page-size (< page-size 50))
          (.setPageSize describe-req (int page-size)))
        (let [^DescribeInstancesResponse describe-resp (acs/get-response client describe-req)]
          (from-java describe-resp))))

(defn describe-instance
  "Describe a single instance"
  [client instance-id]
  (let [describe-req (DescribeInstancesRequest.)]
    (.setInstanceIds describe-req (acs/string-json-array [instance-id]))
    (let [^DescribeInstancesResponse describe-resp (acs/get-response client describe-req)]
        (from-java describe-resp))))

(defn describe-instance-attribute
  "Describe instances attribute"
  [client instance-id]
  (let [describe-req (DescribeInstanceAttributeRequest.)
        ^DescribeInstanceAttributeResponse describe-resp (acs/get-response client describe-req)]
        (from-java describe-resp)))

(defn start-instance
  "Start a created instance"
  [client instance-id]
  (let [^StartInstanceRequest start-req (StartInstanceRequest.)]
    (.setInstanceId start-req instance-id)
    (acs/do-action client start-req)))

(defn stop-instance
  "Stop a running instance"
  [client instance-id]
  (let [^StopInstanceRequest stop-req (StopInstanceRequest.)]
    (.setInstanceId stop-req instance-id)
    (acs/do-action client stop-req)))

(defn delete-instance
  "Delete an instance"
  [client instance-id]
  (let [^DeleteInstanceRequest delete-req (DeleteInstanceRequest.)]
    (.setInstanceId delete-req instance-id)
    (acs/do-action client delete-req)))
