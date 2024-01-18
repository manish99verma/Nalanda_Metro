package com.manish.nalandametro.utils

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    open fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content

}