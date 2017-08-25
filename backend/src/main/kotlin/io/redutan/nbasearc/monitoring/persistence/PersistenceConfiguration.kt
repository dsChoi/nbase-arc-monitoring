package io.redutan.nbasearc.monitoring.persistence

import com.couchbase.client.java.CouchbaseAsyncCluster

// FIXME clusterNode
val cluster = CouchbaseAsyncCluster.create()!!

val bucket = cluster.openBucket("nm")
