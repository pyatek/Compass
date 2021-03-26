package com.pyatek.compass.helpers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.hardware.SensorManager
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import com.pyatek.compass.R
import com.pyatek.compass.app.App
import com.pyatek.compass.model.Coordinates
import kotlinx.android.synthetic.main.dialog_coordinates_insert.*
import kotlinx.android.synthetic.main.dialog_coordinates_insert.dialogCoordinatesAcceptButton
import kotlinx.android.synthetic.main.dialog_sensor_accuracy.*

object DialogFactory {

    fun createInsertCoordinatesDialog(context: Context,
                                      currentLat: Double,
                                      currentLon: Double,
                                      onAcceptCoordinatesListener: (Coordinates) -> Unit): Dialog {
        val coordinatesDialog = Dialog(context)
        coordinatesDialog.setContentView(R.layout.dialog_coordinates_insert)

        coordinatesDialog.dialogCoordinatesLatitudeEditText.setText(currentLat.toString())
        coordinatesDialog.dialogCoordinatesLongitudeEditText.setText(currentLon.toString())

        coordinatesDialog.dialogCoordinatesAcceptButton.setOnClickListener {
            val lat = coordinatesDialog.dialogCoordinatesLatitudeEditText.text.toString()
            val lon = coordinatesDialog.dialogCoordinatesLongitudeEditText.text.toString()
            if (checkCoordinatesCorrect(lat, lon)) {
                onAcceptCoordinatesListener.invoke(Coordinates(lat.toDouble(), lon.toDouble()))
                coordinatesDialog.dismiss()
            }
        }

        coordinatesDialog.show()

        val lp = WindowManager.LayoutParams()
        val window = coordinatesDialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        lp.copyFrom(window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = lp

        return coordinatesDialog
    }

    fun createCompassAccuracyDialog(context: Context,
                                    accuracy: Int): Dialog {
        val accuracyDialog = Dialog(context)
        accuracyDialog.setContentView(R.layout.dialog_sensor_accuracy)

        var accuracyColor = Res.color(R.color.green)
        var accuracyText = Res.string(R.string.accuracy_high)

        accuracyDialog.dialogCoordinatesAcceptButton.setOnClickListener {
            accuracyDialog.dismiss()
        }

        when(accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                accuracyText = Res.string(R.string.accuracy_low)
                accuracyColor = Res.color(R.color.red)
            }
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                accuracyText = Res.string(R.string.accuracy_medium)
                accuracyColor = Res.color(R.color.orange)
            }
        }

        val span1 = SpannableString(Res.string(R.string.accuracy_status))
        val span2 = SpannableString(accuracyText)

        span2.setSpan(
            ForegroundColorSpan(accuracyColor),
            0,
            accuracyText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span2.setSpan(
            Typeface.DEFAULT_BOLD,
            0,
            accuracyText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        accuracyDialog.sensorAccuracyStateTextView.text = TextUtils.concat(span1, " ", span2)

        accuracyDialog.show()

        return accuracyDialog
    }

    private fun checkCoordinatesCorrect(lat: String, lon: String): Boolean {
        val latDouble = lat.toDoubleOrNull()
        val lonDouble = lon.toDoubleOrNull()

        return latDouble != null && lonDouble != null
    }
}