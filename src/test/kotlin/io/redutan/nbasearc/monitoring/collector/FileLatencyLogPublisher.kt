package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
import java.nio.file.Files
import java.nio.file.Paths

const val FILE_NAME = "latency-each-1seconds.txt"

/**
 *
 * @author myeongju.jung
 */
class FileLatencyLogPublisher(parser: Parser<Latency>) : LogPublishable<Latency> {
    override fun observe(interval: Int): Observable<Latency> {
        val lineStream = Files.lines(Paths.get(FILE_NAME))
        // TODO
//        Observable.create<Latency> { e -> e.onNext() }
        return Observable.empty()
    }
}
