package io.redutan.nbasearc.monitoring.collector

import java.time.LocalDateTime

const val UNKNOWN = "?"
val UNKNOWN_HEADER = NbaseArcLogHeader(LocalDateTime.now(), UNKNOWN)

/**
 *
 * @author myeongju.jung
 */
interface Parser<out T : NbaseArcLog> {
    fun parse(current: LocalDateTime, line: String): T
}

interface HeaderParser {
    fun isHeader(line: String): Boolean

    fun parse(line: String): NbaseArcLogHeader
}

data class NbaseArcLogHeader(val current: LocalDateTime, val clusterName: String, override val errorDescription: String = "")
    : NbaseArcLog {
    override fun isSuccess(): Boolean {
        return !isUnknown() && !isError()
    }

    fun isUnknown(): Boolean {
        return UNKNOWN == clusterName
    }

    override fun isError(): Boolean {
        return errorDescription.isNotEmpty()
    }
}