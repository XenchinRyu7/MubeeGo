package com.saefulrdevs.mubeego.core.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

object Utils {

    @SuppressLint("SimpleDateFormat")
    fun changeStringToDateFormat(value: String?): String {
        if (value.isNullOrBlank()) return "-"

        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val objDate = dateFormat.parse(value)

            val dateFormat2 = SimpleDateFormat("MMM dd, yyyy")
            objDate?.let { dateFormat2.format(it) } ?: "-"
        } catch (e: Exception) {
            e.printStackTrace()
            "-"
        }
    }


    fun changeMinuteToDurationFormat(duration: Int): String {
        val hour = duration / 60
        val minute = duration % 60

        return if (hour > 0) {
            "$hour h $minute m"
        } else {
            "$minute m"
        }
    }

    @SuppressLint("NewApi")
    fun changeStringDateToYear(date: String?): Int {
        if (date.isNullOrBlank()) return -1

        return try {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val localDate: LocalDate = LocalDate.parse(date, formatter)
            localDate.year
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            -1
        }
    }

}