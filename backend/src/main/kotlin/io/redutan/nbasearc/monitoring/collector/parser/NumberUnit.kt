package io.redutan.nbasearc.monitoring.collector.parser

import java.math.BigDecimal

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

    /**
     * "69.40 K" 를 숫자로 변경
     */
    fun toLong(value: String): Long {
        // BigDecimal 을 사용하는 이유는 부동소수점 정확도 이슈 때문
        val significand = BigDecimal.valueOf(value.replace(symbol, "").trim().toDouble())   // 69.40
        val unit = BigDecimal.valueOf(unit) // K
        return significand.multiply(unit).toLong() // 69.40 * 1_000
    }
}

fun String.toLogNumber(): Long {
    val newValue = this.trim()
    return NumberUnit.values()
        .firstOrNull { it.match(newValue) }
        ?.toLong(newValue)
        ?: 0
}
