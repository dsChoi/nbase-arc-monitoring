package io.redutan.nbasearc.monitoring.collector

import io.reactivex.observers.TestObserver
import io.redutan.nbasearc.monitoring.collector.parser.LatencyParser
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * @author myeongju.jung
 */
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
        println(latency)
        if (latency.isError()) {
            assertLoggedAt(latency)
            assertThat(latency.errorDescription, Matchers.notNullValue())
            return
        }
        assertThat(latency.isUnknown(), equalTo(false))
        assertLoggedAt(latency)
        assertThat(latency.under1ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under2ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under4ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under8ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under16ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under32ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under64ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under128ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under256ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under512ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.under1024ms, Matchers.greaterThanOrEqualTo(0L))
        assertThat(latency.over1024ms, Matchers.greaterThanOrEqualTo(0L))
    }
}
