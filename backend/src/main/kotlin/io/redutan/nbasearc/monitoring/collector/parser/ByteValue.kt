package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.parser.ByteValue.Unit
import io.redutan.nbasearc.monitoring.collector.parser.ByteValue.Unit.B

val EMPTY_BYTE_VALUE = ByteValue(0.0, unit = B)

data class ByteValue(val value: Double, val unit: Unit) {
    enum class Unit {
        B, KB, MB, GB, TB;
    }
}

private const val IDX_VALUE = 0
private const val IDX_UNIT = 1

internal fun String.toByteValue(): ByteValue {
    val byteValueString = this.trim()
    val items = byteValueString.split(" ")
    val value = items[IDX_VALUE].toDouble()
    val unit = if (items.size > 1) Unit.valueOf(items[IDX_UNIT].trim()) else B
    return ByteValue(value, unit)
}
