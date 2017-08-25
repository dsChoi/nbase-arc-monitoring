@file:Suppress("UNCHECKED_CAST")

package io.redutan.nbasearc.monitoring.collector

import io.reactivex.schedulers.Schedulers
import io.redutan.nbasearc.monitoring.logger
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author myeongju.jung
 */
interface LogPublisherFactory {
    fun <T : NbaseArcLog> getLogPublisher(logTypeId: LogTypeId<T>): LogPublishable<T>
}

object ArcCliLogPublisherFactory : LogPublisherFactory{
    val log by logger()

    private val logPublishers = ConcurrentHashMap<LogTypeId<out NbaseArcLog>, LogPublishable<out NbaseArcLog>>()

    override fun <T : NbaseArcLog> getLogPublisher(logTypeId: LogTypeId<T>): LogPublishable<T> {
        return logPublishers.computeIfAbsent(logTypeId, { (logType, clusterId) ->
            val result = ArcCliLogPublisher(clusterId, logType as LogType<T>)
            result.observe()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    logType.repository.save(it)
                }
            return@computeIfAbsent result
        }) as LogPublishable<T>
    }
}
