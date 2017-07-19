package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.EMPTY_LATENCY
import io.redutan.nbasearc.monitoring.collector.Latency
import io.redutan.nbasearc.monitoring.collector.Parser
import java.time.LocalDateTime

const val IDX_MINUTE_AND_SECOND = 1
const val IDX_UNDER_1MS = 2
const val IDX_UNDER_2MS = 3
const val IDX_UNDER_4MS = 4
const val IDX_UNDER_8MS = 5
const val IDX_UNDER_16MS = 6
const val IDX_UNDER_32MS = 7
const val IDX_UNDER_64MS = 8
const val IDX_UNDER_128MS = 9
const val IDX_UNDER_256MS = 10
const val IDX_UNDER_512MS = 11
const val IDX_UNDER_1024MS = 12
const val IDX_OVER_1024MS = 13
const val LATENCY_STRING_LENGTH = 129
const val LATENCY_STRING_SPLIT_SIZE = 15
/**
 *
 * @author myeongju.jung
 */
class LatencyParser : Parser<Latency> {
    override fun parse(dateAndHour: LocalDateTime, line: String): Latency {
        if (line.contains("Exception")) {
            throw NbaseArcServerException(line.trim())
        }
        if (line.length != LATENCY_STRING_LENGTH) {
            return EMPTY_LATENCY
        }
        val items = line.split(delimiters = "|")
        if (items.size != LATENCY_STRING_SPLIT_SIZE) {
            return EMPTY_LATENCY
        }
        try {
            val minuteAndSecondString = items[IDX_MINUTE_AND_SECOND]
            val (minute, second) = toMinuteAndSecond(minuteAndSecondString)
            val under1ms = toLogCount(items[IDX_UNDER_1MS])
            val under2ms = toLogCount(items[IDX_UNDER_2MS])
            val under4ms = toLogCount(items[IDX_UNDER_4MS])
            val under8ms = toLogCount(items[IDX_UNDER_8MS])
            val under16ms = toLogCount(items[IDX_UNDER_16MS])
            val under32ms = toLogCount(items[IDX_UNDER_32MS])
            val under64ms = toLogCount(items[IDX_UNDER_64MS])
            val under128ms = toLogCount(items[IDX_UNDER_128MS])
            val under256ms = toLogCount(items[IDX_UNDER_256MS])
            val under512ms = toLogCount(items[IDX_UNDER_512MS])
            val under1024ms = toLogCount(items[IDX_UNDER_1024MS])
            val over1024ms = toLogCount(items[IDX_OVER_1024MS])

            return Latency(dateAndHour.changeMinuteAndSecond(minute, second), under1ms, under2ms, under4ms, under8ms, under16ms, under32ms, under64ms,
                    under128ms, under256ms, under512ms, under1024ms, over1024ms)
        } catch (e: Throwable) {
            return EMPTY_LATENCY
        }
    }

    private fun toMinuteAndSecond(minuteAndSecondString: String): Pair<Int, Int> {
        val minuteAndSecondStringArray = minuteAndSecondString.split(delimiters = ":")
        val minuteString = minuteAndSecondStringArray[0].trim()
        val secondString = minuteAndSecondStringArray[1].trim()
        return Pair(minuteString.toInt(), secondString.toInt())
    }
}

data class NbaseArcServerException(val exceptionString: String) : Throwable()

fun LocalDateTime.changeMinuteAndSecond(minutes: Int, seconds: Int): LocalDateTime {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minutes, seconds)
}

enum class NumberUnit(val symbol: String, val unit: Long) {
    NONE("", 1) {
        override fun match(value: String): Boolean {
            return values().filter { it != this }.none { it.match(value) }
        }
    },
    KILO("K", 1_000),
    MEGA("M", 1_000_000),

    GIGA("G", 1_000_000_000);

    open fun match(value: String): Boolean {
        return value.contains(symbol)
    }
    fun toLong(value: String): Long {
        return (value.replace(symbol, "").trim().toDouble() * unit).toLong()
    }

}

fun toLogCount(value: String): Long {
    val newValue = value.trim()
    return NumberUnit.values()
            .firstOrNull { it.match(newValue) }
            ?.toLong(newValue)
            ?: 0
}
