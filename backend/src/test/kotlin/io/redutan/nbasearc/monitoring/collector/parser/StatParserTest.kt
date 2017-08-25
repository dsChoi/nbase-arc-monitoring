package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.ClusterId
import io.redutan.nbasearc.monitoring.collector.Stat
import io.redutan.nbasearc.monitoring.collector.parser.ByteValue.Unit.GB
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 * @author myeongju.jung
 */
class StatParserTest {
    var statParser = StatParser()

    @Test
    fun testParse() {
        // given
        val now = LocalDateTime.now()
        val line = "| 59:42 |    24 |   12 |       6006 |  69.40 GB |  2.11 K | 153.63 M |  16.45 M |   2.02 M |   24.09 K |"
        // when
        val stat = statParser.parse(ClusterId.empty(), now, line)
        // then
        assertStat(stat, now, 24, 12, 6006, ByteValue(69.40, GB), 2_110, 153_630_000, 16_450_000, 2_020_000, 24_090)
    }

    private fun assertStat(stat: Stat, datetime: LocalDateTime, redis: Long, pg: Long, connection: Long, mem: ByteValue, ops: Long,
                           hits: Long, misses: Long, keys: Long, expires: Long) {
        assertEquals(Stat(ClusterId.empty(), datetime, redis, pg, connection, mem, ops, hits, misses, keys, expires), stat)
    }

    @Test
    fun testParse_Another() {
        // given
        val now = LocalDateTime.now()
        val line = "| 02:12 |    24 |   12 |       6008 |  69.57 GB |  6.03 K | 154.21 M |  16.50 M |   2.03 M |   25.24 K |"
        // when
        val stat = statParser.parse(ClusterId.empty(), now, line)
        // then
        assertStat(stat, now, 24, 12, 6008, ByteValue(69.57, GB), 6_030, 154_210_000, 16_500_000, 2_030_000, 25_240)
    }

    @Test
    fun testParse_ConnectionTimeoutException() {
        // given
        val now = LocalDateTime.now()
        val line = "              ConnectionTimeoutException                              "
        // when
        val stat = statParser.parse(ClusterId.empty(), now, line)
        // then
        assertTrue(stat.isError())
        assertEquals("ConnectionTimeoutException", stat.errorDescription)
    }

    @Test
    fun testParse_Unknowns() {
        // given
        val now = LocalDateTime.now()
        val lines = listOf(
                "+------------------------------------------------------------------------------------------------------+",
                "|  2017-03-24 02:02:12, CLUSTER:ticketlink_cluster_1                                                   |",
                "|  Time | Redis |  PG  | Connection |    Mem    |   OPS   |   Hits   |  Misses  |   Keys   |  Expires  |"
        )
        assertTrue(lines.size == 3)
        lines
                .map { statParser.parse(ClusterId.empty(), now, it) }
                .forEach { assertTrue(it.isUnknown()) }
    }
}

