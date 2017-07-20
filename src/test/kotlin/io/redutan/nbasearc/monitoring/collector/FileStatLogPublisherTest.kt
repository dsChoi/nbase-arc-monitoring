package io.redutan.nbasearc.monitoring.collector

import io.reactivex.observers.TestObserver
import io.redutan.nbasearc.monitoring.collector.parser.EMPTY_BYTE_VALUE
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import io.redutan.nbasearc.monitoring.collector.parser.Stat
import io.redutan.nbasearc.monitoring.collector.parser.StatParser
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author myeongju.jung
 */
class FileStatLogPublisherTest {
    val parser = StatParser()
    val headerParser = LogHeaderParser()
    var logPublisher = FileStatLogPublisher(parser, headerParser)

    @Test
    fun testObserve_Each() {
        // given
        val to = TestObserver.create<Stat>()
        // when
        val latencies = logPublisher.observe()
        latencies.subscribe(to)
        // then
        to.assertComplete()
        to.assertValueCount(318)
        to.assertNever({ !isValidStat(it) })
    }

    private fun isValidStat(it: Stat): Boolean {
        try {
            assertStat(it)
            return true
        } catch (t: AssertionError) {
            return false
        }
    }

    private fun assertStat(stat: Stat) {
        println(stat)
        if (stat.isError()) {
            assertLoggedAt(stat)
            assertThat(stat.errorDescription, notNullValue())
            return
        }
        assertThat(stat.isUnknown(), CoreMatchers.equalTo(false))
        assertThat(stat.redis, greaterThanOrEqualTo(0L))
        assertThat(stat.pg, greaterThanOrEqualTo(0L))
        assertThat(stat.connection, greaterThanOrEqualTo(0L))
        assertThat(stat.mem, not(EMPTY_BYTE_VALUE))
        assertThat(stat.ops, greaterThanOrEqualTo(0L))
        assertThat(stat.hits, greaterThanOrEqualTo(0L))
        assertThat(stat.misses, greaterThanOrEqualTo(0L))
        assertThat(stat.keys, greaterThanOrEqualTo(0L))
        assertThat(stat.expires, greaterThanOrEqualTo(0L))
    }
}

fun assertLoggedAt(log: NbaseArcLog) {
    val startDateTime = LocalDateTime.parse("2017-03-24 01:59:27", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val endDateTime = LocalDateTime.parse("2017-03-24 02:05:11", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    try {
        assertThat(log.loggedAt in startDateTime..endDateTime, CoreMatchers.equalTo(true))
    } catch (t: Throwable) {
        println("startDateTime = " + startDateTime)
        println("endDateTime = " + endDateTime)
        throw t
    }
}