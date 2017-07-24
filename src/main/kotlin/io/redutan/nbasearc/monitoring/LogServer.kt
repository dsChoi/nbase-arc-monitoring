package io.redutan.nbasearc.monitoring

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.disposables.Disposable
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
class LogServer(val logPublisherFactory: LogPublisherFactory, val logPersistence: LogPersistence) {
    companion object {
        val log by logger()
    }
    val webSocketMap = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
    val clusterIds = ConcurrentHashMap<String, ClusterId>()
    val subscriberMap = ConcurrentHashMap<WebSocketSession, MutableList<Disposable>>()
    val gson: Gson = GsonBuilder().create()

    fun openSocket(session: LogSession, clusterId: ClusterId, socket: WebSocketSession) {
        val sockets = webSocketMap.computeIfAbsent(session.id) { CopyOnWriteArrayList<WebSocketSession>() }
        sockets.add(socket)
        clusterIds.putIfAbsent(session.id, clusterId)
        log.info("Opened Socket {}:{}", session.id, socket)
    }

    fun publish(session: LogSession, socket: WebSocketSession, logType: LogType<*>) {
        log.info("Will publish {}:{}:{}", session.id, socket, logType)
        val clusterId = clusterIds[session.id]!!
        val logPublisher = logPublisherFactory.getLogPublisher(LogTypeId(logType, clusterId))
        logPersistence.initialize(logPublisher)
        val subscriber = logPublisher.observe()
                .subscribe({
                    socket.outgoing.offer(Frame.Text(gson.toJson(it)))
                })
        val subscribers = subscriberMap.computeIfAbsent(socket, { CopyOnWriteArrayList<Disposable>() })
        subscribers.add(subscriber)
        log.info("Published {}:{}:{}", session.id, socket, logType)
    }

    fun closeSocket(session: LogSession, socket: WebSocketSession) {
        val subscribers = subscriberMap[socket]
        subscribers?.forEach { if (!it.isDisposed) it.dispose() }
        subscriberMap.remove(socket)

        val sockets = webSocketMap[session.id]
        sockets?.remove(socket)
        if (sockets?.isEmpty() ?: true) {
            webSocketMap.remove(session.id)
            clusterIds.remove(session.id)
        }
        log.info("Closed Socket {}:{}", session.id, socket)
    }
}

