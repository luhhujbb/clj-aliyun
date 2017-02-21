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
                  internet-max-bandwidth-out]
            ;;by default internet is disabled
           :or {internet-charge-type (:traffic internet-charge)
                internet-max-bandwidth-in 200
                internet-max-bandwidth-out 0
                name hostname ;; if an hostname is set and no name, name is set to hostname
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
            (.setHostName create-req hostname)))
      (let [^CreateInstanceResponse create-resp (acs/get-response client create-req)]
        (log/info "Instance with id" (.getInstanceId create-resp) "successfully created")
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
    (.setInstanceIds describe-req (str "[\"" instance-id "\"]"))
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
