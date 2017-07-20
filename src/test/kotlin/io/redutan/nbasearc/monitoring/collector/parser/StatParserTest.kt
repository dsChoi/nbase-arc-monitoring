package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.parser.ByteValue.Unit.GB
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

//    @Test
//    fun testParse_Another() {
//        // given
//        val now = LocalDateTime.now()
//        val line = "| 11:53 |  1.09 K |      13 |       7 |       2 |      11 |      22 |      33 |      44 |      55 |      66 |      77 |      88 |"
//        // when
//        val latency = statParser.parse(now, line)
//        // then
//        assertStat(latency, now, 1_090, 13, 7, 2, 11, 22, 33, 44, 55, 66, 77, 88)
//    }
//
//    @Test
//    fun testParse_ConnectionTimeoutException() {
//        // given
//        val now = LocalDateTime.now()
//        val line = "              ConnectionTimeoutException                              "
//        // when
//        val latency = statParser.parse(now, line)
//        // then
//        Assert.assertThat(latency.isError(), CoreMatchers.equalTo(true))
//        Assert.assertThat(latency.errorDescription, CoreMatchers.equalTo("ConnectionTimeoutException"))
//    }
//
//    @Test
//    fun testParse_Unknowns() {
//        // given
//        val now = LocalDateTime.now()
//        val lines = listOf(
//                "+-------------------------------------------------------------------------------------------------------------------------------+",
//                "|  2017-03-24 01:59:42, CLUSTER:ticketlink_cluster_1                                                                            |",
//                "|  Time |  <= 1ms |  <= 2ms |  <= 4ms |  <= 8ms | <= 16ms | <= 32ms | <= 64ms |  <= 128 |  <= 256 |  <= 512 | <= 1024 |  > 1024 |"
//        )
//        print("""lines.size = ${lines.size}""")
//        lines
//                .map {
//                    // when
//                    statParser.parse(now, it)
//                    // then
//                }
//                .forEach { Assert.assertThat(it.isUnknown(), CoreMatchers.equalTo(true)) }
//    }
}

