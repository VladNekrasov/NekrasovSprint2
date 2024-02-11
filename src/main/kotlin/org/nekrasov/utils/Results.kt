package org.nekrasov.utils

sealed class ServiceResult<out T> {
    data class Success<out T>(val data: T) : ServiceResult<T>()
    data class Error(val error: ErrorCode) : ServiceResult<Nothing>()
    fun get(): T? {
        return when (this) {
            is Success -> data
            is Error -> null
        }
    }
    fun getErrorCode(): ErrorCode? {
        return when (this) {
            is Error -> error
            is Success -> null
        }
    }
}
