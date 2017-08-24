package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.Latency
import io.redutan.nbasearc.monitoring.collector.Parser
import io.redutan.nbasearc.monitoring.collector.UNKNOWN_LATENCY
import java.time.LocalDateTime

private const val IDX_UNDER_1MS = 2
private const val IDX_UNDER_2MS = 3
private const val IDX_UNDER_4MS = 4
private const val IDX_UNDER_8MS = 5
private const val IDX_UNDER_16MS = 6
private const val IDX_UNDER_32MS = 7
private const val IDX_UNDER_64MS = 8
private const val IDX_UNDER_128MS = 9
private const val IDX_UNDER_256MS = 10
private const val IDX_UNDER_512MS = 11
private const val IDX_UNDER_1024MS = 12
private const val IDX_OVER_1024MS = 13

private const val LATENCY_STRING_LENGTH = 129
private const val LATENCY_STRING_SPLIT_SIZE = 15

/**
 *
 * @author myeongju.jung
 */
class LatencyParser : Parser<Latency> {

    override fun parse(current: LocalDateTime, line: String): Latency {
        if (line.length != LATENCY_STRING_LENGTH) {
            return Latency(current, line.trim())
        }
        val items = line.split(delimiters = "|")
        if (items.size != LATENCY_STRING_SPLIT_SIZE) {
            return UNKNOWN_LATENCY
        }
        try {
            val under1ms = items[IDX_UNDER_1MS].toLogNumber()
            val under2ms = items[IDX_UNDER_2MS].toLogNumber()
            val under4ms = items[IDX_UNDER_4MS].toLogNumber()
            val under8ms = items[IDX_UNDER_8MS].toLogNumber()
            val under16ms = items[IDX_UNDER_16MS].toLogNumber()
            val under32ms = items[IDX_UNDER_32MS].toLogNumber()
            val under64ms = items[IDX_UNDER_64MS].toLogNumber()
            val under128ms = items[IDX_UNDER_128MS].toLogNumber()
            val under256ms = items[IDX_UNDER_256MS].toLogNumber()
            val under512ms = items[IDX_UNDER_512MS].toLogNumber()
            val under1024ms = items[IDX_UNDER_1024MS].toLogNumber()
            val over1024ms = items[IDX_OVER_1024MS].toLogNumber()

            return Latency(current, under1ms, under2ms, under4ms, under8ms, under16ms, under32ms, under64ms,
                    under128ms, under256ms, under512ms, under1024ms, over1024ms)
        } catch (e: Throwable) {
            return UNKNOWN_LATENCY
        }
    }
}

fun String.toLogNumber(): Long {
    val newValue = this.trim()
    return NumberUnit.values()
            .firstOrNull { it.match(newValue) }
            ?.toLong(newValue)
            ?: 0
}
