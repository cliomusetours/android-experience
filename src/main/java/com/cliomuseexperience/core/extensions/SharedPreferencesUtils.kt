package com.cliomuseexperience.core.extensions

import android.content.Context


fun saveLastTourInfo(context: Context, tourId: Int, langId: Int) {
    val sharedPreferences = context.getSharedPreferences("tour_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit()
        .putInt("last_tour_id", tourId)
        .putInt("last_lang_id", langId)
        .apply()
}

fun getLastTourId(context: Context): Int? {
    val sharedPreferences = context.getSharedPreferences("tour_prefs", Context.MODE_PRIVATE)
    val tourId = sharedPreferences.getInt("last_tour_id", -1)
    return if (tourId != -1) tourId else null
}

fun getLastLangId(context: Context): Int? {
    val sharedPreferences = context.getSharedPreferences("tour_prefs", Context.MODE_PRIVATE)
    val langId = sharedPreferences.getInt("last_lang_id", -1)
    return if (langId != -1) langId else null
}
