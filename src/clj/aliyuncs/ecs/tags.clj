(ns aliyuncs.ecs.tags
  (:import [com.aliyuncs.ecs.model.v20140526
            AddTagsRequest
            AddTagsResponse
            DescribeTagsRequest
            DescribeTagsResponse
            RemoveTagsRequest
            RemoveTagsResponse
            DescribeResourceByTagsRequest
            DescribeResourceByTagsResponse])
    (:require [aliyuncs.core :as acs])
    (:use [clojure.java.data]))
