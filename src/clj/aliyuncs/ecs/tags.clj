(ns aliyuncs.ecs.tags
  (:import [com.aliyuncs.ecs.model.v20140526
            AddTagsRequest
            DescribeTagsRequest
            DescribeTagsResponse
            RemoveTagsRequest
            DescribeResourceByTagsRequest
            DescribeResourceByTagsResponse])
    (:require [aliyuncs.core :as acs])
    (:use [clojure.java.data]))

(def res-type
  {:instance "instance"
   :image "image"
   :snapshot "snapshot"
   :disk "disk"
   :security-group "securitygroup"})

(defn set-tag
  [^AddTagsRequest add-req i key value]
  (condp = i
    1 (do
        (.setTag1Key add-req key)
        (.setTag1Value add-req value))
    2  (do
        (.setTag2Key add-req key)
        (.setTag2Value add-req value))
    3  (do
        (.setTag3Key add-req key)
        (.setTag3Value add-req value))
    4  (do
        (.setTag4Key add-req key)
        (.setTag4Value add-req value))
    5  (do
        (.setTag5Key add-req key)
        (.setTag5Value add-req value))
    nil))

;;OK it's the same function
(defn set-rm-tag
  [^RemoveTagsRequest rm-req i key value]
  (condp = i
    1 (do
        (.setTag1Key rm-req key)
        (.setTag1Value rm-req value))
    2  (do
        (.setTag2Key rm-req key)
        (.setTag2Value rm-req value))
    3  (do
        (.setTag3Key rm-req key)
        (.setTag3Value rm-req value))
    4  (do
        (.setTag4Key rm-req key)
        (.setTag4Value rm-req value))
    5  (do
        (.setTag5Key rm-req key)
        (.setTag5Value rm-req value))
    nil))

(defn set-tags
  [add-req tags]
  (loop [tgs tags
         tn 1]
    (when-let [tag (first tgs)]
      (let [[k v] tag]
        (set-tag add-req tn (name k) v)
        (recur (rest tgs) (inc tn))))))

(defn add-tags
  "Add Tags"
  [client {:keys [resource-type resource-id tags]}]
  (let [^AddTagsRequest add-req (AddTagsRequest.)]
    (when (and resource-type resource-id)
      (.setResourceType add-req (res-type resource-type))
      (.setResourceId add-req resource-id)
      (when tags
        (set-tags add-req tags))
      (acs/do-action client add-req))))

(defn remove-tag
  "remove a tag with specified index and k v"
  [client {:keys [resource-type resource-id tag-index key value]}]
  (let [^RemoveTagsRequest rm-req (RemoveTagsRequest.)]
    (when (and resource-type resource-id)
      (.setResourceType rm-req (res-type resource-type))
      (.setResourceId rm-req resource-id)
      (set-rm-tag rm-req tag-index key value)
      (acs/do-action client rm-req))))
