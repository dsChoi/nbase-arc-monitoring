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

data class Latency(val expectedDateTime: LocalDateTime,
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
                   val over1024ms: Long) : NbaseArcLog
