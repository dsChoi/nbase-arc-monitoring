package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import io.redutan.nbasearc.monitoring.logger
import java.nio.file.Paths

/**
 *
 * @author myeongju.jung
 */
class ArcCliLogPublisher<T : NbaseArcLog>(
        private val clusterId: ClusterId,
        private val logType: LogType<T>,
        private val headerParser: HeaderParser = LogHeaderParser(),
        private val directory: String = ".")
    : LogPublishable<T>, AutoCloseable, Disposable {
    companion object {
        val log by logger()
    }
    var process: Process? = null
    var isCallProcess = false
    val observable: Observable<T> = Observable.create<T> { e ->
        var header = UNKNOWN_HEADER  // header 초기화
        var currentDateTime = header.current
        try {
            process = executeArcCli()
            process!!.inputStream.bufferedReader().forEachLine { line ->
                // header 인가?
                log.debug("{}", line)
                if (headerParser.isHeader(line)) {
                    header = headerParser.parse(line)
                    currentDateTime = header.current
                }
                val parsedLog = logType.parser.parse(currentDateTime, line)
                // TODO 미접속 오류 : "Connect to cluster: Request timeout"
                // 알 수 없는 로그인가?
                if (parsedLog.isUnknown()) {
                    return@forEachLine
                }
                if (parsedLog.isError()) {
                    log.error("{} : {}", clusterId, parsedLog.errorDescription)
                    return@forEachLine
                }
                e.onNext(parsedLog)   // 방출
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
        val result = ProcessBuilder("arc-cli-admin", "-t", logType.command, "-i", "1", "-z", clusterId.zkAddress, "-c", clusterId.cluster)
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
        if (isCallProcess) {
            process?.destroyForcibly()
        }
    }

    override fun close() {
        dispose()
    }
}