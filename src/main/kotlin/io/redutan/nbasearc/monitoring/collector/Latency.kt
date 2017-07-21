package io.redutan.nbasearc.monitoring.collector

import java.time.LocalDateTime

val UNKNOWN_LATENCY = Latency(LocalDateTime.MIN)

data class Latency(override val loggedAt: LocalDateTime,
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
                   override val errorDescription: String = "")
    : NbaseArcLog {

    constructor(loggedAt: LocalDateTime, errorDescription: String = "") :
            this(loggedAt, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, errorDescription)

    override fun isUnknown(): Boolean {
        return this == UNKNOWN_LATENCY
    }
}
