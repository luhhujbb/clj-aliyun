(ns aliyuncs.ecs.snapshot
  (:import [com.aliyuncs.ecs.model.v20140526
            DescribeSnapshotsRequest
            DescribeSnapshotsResponse
            CreateSnapshotRequest
            CreateSnapshotRequest$Tag
            CreateSnapshotResponse
            DeleteSnapshotRequest])
    (:require [aliyuncs.core :as acs]
              [clojure.tools.logging :as log])
    (:use [clojure.java.data]))


(defn mk-tag
    [tag]
    (let [^CreateSnapshotRequest$Tag ali-tag (CreateSnapshotRequest$Tag.)]
    (doto
        ali-tag
        (.setKey (:key tag))
        (.setValue (:value tag)))
        ali-tag))

(defn set-tags
  [^CreateSnapshotRequest create-req tags]
    (.setTags create-req (map mk-tag tags)))

(defn describe-snapshots
  "Describe snapshots"
  [client & [{:keys [page page-size disk-id instance-id]}]]
  (let [^DescribeSnapshotsRequest describe-req (DescribeSnapshotsRequest.)]
    (when page
      (.setPageNumber describe-req (int page)))
    (when (and page-size (< page-size 50))
      (.setPageSize describe-req (int page-size)))
    (when disk-id
      (.setDiskId describe-req disk-id))
    (when instance-id
       (.setInstanceId describe-req instance-id))
    (let [^DescribeSnapshotsResponse describe-resp (acs/get-response client describe-req)]
      (from-java describe-resp))))

(defn describe-snapshot
  "Describe snapshot"
  [client snapshot-id]
  (let [^DescribeSnapshotsRequest describe-req (doto (DescribeSnapshotsRequest.)
                        (.setSnapshotIds (acs/string-json-array [snapshot-id])))
        ^DescribeSnapshotsResponse describe-resp (acs/get-response client describe-req)]
        (from-java describe-resp)))

(defn create-snapshot
  "Create a disk snapshot"
  [client {:keys [name disk-id tags]}]
  (let [^CreateSnapshotRequest create-req (CreateSnapshotRequest.)]
    (when disk-id
      (.setDiskId create-req disk-id))
    (when name
      (.setSnapshotName create-req name))
    (when tags
      (set-tags create-req tags))
    (let [^CreateSnapshotResponse create-resp (acs/get-response client create-req)
          snapshot-id (.getSnapshotId create-resp)]
      (log/info "[ECS][SNAPSHOT] creation of snapshot (" snapshot-id ") for disk" disk-id "successfull")
      snapshot-id)))

(defn delete-snapshot
  "Delete a snapshot"
  [client snapshot-id]
  (let [^DeleteSnapshotRequest delete-req (doto (DeleteSnapshotRequest.)
                                            (.setSnapshotId snapshot-id))]
    (acs/do-action client delete-req)))
