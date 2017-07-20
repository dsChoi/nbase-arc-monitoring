package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.Latency
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@Suppress("UNUSED_VARIABLE")
/**
 * @author myeongju.jung
 */
class LatencyParserTest {
    var latencyParser = LatencyParser()

    @Before
    fun setUp() {
        latencyParser = LatencyParser()
    }

    @Test
    fun testParse() {
        // given
        val now = LocalDateTime.now()
        val line = "| 00:52 |  1.08 K |      13 |       7 |       2 |       1 |       2 |       3 |       4 |       5 |       6 |       7 |       8 |"
        // when
        val latency = latencyParser.parse(now, line)
        // then
        assertLatency(latency, now, 1_080, 13, 7, 2, 1, 2, 3, 4, 5, 6, 7, 8)
    }

    private fun assertLatency(latency: Latency, datetime: LocalDateTime, under1ms: Long, under2ms: Long, under4ms: Long, under8ms: Long, under16ms: Long,
                              under32ms: Long, under64ms: Long, under128ms: Long, under256ms: Long, under512ms: Long, under1024ms: Long, over1024ms: Long) {
        assertThat(latency, equalTo(Latency(datetime, under1ms, under2ms, under4ms, under8ms, under16ms, under32ms, under64ms, under128ms,
                under256ms, under512ms, under1024ms, over1024ms)))
    }

    @Test
    fun testParse_Another() {
        // given
        val now = LocalDateTime.now()
        val line = "| 11:53 |  1.09 K |      13 |       7 |       2 |      11 |      22 |      33 |      44 |      55 |      66 |      77 |      88 |"
        // when
        val latency = latencyParser.parse(now, line)
        // then
        assertLatency(latency, now, 1_090, 13, 7, 2, 11, 22, 33, 44, 55, 66, 77, 88)
    }

    @Test
    fun testParse_ConnectionTimeoutException() {
        // given
        val now = LocalDateTime.now()
        val line = "              ConnectionTimeoutException                              "
        // when
        val latency = latencyParser.parse(now, line)
        // then
        assertThat(latency.isError(), equalTo(true))
        assertThat(latency.errorDescription, equalTo("ConnectionTimeoutException"))
    }

    @Test
    fun testParse_Unknowns() {
        // given
        val now = LocalDateTime.now()
        val lines = listOf(
            "+-------------------------------------------------------------------------------------------------------------------------------+",
            "|  2017-03-24 01:59:42, CLUSTER:ticketlink_cluster_1                                                                            |",
            "|  Time |  <= 1ms |  <= 2ms |  <= 4ms |  <= 8ms | <= 16ms | <= 32ms | <= 64ms |  <= 128 |  <= 256 |  <= 512 | <= 1024 |  > 1024 |"
        )
        print("""lines.size = ${lines.size}""")
        lines
                .map { // when
                    latencyParser.parse(now, it)
                    // then
                }
                .forEach { assertThat(it.isUnknown(), equalTo(true)) }
    }
}