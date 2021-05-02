package com.udacity.asteroidradar

import com.udacity.asteroidradar.repository.AsteroidsRepository.Companion.ASTEROIDS_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

        /**
         * Get date from formatted date string. Input must be formatted to "yyyy-MM-dd" (eg "2015-11-08, ASTEROIDS_DATE_FORMAT)
         * Throws exception if result is null. (We can't create ParseException.)
         * Throws ParseException if parse fails.
         * @exceptionMessagePart will be appended to exception message on Exception.
         * We don't append to message [exceptionMessagePart] of ParseException.
         */
        fun getDateFromString(
            formattedDate: String,
            exceptionMessagePart: String = ""
        ): Date {
            val formatter = SimpleDateFormat(ASTEROIDS_DATE_FORMAT, Locale.US)
            return formatter.parse(formattedDate)
                ?: throw Exception(
                    "Parse of formatted date $formattedDate leads to null. $exceptionMessagePart"
                )
        }

        /**
         * inspired by https://stackoverflow.com/questions/47006254/how-to-get-current-local-date-and-time-in-kotlin
         * use by: val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")
         */
        fun Date.toString(
            format: String,
            locale: Locale = Locale.getDefault()
        ) = SimpleDateFormat(format, locale).format(this)

        /**
         * Get string from date in format: "yyyy-MM-dd"
         */
        fun Date.toYearMonthsDays() = this.toString(ASTEROIDS_DATE_FORMAT)

        /**
         * inspired by https://stackoverflow.com/questions/47006254/how-to-get-current-local-date-and-time-in-kotlin
         * use by: val date = getCurrentDateTime()
         */
        fun getCurrentDateTime() = Calendar.getInstance()
            .time

        /**
         * Get date that's the last day of 1 week. Week means 7 days, not monday until sunday.
         * @date ist the first day of this week. */
        fun getDate6DaysLater(date: Date) = addDays(date, 6)

        /**
         * inspired by https://stackoverflow.com/questions/1005523/how-to-add-one-day-to-a-date
         */
        private fun addDays(date: Date, days: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DATE, days) //minus number would decrement the days
            return calendar.time
        }
    }
}