@file:Suppress("UNCHECKED_CAST")

package io.redutan.nbasearc.monitoring.collector

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
        val result = logPublishers.computeIfAbsent(logTypeId,
                { ArcCliLogPublisher(it.clusterId, it.logType as LogType<T>) }) as LogPublishable<T>
        if (log.isInfoEnabled)
            log.info("logPublishers count = {}", logPublishers.count())
        return result
    }
}