package com.pyatek.compass.helpers

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

var toast: Toast? = null

fun AppCompatActivity.toast(text: String) {
    toast?.cancel()
    toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
    toast?.show()
}

fun Fragment.toast(text: String) {
    toast?.cancel()
    toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT)
    toast?.show()
}

fun View.visibility(isVisible: Boolean) {
    visibility = if(isVisible)
        View.VISIBLE
    else
        View.GONE
}