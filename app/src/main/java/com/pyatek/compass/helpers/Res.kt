package com.pyatek.compass.helpers

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.pyatek.compass.app.App

object Res {
    fun color(@ColorRes id: Int): Int = ContextCompat.getColor(App.appContext(), id)
    fun string(@StringRes id: Int): String = App.appContext().getString(id)
}