package io.redutan.nbasearc.monitoring.collector

import java.time.LocalDateTime

/**
 *
 * @author myeongju.jung
 */
interface Parser<out T : NbaseArcLog> {
    fun parse(clusterId: ClusterId, current: LocalDateTime, line: String): T
}

interface HeaderParser {
    fun isHeader(line: String): Boolean

    fun parse(clusterId: ClusterId, line: String): NbaseArcLogHeader
}

data class NbaseArcLogHeader(override val clusterId: ClusterId,
                             override val loggedAt: LocalDateTime,
                             override val errorDescription: String? = null)
    : NbaseArcLog {

    companion object {
        private val UNKNOWN_HEADER = NbaseArcLogHeader(ClusterId.empty(), LocalDateTime.now())
        fun unknown(): NbaseArcLogHeader = UNKNOWN_HEADER
    }

    override fun isUnknown(): Boolean {
        return this == UNKNOWN_HEADER
    }
}
