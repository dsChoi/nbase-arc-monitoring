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
 * nbase-arc 로그 모델 : Marked interface
 */
interface NbaseArcLog {
    fun isSuccess(): Boolean
    fun isError() = errorDescription.isNotEmpty()
    val errorDescription: String
    val loggedAt: LocalDateTime
}
