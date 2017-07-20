package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
import java.time.LocalDateTime

/**
 *
 * @author myeongju.jung
 */
class LatencyLogPublisher : LogPublishable<Latency> {
    override fun observe(interval: Int): Observable<Latency> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class Latency(val loggedAt: LocalDateTime,
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
                   override val errorDescription: String = "") : NbaseArcLog {
    override fun isError(): Boolean {
        return errorDescription.isNotEmpty()
    }

    fun isUnknown(): Boolean {
        return this == UNKNOWN_LATENCY
    }

    fun syncHour(headerDateTime: LocalDateTime): Latency {
        if (this.loggedAt >= headerDateTime) {
            return this
        }
        println("syncHour : " + loggedAt)
        return this.copy(loggedAt = loggedAt.plusHours(1),
                under1ms = under1ms, under2ms = under2ms, under4ms = under4ms, under8ms = under8ms, under16ms = under16ms, under32ms = under32ms,
                under64ms = under64ms, under128ms = under128ms, under256ms = under256ms, under512ms = under512ms, under1024ms = under1024ms,
                over1024ms = over1024ms)
    }

    constructor(loggedAt: LocalDateTime, errorDescription: String = "") : this(loggedAt, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            errorDescription)
}

val UNKNOWN_LATENCY = Latency(LocalDateTime.MIN)