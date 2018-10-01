(ns aliyuncs.ecs.disk
  (:import [com.aliyuncs.ecs.model.v20140526
            DescribeDisksRequest
            DescribeDisksResponse
            CreateDiskRequest
            CreateDiskRequest$Tag
            CreateDiskResponse
            DeleteDiskRequest
            AttachDiskRequest
            DetachDiskRequest
            ResizeDiskRequest])
    (:require [aliyuncs.core :as acs]
              [clojure.tools.logging :as log])
    (:use [clojure.java.data]))

(defn- mk-tag
   [tag]
   (let [^CreateDiskRequest$Tag ali-tag (CreateDiskRequest$Tag.)]
   (doto
       ali-tag
       (.setKey (:key tag))
       (.setValue (:value tag)))
       ali-tag))

(defn- set-tags
    [^CreateDiskRequest create-req tags]
        (.setTags create-req (map mk-tag tags)))

(def disk-category
  {:cloud "cloud"
   :efficiency "cloud_efficiency"
   :ssd "cloud_ssd"})

(defn size-ok?
  [size]
  (< 20 size 2000))

(defn describe-disks
  "Describe disks"
  [client & [{:keys [page page-size]}]]
  (let [describe-req (DescribeDisksRequest.)]
    (when page
      (.setPageNumber describe-req (int page)))
    (when (and page-size (< page-size 50))
      (.setPageSize describe-req (int page-size)))
    (let [^DescribeDisksResponse describe-resp (acs/get-response client describe-req)]
      (from-java describe-resp))))

(defn describe-disk
  "Describe disks"
  [client disk-id]
  (let [describe-req (doto (DescribeDisksRequest.)
                        (.setDiskIds (acs/string-json-array [disk-id])))
        ^DescribeDisksResponse describe-resp (acs/get-response client describe-req)]
        (from-java describe-resp)))

(defn create-disk
  "Create a new disk"
  [client {:keys [name category size snapshot-id tags]}]
  (let [^CreateDiskRequest create-req (CreateDiskRequest.)]
    (when (and size (size-ok? size))
      (.setSize create-req (int size))
      (when (category disk-category)
        (.setDiskCategory create-req (category disk-category)))
      (when name
        (.setDiskName create-req name))
      (when snapshot-id
        (.setSnapshotId create-req snapshot-id))
      (when tags
        (set-tags create-req tags))
      (let [^CreateDiskResponse create-resp (acs/get-response client create-req)
            disk-id (.getDiskId create-resp)]
        (log/info "[ECS][DISK] Disk with id" disk-id "created")
        disk-id))))

(defn delete-disk
  "Delete a disk"
  [client disk-id]
  (let [^DeleteDiskRequest delete-req (doto
                                        (DeleteDiskRequest.)
                                        (.setDiskId disk-id))]
      (acs/do-action client delete-req)))

(defn attach-disk
  "Attach a disk to an instance"
  [client disk-id instance-id]
  (let [^AttachDiskRequest attach-req (doto
                                        (AttachDiskRequest.)
                                          (.setDiskId disk-id)
                                          (.setInstanceId instance-id))]
      (acs/do-action client attach-req)))

(defn detach-disk
  "Detach a disk to an instance"
  [client disk-id instance-id]
  (let [^DetachDiskRequest detach-req (doto
                                        (DetachDiskRequest.)
                                          (.setDiskId disk-id)
                                          (.setInstanceId instance-id))]
      (acs/do-action client detach-req)))

(defn resize-disk
  "Resize a disk "
  [client disk-id new-size]
  (let [^ResizeDiskRequest resize-req (doto
                                        (ResizeDiskRequest.)
                                          (.setDiskId disk-id)
                                          (.setNewSize new-size))]
      (acs/do-action client resize-req)))
