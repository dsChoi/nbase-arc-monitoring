package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.ClusterId
import io.redutan.nbasearc.monitoring.collector.HeaderParser
import io.redutan.nbasearc.monitoring.collector.NbaseArcLogHeader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @author myeongju.jung
 */
class LogHeaderParser : HeaderParser {
    private val headerStringSplitSize = 3
    private val idxContent = 1

    override fun isHeader(line: String): Boolean {
        val items = line.split(delimiters = "|")
        return items.size == headerStringSplitSize
    }

    override fun parse(clusterId: ClusterId, line: String): NbaseArcLogHeader {
        if (line.contains("Exception")) {
            // 오류 생성자
            return NbaseArcLogHeader(clusterId, LocalDateTime.now(), errorDescription = line.trim())
        }
        if (!isHeader(line)) {
            return NbaseArcLogHeader.unknown()
        }
        val items = line.split(delimiters = "|")
        val content = items[idxContent].trim()
        val logDatetime = getLogDatetime(content)
        return NbaseArcLogHeader(clusterId, logDatetime)
    }

    private val dateTimeStringLength = 19

    private fun getLogDatetime(content: String): LocalDateTime {
        val datetimeString = content.trim().substring(0, dateTimeStringLength)
        return LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}
