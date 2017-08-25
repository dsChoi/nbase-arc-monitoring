package io.redutan.nbasearc.monitoring.persistence

import io.redutan.nbasearc.monitoring.collector.NbaseArcLog

interface LogRepository<T : NbaseArcLog> {
    fun save(entity : T) : T

    fun findOne() : T
}
