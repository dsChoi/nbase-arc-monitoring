package io.redutan.nbasearc.monitoring.persistence

import io.redutan.nbasearc.monitoring.collector.Latency

class LatencyRepository : LogRepository<Latency> {
    override fun findOne(): Latency {
        // TODO
        return Latency.unknown()
    }

    override fun save(entity: Latency): Latency {
        // TODO
        return Latency.unknown()
    }
}
