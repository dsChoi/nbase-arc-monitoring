package io.redutan.nbasearc.monitoring.collector

import io.redutan.nbasearc.monitoring.collector.parser.ByteValue
import io.redutan.nbasearc.monitoring.collector.parser.EMPTY_BYTE_VALUE
import java.time.LocalDateTime

data class Stat(override val clusterId: ClusterId,
                override val loggedAt: LocalDateTime,
                val redis: Long,
                val pg: Long,
                val connection: Long,
                val mem: ByteValue,
                val ops: Long,
                val hits: Long,
                val misses: Long,
                val keys: Long,
                val expires: Long,
                override val errorDescription: String? = null)
    : NbaseArcLog {

    companion object {
        private val UNKNOWN = Stat(ClusterId.empty(), LocalDateTime.MIN, -1, -1, -1, EMPTY_BYTE_VALUE, -1, -1, -1, -1, -1)
        fun unknown(): Stat {
            return UNKNOWN
        }

        fun error(clusterId: ClusterId, loggedAt: LocalDateTime, errorDescription: String): Stat {
            return Stat(clusterId, loggedAt, -1, -1, -1, EMPTY_BYTE_VALUE, -1, -1, -1, -1, -1, errorDescription = errorDescription)
        }
    }

    override fun isUnknown(): Boolean {
        return this == UNKNOWN
    }
}
