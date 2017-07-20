package io.redutan.nbasearc.monitoring.collector.parser

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