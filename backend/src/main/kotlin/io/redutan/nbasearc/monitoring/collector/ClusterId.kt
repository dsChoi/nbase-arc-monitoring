package io.redutan.nbasearc.monitoring.collector

/**
 * nbase-arc cluster ID
 * @author myeongju.jung
 */

data class ClusterId(val zkAddress: String, val cluster: String) {
    companion object {
        private val EMPTY = ClusterId("", "")
        fun empty(): ClusterId {
            return EMPTY
        }
    }
}
