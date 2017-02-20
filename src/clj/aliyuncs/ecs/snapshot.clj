(ns aliyuncs.ecs.snapshot
  (:import [com.aliyuncs.ecs.model.v20140526
            DescribeSnapshotsRequest
            DescribeSnapshotsResponse
            CreateSnapshotRequest
            CreateSnapshotResponse
            DeleteSnapshotRequest
            DeleteSnapshotResponse])
    (:require [aliyuncs.core :as acs])
    (:use [clojure.java.data]))
