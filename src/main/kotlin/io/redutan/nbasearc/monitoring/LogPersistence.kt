package io.redutan.nbasearc.monitoring

import io.redutan.nbasearc.monitoring.collector.LogPublishable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.TimeUnit

/**
 *
 * @author myeongju.jung
 */
interface LogPersistence {
    fun initialize(logPublisher: LogPublishable<*>)
}

object DefaultLogPersistence : LogPersistence {
    val logPublishers = CopyOnWriteArraySet<LogPublishable<*>>()

    override fun initialize(logPublisher: LogPublishable<*>) {
        val log by logger()
        if (logPublishers.contains(logPublisher)) {
            return
        }
        logPublishers.add(logPublisher)
        launch(CommonPool) {
            logPublisher.observe()
                    .doOnNext { log.debug("Db Insert {}", it) }
                    .subscribe()
        }
        log.info("H2 Db registered initialization")
        TimeUnit.MILLISECONDS.sleep(500)
    }
}