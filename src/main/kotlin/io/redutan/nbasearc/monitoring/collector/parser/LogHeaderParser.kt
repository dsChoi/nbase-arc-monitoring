package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.HeaderParser
import io.redutan.nbasearc.monitoring.collector.NbaseArcLogHeader
import io.redutan.nbasearc.monitoring.collector.UNKNOWN
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

    override fun parse(line: String): NbaseArcLogHeader {
        if (line.contains("Exception")) {
            throw NbaseArcServerException(line.trim())
        }
        if (!isHeader(line)) {
            return NbaseArcLogHeader(LocalDateTime.now(), UNKNOWN)
        }
        val items = line.split(delimiters = "|")
        val content = items[idxContent].trim()
        val logDatetime = getLogDatetime(content)
        val clusterName = getClusterName(content)
        return NbaseArcLogHeader(logDatetime, clusterName)
    }

    private val dateTimeStringLength = 19

    private fun getLogDatetime(content: String): LocalDateTime {
        val datetimeString = content.trim().substring(0, dateTimeStringLength)
        return LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    private val clusterTitle = "CLUSTER:"

    private fun getClusterName(content: String): String {
        val clusterIndex = content.indexOf(clusterTitle)
        if (clusterIndex < 0) {
            return UNKNOWN
        }
        return content.substring(clusterIndex + clusterTitle.length).trim()
    }
}
