@file:Suppress("UNCHECKED_CAST")

package io.redutan.nbasearc.monitoring.collector

import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author myeongju.jung
 */
interface LogPublisherFactory {
    fun <T : NbaseArcLog> getLogPublisher(logTypeId: LogTypeId<T>): LogPublishable<T>
}

object ArcCliLogPublisherFactory : LogPublisherFactory{
    private val logPublishers = ConcurrentHashMap<LogTypeId<out NbaseArcLog>, LogPublishable<out NbaseArcLog>>()

    override fun <T : NbaseArcLog> getLogPublisher(logTypeId: LogTypeId<T>): LogPublishable<T> {
        return logPublishers.computeIfAbsent(logTypeId,
                { ArcCliLogPublisher(it.clusterId, it.logType as LogType<T>) }) as LogPublishable<T>
    }
}