package com.pyatek.compass.helpers

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    const val DATE_DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm"

    fun getFormattedDateFromTimestamp(timestamp: Long, format: String): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat(format)
        return dateFormat.format(date)
    }
}