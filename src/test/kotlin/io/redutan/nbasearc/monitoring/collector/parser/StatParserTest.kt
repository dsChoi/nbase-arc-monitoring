package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.parser.ByteValue.Unit.GB
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.LocalDateTime

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
        val stat = statParser.parse(now, line)
        // then
        assertStat(stat, now, 24, 12, 6006, ByteValue(69.40, GB), 2_110, 153_630_000, 16_450_000, 2_020_000, 24_090)
    }

    private fun assertStat(stat: Stat, datetime: LocalDateTime, redis: Long, pg: Long, connection: Long, mem: ByteValue, ops: Long,
                           hits: Long, misses: Long, keys: Long, expires: Long) {
        assertThat(stat, equalTo(Stat(datetime, redis, pg, connection, mem, ops, hits, misses, keys, expires)))
    }

    @Test
    fun testParse_Another() {
        // given
        val now = LocalDateTime.now()
        val line = "| 02:12 |    24 |   12 |       6008 |  69.57 GB |  6.03 K | 154.21 M |  16.50 M |   2.03 M |   25.24 K |"
        // when
        val stat = statParser.parse(now, line)
        // then
        assertStat(stat, now, 24, 12, 6008, ByteValue(69.57, GB), 6_030, 154_210_000, 16_500_000, 2_030_000, 25_240)
    }

    @Test
    fun testParse_ConnectionTimeoutException() {
        // given
        val now = LocalDateTime.now()
        val line = "              ConnectionTimeoutException                              "
        // when
        val stat = statParser.parse(now, line)
        // then
        assertThat(stat.isError(), CoreMatchers.equalTo(true))
        assertThat(stat.errorDescription, CoreMatchers.equalTo("ConnectionTimeoutException"))
    }

    @Test
    fun testParse_Unknowns() {
        // given
        val now = LocalDateTime.now()
        val lines = listOf(
                "+-------------------------------------------------------------------------------------------------------------------------------+",
                "|  2017-03-24 01:59:42, CLUSTER:ticketlink_cluster_1                                                                            |",
                "|  Time |  <= 1ms |  <= 2ms |  <= 4ms |  <= 8ms | <= 16ms | <= 32ms | <= 64ms |  <= 128 |  <= 256 |  <= 512 | <= 1024 |  > 1024 |",
                "+------------------------------------------------------------------------------------------------------+",
                "|  2017-03-24 02:02:12, CLUSTER:ticketlink_cluster_1                                                   |",
                "|  Time | Redis |  PG  | Connection |    Mem    |   OPS   |   Hits   |  Misses  |   Keys   |  Expires  |"
        )
        print("""lines.size = ${lines.size}""")
        lines
                .map { statParser.parse(now, it) }
                .forEach { assertThat(it.isUnknown(), CoreMatchers.equalTo(true)) }
    }
}

