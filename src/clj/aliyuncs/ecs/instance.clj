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

(defn create-instance
  "Create an ecs instance and return instance-id"
  [client {:keys [image-id instance-type security-group-id]}]
  (let [^CreateInstanceRequest create-req (CreateInstanceRequest.)]
    (-> create-req
      (.setImageId image-id)
      (.setInstanceType instance-type)
      (.setSecurityGroupId security-group-id))
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
