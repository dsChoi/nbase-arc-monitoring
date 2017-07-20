package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
import io.redutan.nbasearc.monitoring.collector.parser.Stat
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream

private const val FILE_NAME = "stat-each-1seconds.txt"

/**
 *
 * @author myeongju.jung
 */
class FileStatLogPublisher(private val parser: Parser<Stat>,
                           private val headerParser: HeaderParser)
    : LogPublishable<Stat> {

    override fun observe(): Observable<Stat> {
        val lineStream = getFileLineStream()
        return Observable.create<Stat> { e ->
            var header = UNKNOWN_HEADER  // header 초기화
            var currentDateTime = header.current
            lineStream.forEach {
                try {
                    // header 인가?
                    if (headerParser.isHeader(it)) {
                        header = headerParser.parse(it)
                        currentDateTime = header.current
                    }
                    val log = parser.parse(currentDateTime, it)
                    // 알 수 없는 로그인가?
                    if (log.isUnknown()) {
                        return@forEach
                    }
                    e.onNext(log)   // 방출
                    currentDateTime = currentDateTime.plusSeconds(1)
                } catch (t: Throwable) {
                    e.onError(t)
                }
            }
            e.onComplete()  // 방출종료
        }
    }

    private fun getFileLineStream(): Stream<String> {
        return Files.lines(Paths.get(this.javaClass.getResource(FILE_NAME).path))
    }
}
