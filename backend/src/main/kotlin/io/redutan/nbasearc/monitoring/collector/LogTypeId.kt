package io.redutan.nbasearc.monitoring.collector

/**
 *
 * @author myeongju.jung
 */
data class LogTypeId<T : NbaseArcLog>(val logType: LogType<T>, val clusterId: ClusterId)