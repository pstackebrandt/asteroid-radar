package com.udacity.asteroidradar.database

import androidx.room.TypeConverter
import androidx.room.*
import java.util.*

//@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
