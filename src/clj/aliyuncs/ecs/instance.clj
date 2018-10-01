(ns aliyuncs.ecs.instance
  (:import [com.aliyuncs.ecs.model.v20140526
            DescribeInstancesRequest
            DescribeInstancesResponse
            DescribeInstanceAttributeRequest
            DescribeInstanceAttributeResponse
            DescribeInstanceStatusRequest
            DescribeInstanceStatusResponse
            ModifyInstanceAttributeRequest
            ModifyInstanceAttributeResponse
            CreateInstanceRequest
            CreateInstanceRequest$Tag
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

(defn- mk-tag
   [tag]
   (let [^CreateInstanceRequest$Tag ali-tag (CreateInstanceRequest$Tag.)]
   (doto
       ali-tag
       (.setKey (:key tag))
       (.setValue (:value tag)))
       ali-tag))

(defn- set-tags
    [^CreateInstanceRequest create-req tags]
        (.setTags create-req (map mk-tag tags)))

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
        (log/info "[ECS][INSTANCE] Instance with id" (.getInstanceId create-resp) "successfully created")
       (.getInstanceId create-resp))))

(defn describe-instances
  "Describe instances"
  [client & [{:keys [page page-size]}]]
  (let [^DescribeInstancesRequest describe-req (DescribeInstancesRequest.)]
        (when page
          (.setPageNumber describe-req (int page)))
        (when (and page-size (< page-size 50))
          (.setPageSize describe-req (int page-size)))
        (let [^DescribeInstancesResponse describe-resp (acs/get-response client describe-req)]
          (from-java describe-resp))))

(defn describe-instance
  "Describe a single instance"
  [client instance-id]
  (let [^DescribeInstancesRequest describe-req (DescribeInstancesRequest.)]
    (.setInstanceIds describe-req (acs/string-json-array [instance-id]))
    (let [^DescribeInstancesResponse describe-resp (acs/get-response client describe-req)]
        (from-java describe-resp))))

(defn describe-instance-attribute
  "Describe instances attribute"
  [client instance-id]
  (let [^DescribeInstancesRequest describe-req (DescribeInstanceAttributeRequest.)
        ^DescribeInstanceAttributeResponse describe-resp (acs/get-response client describe-req)]
        (from-java describe-resp)))

(defn rename-instance
    "Set name of an instance"
    [client instance-id instance-name]
    (let [^ModifyInstanceAttributeRequest modify-req (ModifyInstanceAttributeRequest.)]
        (.setInstanceId modify-req instance-id)
        (.setInstanceName modify-req instance-name)
        (acs/do-action client modify-req)))

(defn set-instance-hostname
    "Set hostname of an instance"
    [client instance-id hostname]
    (let [^ModifyInstanceAttributeRequest modify-req (ModifyInstanceAttributeRequest.)]
        (.setInstanceId modify-req instance-id)
        (.setHostName modify-req hostname)
        (acs/do-action client modify-req)))

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
