package io.redutan.nbasearc.monitoring.collector

import io.reactivex.Observable

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
    fun isError(): Boolean
    val errorDescription: String
}
