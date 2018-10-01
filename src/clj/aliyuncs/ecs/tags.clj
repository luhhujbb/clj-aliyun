(ns aliyuncs.ecs.tags
  (:import [com.aliyuncs.ecs.model.v20140526
            AddTagsRequest
            AddTagsRequest$Tag
            DescribeTagsRequest
            DescribeTagsResponse
            RemoveTagsRequest
            RemoveTagsRequest$Tag
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

(defn- mk-add-tag
    [tag]
    (let [^AddTagsRequest$Tag ali-tag (AddTagsRequest$Tag.)]
    (doto
        ali-tag
        (.setKey (:key tag))
        (.setValue (:value tag)))
        ali-tag))

(defn- mk-rm-tag
    [tag]
    (let [^RemoveTagsRequest$Tag ali-tag (RemoveTagsRequest$Tag.)]
    (doto
        ali-tag
        (.setKey (:key tag))
        (.setValue (:value tag)))
        ali-tag))

(defn- set-add-tags
  [^AddTagsRequest add-req tags]
    (when (>= 20 (count tags)))
        (.setTags add-req (map mk-add-tag tags)))

(defn- set-rm-tags
  [^RemoveTagsRequest add-req tags]
    (when (>= 20 (count tags)))
        (.setTags add-req (map mk-rm-tag tags)))

(defn add-tags
  "Add Tags"
  [client {:keys [resource-type resource-id tags]}]
  (let [^AddTagsRequest add-req (AddTagsRequest.)]
    (when (and resource-type resource-id)
      (.setResourceType add-req (res-type resource-type))
      (.setResourceId add-req resource-id)
      (when tags
        (set-add-tags add-req tags)
        (acs/do-action client add-req)))))

(defn remove-tags
  "remove a tag with specified index and k v"
  [client {:keys [resource-type resource-id tags]}]
  (let [^RemoveTagsRequest rm-req (RemoveTagsRequest.)]
    (when (and resource-type resource-id)
      (.setResourceType rm-req (res-type resource-type))
      (.setResourceId rm-req resource-id)
      (when tags
          (set-rm-tags rm-req tags)
          (acs/do-action client rm-req)))))
