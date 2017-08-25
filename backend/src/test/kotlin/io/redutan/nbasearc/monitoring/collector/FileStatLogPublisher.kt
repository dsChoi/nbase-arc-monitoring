package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
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
            var header = NbaseArcLogHeader.unknown()  // header 초기화
            var currentDateTime = header.loggedAt
            lineStream.forEach {
                try {
                    // header 인가?
                    if (headerParser.isHeader(it)) {
                        header = headerParser.parse(ClusterId.empty(), it)
                        currentDateTime = header.loggedAt
                    }
                    val log = parser.parse(ClusterId.empty(), currentDateTime, it)
                    // 알 수 없는 로그인가?
                    if (log.isUnknown()) {
                        return@forEach
                    }
                    if (log.isError()) {
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
