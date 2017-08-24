package io.redutan.nbasearc.monitoring

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.redutan.nbasearc.monitoring.collector.ClusterId
import io.redutan.nbasearc.monitoring.collector.LogPublisherFactory
import io.redutan.nbasearc.monitoring.collector.LogType
import io.redutan.nbasearc.monitoring.collector.LogTypeId
import org.jetbrains.ktor.websocket.Frame
import org.jetbrains.ktor.websocket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * @author myeongju.jung
 */
class LogServer(private val logPublisherFactory: LogPublisherFactory,
                private val logPersistence: LogPersistence) {
    companion object {
        val log by logger()
    }

    private val webSocketsMap = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
    private val clusterIds = ConcurrentHashMap<WebSocketSession, ClusterId>()
    private val subscribersMap = ConcurrentHashMap<WebSocketSession, MutableList<Disposable>>()
    private val gson: Gson = GsonBuilder().create()

    fun openSocket(session: LogSession, clusterId: ClusterId, socket: WebSocketSession) {
        val sockets = webSocketsMap.computeIfAbsent(session.id) { CopyOnWriteArrayList<WebSocketSession>() }
        sockets.add(socket)
        clusterIds.putIfAbsent(socket, clusterId)
        if (log.isInfoEnabled) {
            log.info("Open Socket {} : {}", session.id, socket)
            logTotal()
        }
    }

    suspend fun publish(session: LogSession, socket: WebSocketSession, logType: LogType<*>) {
        val clusterId = clusterIds[socket]!!
        val logPublisher = logPublisherFactory.getLogPublisher(LogTypeId(logType, clusterId))
//        logPersistence.initialize(logPublisher)
        val subscriber = logPublisher.observe()
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    socket.outgoing.offer(Frame.Text(gson.toJson(it)))
                })
        val subscribers = subscribersMap.computeIfAbsent(socket, { CopyOnWriteArrayList<Disposable>() })
        subscribers.add(subscriber)
        if (log.isInfoEnabled) {
            log.info("Published {} : {} : {}", session.id, socket, logType)
            logTotal()
        }
    }

    fun closeSocket(session: LogSession, socket: WebSocketSession) {
        log.info("Will close Socket {} : {}", session.id, socket)
        val subscribers = subscribersMap[socket]
        subscribers?.forEach { if (!it.isDisposed) it.dispose() }
        subscribersMap.remove(socket)
        clusterIds.remove(socket)
        webSocketsMap[session.id]?.let {
            it.remove(socket)
            if (it.isEmpty()) {
                webSocketsMap.remove(session.id)
            }
        }
        if (log.isInfoEnabled) {
            log.info("Closed Socket {} : {}", session.id, socket)
            logTotal()
        }
    }

    private fun logTotal() {
        log.info("Total | Session count : {}, Socket count : {}, Subscriber count : {}",
                webSocketsMap.count(), clusterIds.count(), subscribersMap.count())
    }
}

