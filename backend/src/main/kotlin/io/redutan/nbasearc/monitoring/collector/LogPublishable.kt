package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable
import java.time.LocalDateTime

/**
 * nbase-arc 로그 발행 인터페이스
 * @author myeongju.jung
 */
interface LogPublishable<T: NbaseArcLog> {
    fun observe(): Observable<T>
}

/**
 * nbase-arc 로그 모델
 */
interface NbaseArcLog {
    val clusterId: ClusterId
    val loggedAt: LocalDateTime
    val errorDescription: String?
    fun isSuccess(): Boolean = !isError() && !isUnknown()
    fun isError() = errorDescription != null
    fun isUnknown():Boolean
}
