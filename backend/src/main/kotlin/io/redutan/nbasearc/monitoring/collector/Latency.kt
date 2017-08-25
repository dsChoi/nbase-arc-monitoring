package io.redutan.nbasearc.monitoring.collector

import java.time.LocalDateTime

data class Latency(override val clusterId: ClusterId,
                   override val loggedAt: LocalDateTime,
                   val under1ms: Long,
                   val under2ms: Long,
                   val under4ms: Long,
                   val under8ms: Long,
                   val under16ms: Long,
                   val under32ms: Long,
                   val under64ms: Long,
                   val under128ms: Long,
                   val under256ms: Long,
                   val under512ms: Long,
                   val under1024ms: Long,
                   val over1024ms: Long,
                   override val errorDescription: String? = null)
    : NbaseArcLog {

    companion object {
        private val UNKNOWN = Latency(ClusterId.empty(), LocalDateTime.MIN, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
        fun unknown(): Latency {
            return UNKNOWN
        }

        fun error(clusterId: ClusterId, loggedAt: LocalDateTime, errorDescription: String): Latency {
            return Latency(clusterId, loggedAt, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, errorDescription = errorDescription)
        }
    }

    override fun isUnknown(): Boolean {
        return this == UNKNOWN
    }
}
