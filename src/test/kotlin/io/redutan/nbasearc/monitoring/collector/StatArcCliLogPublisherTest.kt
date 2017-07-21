package io.redutan.nbasearc.monitoring.collector

import io.reactivex.observers.TestObserver
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import io.redutan.nbasearc.monitoring.collector.parser.StatParser
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * @author myeongju.jung
 */
class StatArcCliLogPublisherTest {

    val statParser = StatParser()
    val headerParser = LogHeaderParser()
    val zkAddress = System.getProperty("nbase.arc.zkaddress", "localhost:2181")!!
    val cluster = System.getProperty("nbase.arc.cluster", "default")!!
    val command = "-s"  // stat

    var logPublisher: ArcCliLogPublisher<Stat>? = null

    @Before
    fun setUp() {
        logPublisher = ArcCliLogPublisher(statParser, headerParser,
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
        val to = TestObserver<Stat>()
        // when
        val count = 5L
        Thread {
            observable()
                    .take(count)
                    .subscribe(to)
        }.start()

        TimeUnit.SECONDS.sleep(count + 1)

        // then
        to.assertComplete()
        to.assertValueCount(count.toInt())
    }

    @Test
    fun testObserve() {
        // given
        val to1 = TestObserver<Stat>()
        val to2 = TestObserver<Stat>()
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


        val thread1 = Thread {
            latencies1.subscribe(to1)
        }

        val thread2 = Thread {
            latencies2.subscribe(to2)
        }

        thread1.start()
        thread2.start()

        TimeUnit.SECONDS.sleep(5)

        to1.assertComplete()
        to1.assertValueCount(1)
        to2.assertComplete()
        to2.assertValueCount(2)
    }

}