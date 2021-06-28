package com.udacity.asteroidradar

import com.udacity.asteroidradar.DateUtils.Companion.toYearMonthsDays
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.util.*

/**
 * unit tests, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DateUtilsTest {

    class GetCurrentDateTimeTest {
        @Test
        fun should_return_date_object() {
            val expected = Date::class

            val actual = DateUtils.getCurrentDateTime()

            assertSame(expected, actual::class)
        }
    }

    class GetDate6DaysLaterTest {
        @Test
        fun should_return_date_object() {
            val expected = Date::class

            val actual = DateUtils.getDate6DaysLater(DateUtils.getCurrentDateTime())

            assertSame(expected, actual::class)
        }

        @Test
        fun should_return_correct_date() {
            val firstDay: Date = DateUtils.getDateFromString("2021-05-27")

            val expectedFormatted = "2021-06-02"
            val actual = DateUtils.getDate6DaysLater(firstDay)

            val actualFormatted = actual.toYearMonthsDays()
            assertEquals(expectedFormatted, actualFormatted)
        }
    }
}
