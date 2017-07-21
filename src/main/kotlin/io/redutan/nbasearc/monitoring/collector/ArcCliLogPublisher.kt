package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.nio.file.Paths

/**
 *
 * @author myeongju.jung
 */
class ArcCliLogPublisher<T : NbaseArcLog>(
        private val parser: Parser<T>, private val headerParser: HeaderParser, private val directory: String = ".",
        private val zkAddress: String, private val cluster: String, private val command: String)
    : LogPublishable<T>, AutoCloseable, Disposable {

    var process: Process? = null
    var isCallProcess = false
    val observable: Observable<T> = Observable.create<T> { e ->
        var header = UNKNOWN_HEADER  // header 초기화
        var currentDateTime = header.current
        try {
            process = executeArcCli()
            process!!.inputStream.bufferedReader().forEachLine { line ->
                println(line + this)
                // header 인가?
                if (headerParser.isHeader(line)) {
                    header = headerParser.parse(line)
                    currentDateTime = header.current
                }
                val log = parser.parse(currentDateTime, line)
                // 알 수 없는 로그인가?
                if (log.isUnknown()) {
                    return@forEachLine
                }
                e.onNext(log)   // 방출
                currentDateTime = currentDateTime.plusSeconds(1)
            }
        } catch (t: Throwable) {
            e.onError(t)
        } finally {
            process?.destroyForcibly()
            e.onComplete()
        }
    }.share()

    /*
    Usage: arc-cli-admin -z <zk addr> -c <cluster name> [OPTIONS]
       -z <zookeeper address>      Zookeeper address (ex: zookeeper.nbasearc.com:2181)
       -c <cluster name>           Cluster name (ex: dev_test)
       -t                          Allow non-tty input
       -s                          stat mode
       -l                          latency mode
       -r <repeat count>           repeat mode
       -i <interval sec>           interval for stat/latency/repeat (default: 5sec)
       -p                          Use raw formatting

    Special Commands
       STAT [<interval sec>]       Print stats of cluster
       LATENCY [<interval sec>]    Print latencies of cluster
       WHEREIS <key>               Get redis_id of a key.
       SCAN <redis_id> <pattern> <output:"stdout" or filepath>    Get scan result of a specific redis
                                   in escaped string representation. All the non-printable characters
                                   will be escaped in the form of "\n\r\a...." or "\x<hex-number>"
     */
    private fun executeArcCli(): Process {
        val result = ProcessBuilder("arc-cli-admin", "-t", command, "-i", "1", "-z", zkAddress, "-c", cluster)
                .directory(Paths.get(directory).toFile())
                .redirectErrorStream(true)
                .start()
        isCallProcess = true
        return result
    }

    override fun observe() = observable

    override fun isDisposed() = isCallProcess && !isAlive()

    private fun isAlive() = process?.isAlive ?: false

    override fun dispose() {
        if (isCallProcess)
            process?.destroyForcibly()
    }

    override fun close() {
        dispose()
    }
}