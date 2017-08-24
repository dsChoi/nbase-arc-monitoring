package io.redutan.nbasearc.monitoring

import io.reactivex.schedulers.Schedulers
import io.redutan.nbasearc.monitoring.collector.LogPublishable
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
    private val logPublishers = CopyOnWriteArraySet<LogPublishable<*>>()

    override fun initialize(logPublisher: LogPublishable<*>) {
        val log by logger()
        if (logPublishers.contains(logPublisher)) {
            return
        }
        logPublishers.add(logPublisher)
        log.info("Db registered initialized")
        logPublisher.observe()
                .doOnNext { log.debug("Db Inserted {}", it) }
                .subscribeOn(Schedulers.newThread())
                .subscribe()
        TimeUnit.MILLISECONDS.sleep(500)
    }
}