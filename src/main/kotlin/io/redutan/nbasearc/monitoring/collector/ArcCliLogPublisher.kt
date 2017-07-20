package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
import java.lang.ProcessBuilder.Redirect.INHERIT
import java.nio.file.Paths

/**
 *
 * @author myeongju.jung
 */
class ArcCliLogPublisher<T : NbaseArcLog>(
    val parser: Parser<T>, val headerParser: HeaderParser, val directory: String = "~/",
    val zkAddress: String, val cluster: String, val command: String)
    : LogPublishable<T> {

    override fun observe(): Observable<T> {
        val process = ProcessBuilder(listOf("./arc-cli", "-z", zkAddress, "-c", cluster))
            .directory(Paths.get(directory).toFile())
            .redirectOutput(INHERIT)
            .redirectError(INHERIT)
            .start()

        return Observable.create<T> { e ->
            sendCommand(process)
            var header = UNKNOWN_HEADER  // header 초기화
            var currentDateTime = header.current
            process.inputStream.bufferedReader().forEachLine {
                try {
                    if (e.isDisposed) {
                        process.destroyForcibly()
                        e.onComplete()
                        return@forEachLine
                    }
                    if (!process.isAlive) {
                        e.onComplete()
                        return@forEachLine
                    }
                    // header 인가?
                    if (headerParser.isHeader(it)) {
                        header = headerParser.parse(it)
                        currentDateTime = header.current
                    }
                    val log = parser.parse(currentDateTime, it)
                    // 알 수 없는 로그인가?
                    if (log.isUnknown()) {
                        return@forEachLine
                    }
                    e.onNext(log)   // 방출
                    currentDateTime = currentDateTime.plusSeconds(1)
                } catch (t: Throwable) {
                    e.onError(t)
                }
            }
        }
    }

    private fun sendCommand(process: Process) {
        val writer = process.outputStream.bufferedWriter()
        writer.write("$command 1")
        writer.newLine()
        writer.flush()
    }
}
