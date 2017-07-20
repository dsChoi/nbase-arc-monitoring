package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.NbaseArcLogHeader
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

/**
 * @author myeongju.jung
 */
class LogHeaderParserTest {
    val headerParser = LogHeaderParser()

    @Test
    fun testIsHeader() {
        // given
        val line = "|  2017-03-24 01:59:42, CLUSTER:ticketlink_cluster_1                                                                            |"
        // when
        val result = headerParser.isHeader(line)
        // then
        assertThat(result, equalTo(true))
    }

    @Test
    fun testIsHeader_Not() {
        // given
        val lines = listOf(
                "+-------------------------------------------------------------------------------------------------------------------------------+",
                "|  Time |  <= 1ms |  <= 2ms |  <= 4ms |  <= 8ms | <= 16ms | <= 32ms | <= 64ms |  <= 128 |  <= 256 |  <= 512 | <= 1024 |  > 1024 |",
                "| 11:53 |  1.09 K |      13 |       7 |       2 |      11 |      22 |      33 |      44 |      55 |      66 |      77 |      88 |"
        )
        lines
                .map { // when
                    headerParser.isHeader(it)
                    // then
                }
                .forEach { assertThat(it, equalTo(false)) }
    }

    @Test
    fun testParse() {
        // given
        val line = "|  2017-03-24 01:59:42, CLUSTER:ticketlink_cluster_1                                                                            |"
        // when
        val header = headerParser.parse(line)
        // then
        assertThat(header, equalTo(NbaseArcLogHeader(LocalDateTime.of(2017, Month.MARCH, 24, 1, 59, 42), "ticketlink_cluster_1")))
    }

    @Test(expected = NbaseArcServerException::class)
    fun testParse_ConnectionTimeoutException() {
        // given
        val line = "                 ConnectionTimeoutException                       "
        // when
        headerParser.parse(line)
        // then
        fail()
    }

    @Test
    fun testParse_Unknowns() {
        // given
        val lines = listOf(
                "+-------------------------------------------------------------------------------------------------------------------------------+",
                "|  Time |  <= 1ms |  <= 2ms |  <= 4ms |  <= 8ms | <= 16ms | <= 32ms | <= 64ms |  <= 128 |  <= 256 |  <= 512 | <= 1024 |  > 1024 |",
                "| 11:53 |  1.09 K |      13 |       7 |       2 |      11 |      22 |      33 |      44 |      55 |      66 |      77 |      88 |"
        )
        print("""lines.size = ${lines.size}""")
        lines
                .map { // when
                    headerParser.parse(it)
                    // then
                }
                .forEach { assertThat(it.isUnknown(), equalTo(true)) }
    }
}