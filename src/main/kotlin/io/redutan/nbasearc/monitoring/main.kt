package io.redutan.nbasearc.monitoring

import io.redutan.nbasearc.monitoring.collector.parser.LatencyParser
import io.redutan.nbasearc.monitoring.collector.parser.LogHeaderParser
import io.redutan.nbasearc.monitoring.collector.parser.StatParser
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing

/**
 *
 * @author myeongju.jung
 */
fun main(args: Array<String>) {
    val headerParser = LogHeaderParser()
    val latencyParser = LatencyParser()
    val statParser = StatParser()

    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("Hello, World", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}