(ns aliyuncs.ecs.snapshot
  (:import [com.aliyuncs.ecs.model.v20140526
            DescribeSnapshotsRequest
            DescribeSnapshotsResponse
            CreateSnapshotRequest
            CreateSnapshotResponse
            DeleteSnapshotRequest])
    (:require [aliyuncs.core :as acs]
              [clojure.tools.logging :as log])
    (:use [clojure.java.data]))

(defn set-tag
  [^CreateSnapshotRequest create-req i key value]
  (condp = i
    1 (do
        (.setTag1Key create-req key)
        (.setTag1Value create-req value))
    2  (do
        (.setTag2Key create-req key)
        (.setTag2Value create-req value))
    3  (do
        (.setTag3Key create-req key)
        (.setTag3Value create-req value))
    4  (do
        (.setTag4Key create-req key)
        (.setTag4Value create-req value))
    5  (do
        (.setTag5Key create-req key)
        (.setTag5Value create-req value))
    nil))

(defn set-tags
  [create-req tags]
  (loop [tgs tags
         tn 1]
    (when-let [tag (first tgs)]
      (let [[k v] tag]
        (set-tag create-req tn (name k) v)
        (recur (rest tgs) (inc tn))))))

(defn describe-snapshots
  "Describe snapshots"
  [client & [{:keys [page page-size disk-id instance-id]}]]
  (let [describe-req (DescribeSnapshotsRequest.)]
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
  (let [describe-req (doto (DescribeSnapshotsRequest.)
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
