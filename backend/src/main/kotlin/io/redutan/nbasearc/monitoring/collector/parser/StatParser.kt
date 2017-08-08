package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.Parser
import io.redutan.nbasearc.monitoring.collector.Stat
import io.redutan.nbasearc.monitoring.collector.UNKNOWN_STAT
import java.time.LocalDateTime

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

/**
 *
 * @author myeongju.jung
 */
class StatParser : Parser<Stat> {
    override fun parse(current: LocalDateTime, line: String): Stat {
        if (line.contains("Exception")) {
            return Stat(current, line.trim())
        }
        if (line.length != STAT_STRING_LENGTH) {
            return UNKNOWN_STAT
        }
        val items = line.split(delimiters = "|")
        if (items.size != STAT_STRING_SPLIT_SIZE) {
            return UNKNOWN_STAT
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

            return Stat(current, redis, pg, connection, mem, ops, hits, misses,
                    keys, expires)
        } catch (e: Throwable) {
            return UNKNOWN_STAT
        }
    }
}

