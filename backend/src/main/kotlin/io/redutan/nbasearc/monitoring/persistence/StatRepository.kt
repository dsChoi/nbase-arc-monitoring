package io.redutan.nbasearc.monitoring.persistence

import io.redutan.nbasearc.monitoring.collector.Stat

class StatRepository : LogRepository<Stat> {
    override fun findOne(): Stat {
        // TODO
        return Stat.unknown()
    }

    override fun save(entity: Stat) : Stat {
        // TODO
        return Stat.unknown()
    }
}
