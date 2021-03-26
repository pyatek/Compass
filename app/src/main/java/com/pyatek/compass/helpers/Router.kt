package com.pyatek.compass.helpers

import android.app.Dialog
import android.content.Context
import com.pyatek.compass.model.Coordinates

object Router {
    fun showInsertCoordinatesDialog(
        context: Context,
        currentLat: Double,
        currentLon: Double,
        onAcceptCoordinatesListener: (Coordinates) -> Unit
    ): Dialog {
        return DialogFactory.createInsertCoordinatesDialog(context, currentLat, currentLon, onAcceptCoordinatesListener)
    }

    fun showCalibrateDialog(context: Context, accuracy: Int): Dialog {
        return DialogFactory.createCompassAccuracyDialog(context, accuracy)
    }
}