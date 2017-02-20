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
    (:require [aliyuncs.core :as acs])
    (:use [clojure.java.data]))

(def internet-charge
  {:bandwidth "PayByBandwidth"
   :traffic "PayByTraffic"})

(defn create-instance
  "Create an ecs instance and return instance-id"
  [client {:keys [image-id
                  instance-type
                  security-group-id
                  internet-charge-type
                  internet-max-bandwidth-in
                  internet-max-bandwidth-out]
           :or {internet-charge-type (:bandwidth internet-charge)}
                internet-max-bandwidth-in 200
                internet-max-bandwidth-out 0}]
  (let [^CreateInstanceRequest create-req (CreateInstanceRequest.)]
    (-> create-req
      (.setImageId image-id)
      (.setInstanceType instance-type)
      (.setSecurityGroupId security-group-id)
      (.setInternetChargeType internet-charge-type)
      (.setInternetMaxBandwidthIn internet-charge-type)
      (.setInternetMaxBandwidthOut internet-charge-type))
      (let [^CreateInstanceResponse create-resp (acs/get-response client create-req)]
       (.getInstanceId create-resp))))

(defn describe-instances
  "Describe instances"
  [client]
  (let [describe-req (DescribeInstancesRequest.)
        ^DescribeInstancesResponse describe-resp (acs/get-response client describe-req)]
        (from-java describe-resp)))

(defn describe-instance
  "Describe a single instance"
  [client instance-id]
  (let [describe-req (DescribeInstancesRequest.)]
    (.setInstanceIds describe-req instance-id)
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
