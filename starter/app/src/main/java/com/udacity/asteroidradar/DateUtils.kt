package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

        /**
         * Get date from formatted date string. Input must be formatted to "yyyy-MM-dd" (eg "2015-11-08)
         * Throws exception if result is null. (We can't create ParseException.)
         * Throws ParseException if parse fails.
         * @exceptionMessagePart will be appended to exception message on Exception.
         * We don't append to message [exceptionMessagePart] of ParseException.
         */
        fun getDateFromString(
            formattedDate: String,
            exceptionMessagePart: String = ""
        ): Date {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return formatter.parse(formattedDate)
                ?: throw Exception(
                    "Parse of formatted date $formattedDate leads to null. $exceptionMessagePart"
                )
        }
    }
}