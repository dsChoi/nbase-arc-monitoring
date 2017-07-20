package io.redutan.nbasearc.monitoring.collector.parser

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Assert.fail
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

    @Test(expected = NbaseArcServerException::class)
    fun testParse_ConnectionTimeoutException() {
        // given
        val line = "                 ConnectionTimeoutException                       "
        // when
        headerParser.parse(LocalDateTime.MIN, line)
        // then
        fail()
    }

    @Test
    fun testParse_Unknowns() {
        // given
        val now = LocalDateTime.now()
        val lines = listOf(
                "+-------------------------------------------------------------------------------------------------------------------------------+",
                "|  Time |  <= 1ms |  <= 2ms |  <= 4ms |  <= 8ms | <= 16ms | <= 32ms | <= 64ms |  <= 128 |  <= 256 |  <= 512 | <= 1024 |  > 1024 |",
                "| 11:53 |  1.09 K |      13 |       7 |       2 |      11 |      22 |      33 |      44 |      55 |      66 |      77 |      88 |"
        )
        print("""lines.size = ${lines.size}""")
        lines
                .map { // when
                    headerParser.parse(now, it)
                    // then
                }
                .forEach { assertThat(it.isUnknown(), equalTo(true)) }
    }

}