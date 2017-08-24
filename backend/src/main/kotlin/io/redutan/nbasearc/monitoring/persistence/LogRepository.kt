package io.redutan.nbasearc.monitoring.persistence

import io.redutan.nbasearc.monitoring.collector.NbaseArcLog

interface LogRepository<in T : NbaseArcLog> {
    fun save(entity : T)
}
