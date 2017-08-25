package io.redutan.nbasearc.monitoring.collector

import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * @author myeongju.jung
 */
//@Ignore
class LatencyArcCliLogPublisherTest {

    val headerParser = LogHeaderParser()
    val zkAddress = System.getProperty("nbase.arc.zkaddress", "localhost:2181")!!
    val cluster = System.getProperty("nbase.arc.cluster", "default")!!

    var logPublisher: ArcCliLogPublisher<Latency>? = null

    @Before
    fun setUp() {
        logPublisher = ArcCliLogPublisher(ClusterId(zkAddress, cluster), LatencyType, headerParser)
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
        observable()
            .subscribeOn(Schedulers.io())
            .take(count)
            .subscribe(to)

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

        latencies1
            .subscribeOn(Schedulers.io())
            .subscribe(to1)
        latencies2
            .subscribeOn(Schedulers.io())
            .subscribe(to2)
        TimeUnit.SECONDS.sleep(5)
        // then
        to1.assertComplete()
        to1.assertValueCount(1)
        to2.assertComplete()
        to2.assertValueCount(2)
    }

}
