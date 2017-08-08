package io.redutan.nbasearc.monitoring.collector

import io.redutan.nbasearc.monitoring.collector.parser.ByteValue
import io.redutan.nbasearc.monitoring.collector.parser.EMPTY_BYTE_VALUE
import java.time.LocalDateTime

val UNKNOWN_STAT: Stat = Stat(LocalDateTime.MIN)

data class Stat(override val loggedAt: LocalDateTime,
                val redis: Long,
                val pg: Long,
                val connection: Long,
                val mem: ByteValue,
                val ops: Long,
                val hits: Long,
                val misses: Long,
                val keys: Long,
                val expires: Long,
                override val errorDescription: String = "")
    : NbaseArcLog {

    constructor(loggedAt: LocalDateTime, errorDescription: String = "")
            : this(loggedAt, -1, -1, -1, EMPTY_BYTE_VALUE, -1, -1, -1, -1, -1, errorDescription)

    override fun isUnknown(): Boolean {
        return this == UNKNOWN_STAT
    }
}
