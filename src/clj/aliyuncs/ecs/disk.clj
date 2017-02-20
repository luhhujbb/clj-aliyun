(ns aliyuncs.ecs.disk
  (:import [com.aliyuncs.ecs.model.v20140526
            DescribeDisksRequest
            DescribeDisksResponse
            CreateDiskRequest
            CreateDiskResponse
            DeleteDiskRequest
            DeleteDiskResponse
            AttachDiskRequest
            AttachDiskResponse
            DetachDiskRequest
            DetachDiskResponse
            ResizeDiskRequest
            ResizeDiskResponse])
    (:require [aliyuncs.core :as acs])
    (:use [clojure.java.data]))
