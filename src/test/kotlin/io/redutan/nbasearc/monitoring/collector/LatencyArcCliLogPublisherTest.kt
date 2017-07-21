package io.redutan.nbasearc.monitoring.collector

import io.reactivex.observers.TestObserver
import io.redutan.nbasearc.monitoring.collector.parser.LatencyParser
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * @author myeongju.jung
 */
class LatencyArcCliLogPublisherTest {

    val latencyParser = LatencyParser()
    val headerParser = LogHeaderParser()
    val zkAddress = System.getProperty("nbase.arc.zkaddress", "localhost:2181")!!
    val cluster = System.getProperty("nbase.arc.cluster", "default")!!
    val command = "-l" // latency

    var logPublisher: ArcCliLogPublisher<Latency>? = null

    @Before
    fun setUp() {
        logPublisher = ArcCliLogPublisher(latencyParser, headerParser,
                directory = "/Users/myeongju.jung/git/nbase-arc/tools/nbase-arc-cli/",
                zkAddress = zkAddress, cluster = cluster, command = command)
    }

    @After
    fun tearDown() {
        logPublisher?.close()
    }

    private fun observable() = logPublisher!!.observe()

    @Test
    fun testSingleSubscribe() {
        // given
        val to = TestObserver<Latency>()
        // when
        val count = 5L
        launch(CommonPool) {
            observable()
                    .take(count)
                    .subscribe(to)
        }

        TimeUnit.SECONDS.sleep(count + 1)

        // then
        to.assertComplete()
        to.assertValueCount(count.toInt())
    }

    @Test
    fun testObserve() {
        // given
        val to1 = TestObserver<Latency>()
        val to2 = TestObserver<Latency>()
        // when
        val latencies1 = observable()
                .take(1)
                .doOnNext { println("1 $it") }
                .doOnComplete { println("Complete") }
                .doOnError { println("error = $it") }
        val latencies2 = observable()
                .take(2)
                .doOnNext { println("2 $it") }
                .doOnComplete { println("Complete") }
                .doOnError { println("error = $it") }
        launch(CommonPool) {
            latencies1.subscribe(to1)
        }
        launch(CommonPool) {
            latencies2.subscribe(to2)
        }
        TimeUnit.SECONDS.sleep(5)
        // then
        to1.assertComplete()
        to1.assertValueCount(1)
        to2.assertComplete()
        to2.assertValueCount(2)
    }

}