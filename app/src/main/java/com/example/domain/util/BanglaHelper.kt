package com.example.domain.util

import com.example.domain.model.AppLanguage
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.WindSpeedUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object BanglaHelper {

    private val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBanglaDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun formatNumber(number: Number, language: AppLanguage): String {
        return formatNumber(number.toString(), language)
    }

    fun formatNumber(str: String, language: AppLanguage): String {
        return if (language == AppLanguage.BANGLA) {
            toBanglaDigits(str)
        } else {
            str
        }
    }

    fun formatTemp(celsius: Double, unit: TemperatureUnit, language: AppLanguage): String {
        val value = if (unit == TemperatureUnit.FAHRENHEIT) {
            (celsius * 9 / 5) + 32
        } else {
            celsius
        }
        val rounded = value.roundToInt()
        val numStr = if (language == AppLanguage.BANGLA) toBanglaDigits(rounded.toString()) else rounded.toString()
        return "$numStr${unit.symbol}"
    }

    fun formatWindSpeed(kmh: Double, unit: WindSpeedUnit, language: AppLanguage): String {
        val value = when (unit) {
            WindSpeedUnit.KMH -> kmh
            WindSpeedUnit.MS -> kmh / 3.6
            WindSpeedUnit.MPH -> kmh * 0.621371
        }
        val formattedNum = String.format(Locale.US, "%.1f", value)
        val finalNum = if (language == AppLanguage.BANGLA) toBanglaDigits(formattedNum) else formattedNum
        val symbol = if (language == AppLanguage.BANGLA) unit.symbolBn else unit.symbolEn
        return "$finalNum $symbol"
    }

    fun formatRelativeTime(timestampMillis: Long, language: AppLanguage): String {
        val now = System.currentTimeMillis()
        val diffMillis = now - timestampMillis
        val diffSeconds = diffMillis / 1000
        val diffMinutes = diffSeconds / 60
        val diffHours = diffMinutes / 60

        return if (language == AppLanguage.BANGLA) {
            when {
                diffMinutes < 1 -> "এখনই"
                diffMinutes < 60 -> "${toBanglaDigits(diffMinutes.toString())} মিনিট আগে"
                diffHours < 24 -> "${toBanglaDigits(diffHours.toString())} ঘণ্টা আগে"
                else -> {
                    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)
                    toBanglaDigits(sdf.format(Date(timestampMillis)))
                }
            }
        } else {
            when {
                diffMinutes < 1 -> "Just now"
                diffMinutes < 60 -> "$diffMinutes min ago"
                diffHours < 24 -> "$diffHours hr ago"
                else -> {
                    val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.ENGLISH)
                    sdf.format(Date(timestampMillis))
                }
            }
        }
    }

    fun formatDayName(dateString: String, index: Int, language: AppLanguage): String {
        if (index == 0) {
            return if (language == AppLanguage.BANGLA) "আজ" else "Today"
        }
        if (index == 1) {
            return if (language == AppLanguage.BANGLA) "আগামীকাল" else "Tomorrow"
        }

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(dateString) ?: return dateString
            val dayFormat = SimpleDateFormat("EEEE", Locale.US)
            val dayName = dayFormat.format(date)

            return if (language == AppLanguage.BANGLA) {
                when (dayName.lowercase()) {
                    "sunday" -> "রবিবার"
                    "monday" -> "সোমবার"
                    "tuesday" -> "মঙ্গলবার"
                    "wednesday" -> "বুধবার"
                    "thursday" -> "বৃহস্পতিবার"
                    "friday" -> "শুক্রবার"
                    "saturday" -> "শনিবার"
                    else -> dayName
                }
            } else {
                dayName
            }
        } catch (e: Exception) {
            return dateString
        }
    }

    fun formatReadableDate(dateString: String, language: AppLanguage): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(dateString) ?: return dateString
            val dayMonthFormat = SimpleDateFormat("dd MMMM", Locale.US)
            val formatted = dayMonthFormat.format(date)
            return if (language == AppLanguage.BANGLA) {
                val parts = formatted.split(" ")
                val day = toBanglaDigits(parts[0])
                val month = when (parts.getOrNull(1)?.lowercase()) {
                    "january" -> "জানুয়ারি"
                    "february" -> "ফেব্রুয়ারি"
                    "march" -> "মার্চ"
                    "april" -> "এপ্রিল"
                    "may" -> "মে"
                    "june" -> "জুন"
                    "july" -> "জুলাই"
                    "august" -> "আগস্ট"
                    "september" -> "সেপ্টেম্বর"
                    "october" -> "অক্টোবর"
                    "november" -> "নভেম্বর"
                    "december" -> "ডিসেম্বর"
                    else -> parts.getOrNull(1) ?: ""
                }
                "$day $month"
            } else {
                formatted
            }
        } catch (e: Exception) {
            return dateString
        }
    }

    fun formatIsoTimeToHour(isoString: String, language: AppLanguage): String {
        return try {
            // isoString e.g. "2026-09-30T09:00"
            val parts = isoString.split("T")
            if (parts.size >= 2) {
                val timeParts = parts[1].split(":")
                val hour = timeParts[0].toIntOrNull() ?: 0
                val minute = timeParts.getOrNull(1) ?: "00"
                val amPm = if (hour >= 12) "PM" else "AM"
                val h12 = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                val formattedH12 = String.format(Locale.US, "%02d", h12)
                if (language == AppLanguage.BANGLA) {
                    "${toBanglaDigits(formattedH12)} $amPm"
                } else {
                    "$formattedH12 $amPm"
                }
            } else {
                isoString
            }
        } catch (e: Exception) {
            isoString
        }
    }
}
