package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
import java.nio.file.Files
import java.nio.file.Paths

const val FILE_NAME = "latency-each-1seconds.txt"

/**
 *
 * @author myeongju.jung
 */
class FileLatencyLogPublisher(private val parser: Parser<Latency>,
                              private val headerParser: HeaderParser)
    : LogPublishable<Latency> {

    override fun observe(interval: Int): Observable<Latency> {
        val lineStream = Files.lines(Paths.get(FILE_NAME))
        return Observable.create<Latency> { e ->
            var header: NbaseArcLogHeader = UNKNOWN_HEADER  // header 초기화
            lineStream.forEach {
                // header 인가?
                if (headerParser.isHeader(it)) {
                    header = headerParser.parse(it)
                }
                val log = parser.parse(header.current, it)
                // 알 수 없는 로그인가?
                if (log.isUnknown()) {
                    return@forEach
                }
                e.onNext(log)   // 방출
            }
            e.onComplete()  // 방출종료
        }
    }
}
