package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.ClusterId
import io.redutan.nbasearc.monitoring.collector.Parser
import io.redutan.nbasearc.monitoring.collector.Stat
import java.time.LocalDateTime

/**
 *
 * @author myeongju.jung
 */
class StatParser : Parser<Stat> {
    companion object {
        private const val IDX_REDIS = 2
        private const val IDX_PG = 3
        private const val IDX_CONNECTION = 4
        private const val IDX_MEM = 5
        private const val IDX_OPS = 6
        private const val IDX_HITS = 7
        private const val IDX_MISSES = 8
        private const val IDX_KEYS = 9
        private const val IDX_EXPIRES = 10

        private const val STAT_STRING_LENGTH = 104
        private const val STAT_STRING_SPLIT_SIZE = 12
    }

    override fun parse(clusterId: ClusterId, current: LocalDateTime, line: String): Stat {
        if (line.length != STAT_STRING_LENGTH) {
            return Stat.error(clusterId, current, line.trim())
        }
        if (line.startsWith("|  Time |")) {
            return Stat.unknown()
        }
        val items = line.split(delimiters = "|")
        if (items.size != STAT_STRING_SPLIT_SIZE) {
            return Stat.unknown()
        }
        try {
            val redis = items[IDX_REDIS].toLogNumber()
            val pg = items[IDX_PG].toLogNumber()
            val connection = items[IDX_CONNECTION].toLogNumber()
            val mem = items[IDX_MEM].toByteValue()
            val ops = items[IDX_OPS].toLogNumber()
            val hits = items[IDX_HITS].toLogNumber()
            val misses = items[IDX_MISSES].toLogNumber()
            val keys = items[IDX_KEYS].toLogNumber()
            val expires = items[IDX_EXPIRES].toLogNumber()

            return Stat(clusterId, current, redis, pg, connection, mem, ops, hits, misses,
                    keys, expires)
        } catch (e: Throwable) {
            return Stat.error(clusterId, current, e.localizedMessage)
        }
    }
}

