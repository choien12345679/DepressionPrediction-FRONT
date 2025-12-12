package com.imp.presentation.widget.utils

import android.annotation.SuppressLint
import com.imp.presentation.widget.extension.resetCalendarTime
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Date Util
 */
class DateUtil {

    companion object {

        /**
         * Get Current Date (yyyyMMdd)
         */
        fun getCurrentDate(): String {

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            return dateFormat.format(calendar.time)
        }

        /**
         * Get Yesterday Date (yyyy-MM-dd)
         */
        fun getYesterdayDate(): String {

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            return dateFormat.format(calendar.time)
        }

        /**
         * Get Current Date
         */
        fun getCurrentDateWithText(pattern: String): String {

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

            return dateFormat.format(calendar.time)
        }

        /**
         * Get Date with Year, Month, Day Text (yyyy년 MM월 dd일)
         */
        fun getDateWithYearMonthDay(calendar: Calendar?): String {

            if (calendar == null) return ""

            val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            return dateFormat.format(calendar?.time)
        }

        /**
         * Get Current Date (오늘, MM월 dd일)
         */
        fun getCurrentMonthDay(): String {

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("오늘, MM월 dd일", Locale.getDefault())

            return dateFormat.format(calendar.time)
        }

        /**
         * Get Date (MM월 dd일)
         */
        fun getMonthDay(calendar: Calendar): String {

            val todayCalendar = Calendar.getInstance()

            todayCalendar.resetCalendarTime()
            calendar.resetCalendarTime()

            // 오늘 날짜인 경우
            if (calendar == todayCalendar) {
                return getCurrentMonthDay()
            }

            val dateFormat = SimpleDateFormat("MM월 dd일", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }

        /**
         * Get Current Weekly (MM월 dd일 ~ MM월 dd일)
         */
        fun getCurrentWeekly(): String {

            val today = LocalDate.now()
            val startOfWeek = today.with(DayOfWeek.SUNDAY).minusDays(7)
            val endOfWeek = startOfWeek.plusDays(6)

            val formatter = DateTimeFormatter.ofPattern("MM월 dd일")

            val formattedStartOfWeek = startOfWeek.format(formatter)
            val formattedEndOfWeek = endOfWeek.format(formatter)

            return "$formattedStartOfWeek ~ $formattedEndOfWeek"
        }

        /**
         * Get Current Weekly (MM월 dd일 ~ MM월 dd일)
         */
        fun getWeeklyWithLast(calendar: Calendar): String {

            val endOfWeek = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
            val startOfWeek = endOfWeek.minusDays(6)

            val formatter = DateTimeFormatter.ofPattern("MM월 dd일")

            val formattedStartOfWeek = startOfWeek.format(formatter)
            val formattedEndOfWeek = endOfWeek.format(formatter)

            return "$formattedStartOfWeek ~ $formattedEndOfWeek"
        }

        /**
         * Get Current Time (HH:mm:ss)
         */
        fun getCurrentTime(): String {

            val calendar = Calendar.getInstance()
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            return timeFormat.format(calendar.time)
        }

        /**
         * String (yyyy-MM-dd) to LocalDate
         */
        fun stringToLocalDate(string: String): LocalDate {

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return LocalDate.parse(string, formatter)
        }

        /**
         * Timestamp to Time (HH:mm:ss)
         */
        fun timestampToTimeSeconds(timestamp: Long): String {

            val instant = Instant.ofEpochMilli(timestamp)
            val dateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            return formatter.format(dateTime)
        }

        /**
         * Timestamp to Time (HH:mm:ss)
         */
        fun timestampToTimeMin(timestamp: Long): String {

            val instant = Instant.ofEpochMilli(timestamp)
            val dateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            return formatter.format(dateTime)
        }

        /**
         * Timestamp to Screen Time (hour, minute)
         */
        fun timestampToScreenTime(timestamp: Long): Pair<Int, Int> {

            val timestampSeconds = timestamp / 1000
            val hour = timestampSeconds / 3600
            val minute = (timestampSeconds % 3600) / 60

            return Pair(hour.toInt(), minute.toInt())
        }

        /**
         * Get Locale Date
         */
        fun getLocaleDate(timestamp: Long): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        }

        /**
         * Get Hour
         */
        fun getHour(timestamp: Long): Int {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).hour
        }

        /**
         * Get Minute
         */
        fun getMin(timestamp: Long): Int {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).minute
        }

        /**
         * Timestamp to Data
         */
        @SuppressLint("SimpleDateFormat")
        fun timestampToData(timestamp: Long): String {

            val timestamp = if (timestamp == 0L) System.currentTimeMillis() else timestamp
            return SimpleDateFormat("yyyy-MM-dd").format(Date(timestamp))
        }

        /**
         * Timestamp to Data
         */
        @SuppressLint("SimpleDateFormat")
        fun timestampToDataMin(timestamp: Long): String {

            val timestamp = if (timestamp == 0L) System.currentTimeMillis() else timestamp
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(timestamp))
        }

        /**
         * String to (yyyy-MM-dd) String
         */
        @SuppressLint("SimpleDateFormat")
        fun stringToDate(date: String): String {

            return try {
                SimpleDateFormat("yyyy-MM-dd").parse(date).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                getCurrentDateWithText("yyyy-MM-dd")
            }
        }

        /**
         * Calendar to (yyyy-MM-dd) String
         */
        fun calendarToServerFormat(calendar: Calendar): String {

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }
    }
}
