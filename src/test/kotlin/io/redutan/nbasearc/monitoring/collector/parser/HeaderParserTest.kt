package io.redutan.nbasearc.monitoring.collector.parser

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

/**
 * @author myeongju.jung
 */
class HeaderParserTest {
    val headerParser = HeaderParser()

    @Test
    fun testParse() {
        // given
        val line = "|  2017-03-24 01:59:42, CLUSTER:ticketlink_cluster_1                                                                            |"
        // when
        val header = headerParser.parse(LocalDateTime.MIN, line)
        // then
        assertThat(header, equalTo(LogHeader(LocalDateTime.of(2017, Month.MARCH, 24, 1, 59, 42), "ticketlink_cluster_1")))
    }

}