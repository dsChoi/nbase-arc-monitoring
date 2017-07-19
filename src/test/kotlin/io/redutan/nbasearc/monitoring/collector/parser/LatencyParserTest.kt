package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.Latency
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

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
        assertLatency(latency, now, 0, 52, 1_080, 13, 7, 2, 1, 2, 3, 4, 5, 6, 7, 8)
    }

    private fun assertLatency(latency: Latency, datetime: LocalDateTime, minute: Int, second: Int, under1ms: Long, under2ms: Long, under4ms: Long,
                              under8ms: Long, under16ms: Long, under32ms: Long, under64ms: Long, under128ms: Long, under256ms: Long, under512ms: Long,
                              under1024ms: Long, over1024ms: Long) {
        val expectedDateTime = datetime.changeMinuteAndSecond(minute, second)
        assertThat(latency, equalTo(Latency(expectedDateTime, under1ms, under2ms, under4ms, under8ms, under16ms, under32ms, under64ms, under128ms,
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
        assertLatency(latency, now, 11, 53, 1_090, 13, 7, 2, 11, 22, 33, 44, 55, 66, 77, 88)
    }
}