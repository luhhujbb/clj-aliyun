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
            DeleteInstanceResponse])
    (:require [aliyuncs.core :as acs])
    (:use [clojure.java.data]))

(defn describe-instances
  [client]
  (let [desc-request (DescribeInstancesRequest.)
        ^DescribeInstancesResponse desc-response (acs/get-response client desc-request)]
        (from-java desc-response)))
