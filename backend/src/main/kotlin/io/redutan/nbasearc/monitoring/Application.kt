package io.redutan.nbasearc.monitoring

import io.redutan.nbasearc.monitoring.collector.ArcCliLogPublisherFactory
import io.redutan.nbasearc.monitoring.collector.ClusterId
import io.redutan.nbasearc.monitoring.collector.LatencyType
import io.redutan.nbasearc.monitoring.collector.StatType
import kotlinx.coroutines.experimental.channels.consumeEach
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.defaultResource
import org.jetbrains.ktor.content.resources
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.sessions.*
import org.jetbrains.ktor.util.nextNonce
import org.jetbrains.ktor.websocket.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

private val logPersistence = DefaultLogPersistence
private val server = LogServer(ArcCliLogPublisherFactory, logPersistence)

/**
 *
 * @author myeongju.jung
 */
fun Application.main() {
    val log by logger()

    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets) {
        pingPeriod = Duration.ofMinutes(1)
    }

    install(Routing) {
        install(Sessions) {
            cookie<LogSession>("SESSION")
        }

        intercept(ApplicationCallPipeline.Infrastructure) {
            if (call.sessions.get<LogSession>() == null) {
                call.sessions.set(LogSession(nextNonce()))
            }
        }

        webSocket("/logs") {
            val session = call.sessions.get<LogSession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                return@webSocket
            }

            log.debug("parameters = {}", call.parameters)
            val zkAddress = call.parameters["zkAddress"]!!
            val cluster = call.parameters["cluster"]!!
            val clusterId = ClusterId(zkAddress = zkAddress, cluster = cluster)
            log.debug("clusterId = {}", clusterId)
            server.openSocket(session, clusterId, this)
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        receivedMessage(session, this, frame.readText())
                    }
                }
            } finally {
                server.closeSocket(session, this)
            }
        }

        static {
            defaultResource("index.html", "web")
            resources("web")
        }
    }
}

data class LogSession(val id: String)

suspend private fun receivedMessage(session: LogSession, socket: WebSocketSession, command: String) {
    when {
        command.startsWith("/latencies") -> server.publish(session, socket, LatencyType)
        command.startsWith("/stats") -> server.publish(session, socket, StatType)
    }
}

fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this::class.java) }
}
