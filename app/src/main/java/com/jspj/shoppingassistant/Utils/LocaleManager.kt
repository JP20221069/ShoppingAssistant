package com.jspj.shoppingassistant.Utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class LocaleManager(private val context: Context) {

    private val LANGUAGE_KEY = "language_key"
    private val LANGUAGE_DEFAULT = "en"

    fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)

        val editor: SharedPreferences.Editor =
            context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
        editor.putString(LANGUAGE_KEY, language)
        editor.apply()

        context.createConfigurationContext(configuration)
    }

    fun getSavedLocale(): String {
        val sharedPreferences =
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString(LANGUAGE_KEY, LANGUAGE_DEFAULT) ?: LANGUAGE_DEFAULT
    }
}