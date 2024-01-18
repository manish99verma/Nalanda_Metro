package com.manish.nalandametro.utils

import android.provider.ContactsContract.Data

class CustomEvent<out T>(content: T, private val isEvent: Boolean = true) :
    Event<T>(content) {
    override fun getContentIfNotHandled(): T? {
        return if (isEvent)
            super.getContentIfNotHandled()
        else
            super.peekContent()
    }
}