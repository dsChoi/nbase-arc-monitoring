package io.redutan.nbasearc.monitoring.collector

import java.time.LocalDateTime

/**
 *
 * @author myeongju.jung
 */
interface Parser<out T : NbaseArcLog> {
    fun parse(dateAndHour: LocalDateTime, line: String): T
}