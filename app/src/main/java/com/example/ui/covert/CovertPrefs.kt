package com.example.ui.covert

import android.content.Context
import android.content.SharedPreferences

class CovertPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("covert_prefs", Context.MODE_PRIVATE)

    var isCovertEnabled: Boolean
        get() = prefs.getBoolean("is_covert_enabled", false)
        set(value) = prefs.edit().putBoolean("is_covert_enabled", value).apply()

    var disguiseType: String
        get() = prefs.getString("disguise_type", "calculator") ?: "calculator"
        set(value) = prefs.edit().putString("disguise_type", value).apply()

    var covertCode: String
        get() = prefs.getString("covert_code", "1337") ?: "1337"
        set(value) = prefs.edit().putString("covert_code", value).apply()
}
