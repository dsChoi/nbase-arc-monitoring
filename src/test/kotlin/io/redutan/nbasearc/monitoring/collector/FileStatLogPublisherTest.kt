package io.redutan.nbasearc.monitoring.collector

import io.reactivex.observers.TestObserver
import io.redutan.nbasearc.monitoring.collector.parser.EMPTY_BYTE_VALUE
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import io.redutan.nbasearc.monitoring.collector.parser.StatParser
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
        if (stat.isError()) {
            assertLoggedAt(stat)
            assertNotNull(stat.errorDescription)
            return
        }
        assertFalse(stat.isUnknown())
        assertTrue(stat.redis >= 0)
        assertTrue(stat.pg >= 0)
        assertTrue(stat.connection >= 0)
        assertNotEquals(EMPTY_BYTE_VALUE, stat.mem)
        assertTrue(stat.ops >= 0)
        assertTrue(stat.hits >= 0)
        assertTrue(stat.misses >= 0)
        assertTrue(stat.keys >= 0)
        assertTrue(stat.expires >= 0)
    }
}

fun assertLoggedAt(log: NbaseArcLog) {
    val startDateTime = LocalDateTime.parse("2017-03-24 01:59:27", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val endDateTime = LocalDateTime.parse("2017-03-24 02:05:11", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    try {
        assertTrue(log.loggedAt in startDateTime..endDateTime)
    } catch (t: Throwable) {
        println("startDateTime = " + startDateTime)
        println("endDateTime = " + endDateTime)
        throw t
    }
}
