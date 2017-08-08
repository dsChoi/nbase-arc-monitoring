package io.redutan.nbasearc.monitoring.collector

import io.reactivex.observers.TestObserver
import io.redutan.nbasearc.monitoring.collector.parser.LatencyParser
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * @author myeongju.jung
 */
@Ignore
class FileLatencyLogPublisherTest {
    val parser = LatencyParser()
    val headerParser = LogHeaderParser()
    var logPublisher = FileLatencyLogPublisher(parser, headerParser)

    @Test
    fun testObserve_Each() {
        // given
        val to = TestObserver.create<Latency>()
        // when
        val latencies = logPublisher.observe()
                .doOnNext { println(it) }
        latencies.subscribe(to)
        // then
        to.assertComplete()
        to.assertValueCount(23 * 15 - 1)
        to.assertNever({ !isValidLatency(it) })
    }

    private fun isValidLatency(it: Latency): Boolean {
        try {
            assertLatency(it)
            return true
        } catch (t: AssertionError) {
            return false
        }
    }

    private fun assertLatency(latency: Latency) {
        if (latency.isError()) {
            assertLoggedAt(latency)
            assertNotNull(latency.errorDescription)
            return
        }
        assertFalse(latency.isUnknown())
        assertLoggedAt(latency)
        assertTrue(latency.under1ms >= 0)
        assertTrue(latency.under2ms >= 0)
        assertTrue(latency.under4ms >= 0)
        assertTrue(latency.under8ms >= 0)
        assertTrue(latency.under16ms >= 0)
        assertTrue(latency.under32ms >= 0)
        assertTrue(latency.under64ms >= 0)
        assertTrue(latency.under128ms >= 0)
        assertTrue(latency.under256ms >= 0)
        assertTrue(latency.under512ms >= 0)
        assertTrue(latency.under1024ms >= 0)
        assertTrue(latency.over1024ms >= 0)
    }
}
