package com.manish.nalandametro.utils

class Resource<out T> private constructor(
    val status: Status,
    val data: T?,
    val msg: String?
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> error(data: T?, msg: String): Resource<T> {
            return Resource(Status.FAILED, data, null)
        }
    }

    enum class Status {
        SUCCESS,
        LOADING,
        FAILED
    }
}