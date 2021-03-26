package com.pyatek.compass.helpers

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsManager(val activity: Activity, val list: List<String>, val code: Int) {
    fun checkPermissions(): Boolean {
        return isPermissionsGranted() != PackageManager.PERMISSION_GRANTED
    }

    private fun isPermissionsGranted(): Int {
        var counter = 0;
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }

    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_DENIED) return permission
        }
        return ""
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
    }
}