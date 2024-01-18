package com.manish.nalandametro.data.pref

import android.content.SharedPreferences

class PrefManager(private val sharedPref: SharedPreferences) {
    fun getDefaultPref() = sharedPref
}