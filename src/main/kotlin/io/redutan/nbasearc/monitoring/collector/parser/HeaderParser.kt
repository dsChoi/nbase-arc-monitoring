package io.redutan.nbasearc.monitoring.collector.parser

import io.redutan.nbasearc.monitoring.collector.NbaseArcLog
import io.redutan.nbasearc.monitoring.collector.Parser
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val UNKNOWN = "UNKNOWN"

/**
 *
 * @author myeongju.jung
 */
class HeaderParser : Parser<LogHeader>{
    private val headerStringSplitSize = 3
    private val idxContent = 1

    override fun parse(dateAndHour: LocalDateTime, line: String): LogHeader {
        if (line.contains("Exception")) {
            throw NbaseArcServerException(line.trim())
        }
        val items = line.split(delimiters = "|")
        if (items.size != headerStringSplitSize) {
            return LogHeader(LocalDateTime.now(), UNKNOWN)
        }
        val content = items[idxContent].trim()
        val logDatetime = getLogDatetime(content)
        val clusterName = getClusterName(content)
        return LogHeader(logDatetime, clusterName)
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

data class LogHeader(val current: LocalDateTime, val clusterName: String) : NbaseArcLog {
    fun isUnknown(): Boolean {
        return UNKNOWN == clusterName
    }
}
