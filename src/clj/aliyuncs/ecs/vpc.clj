(ns aliyuncs.ecs.vpc
  (:import [com.aliyuncs.ecs.model.v20140526
            CreateVpcRequest
            CreateVpcResponse
            DeleteVpcRequest
            DeleteVpcResponse
            DescribeVpcsRequest
            DescribeVpcsResponse
            ModifyVpcAttributeRequest
            ModifyVpcAttributeResponse])
    (:require [aliyuncs.core :as acs])
    (:use [clojure.java.data]))

(defn create-vpc
  "Create an ecs vpc and return vpc-id"
  [client {:keys [vpc-name vpc-desc vpc-cidr vpc-user-cidr]}]
  (let [^CreateVpcRequest create-req (CreateVpcRequest.)]
    (-> create-req
      (.setVpcName vpc-name)
      (.setDescription vpc-desc)
      (.setCidrBlock vpc-cidr))
    (when-not (nil? vpc-user-cidr)
      (.setUserCidr create-req vpc-user-cidr))
    (let [^CreateVpcResponse create-resp (acs/get-response client create-req)]
     (.getVpcId create-resp))))

(defn describe-vpcs
 "Describe vpcs"
 [client]
 (let [describe-req (DescribeVpcsRequest.)
       ^DescribeVpcsResponse describe-resp (acs/get-response client describe-req)]
       (from-java describe-resp)))

(defn describe-vpc
 "Describe a single vpc"
 [client vpc-id]
 (let [describe-req (DescribeVpcsRequest.)]
   (.setVpcId describe-req vpc-id)
   (let [^DescribeVpcsResponse describe-resp (acs/get-response client describe-req)]
       (from-java describe-resp))))

(defn delete-vpc
  "Delete a vpc"
  [client vpc-id]
  (let [^DeleteVpcRequest delete-req (DeleteVpcRequest.)]
    (.setVpcId delete-req vpc-id)
    (acs/do-action client delete-req)))
