package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import java.util.*

/** Contains the database */
private lateinit var INSTANCE: AsteroidsDatabase

/**
 * Describes interactions with database according to data of Asteroids.
 */
@Dao
interface AsteroidDao {
    /** get all asteroids from database */

    @Query("select * from DatabaseAsteroid ORDER BY closeApproachDate DESC")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

//    @Query("select * from DatabaseAsteroid WHERE closeApproachDate == :targetDate ")
//    fun getAsteroids(targetDate: Date): LiveData<List<DatabaseAsteroid>>

    /**
     * startDate should be without time of day to get all asteroids of day!
     * time of endDate is not required currently.
     */
    @Query(
        "select * from DatabaseAsteroid WHERE closeApproachDate >= :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate DESC"
    )
    fun getAsteroidsWithinTimeSpan(startDate: Date, endDate: Date): LiveData<List<DatabaseAsteroid>>

    /** Insert asteroids into database. Replace asteroids,
     * that already exist. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

/** Describes the database */
@Database(entities = [DatabaseAsteroid::class], version = 1)
@TypeConverters(Converters::class)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

/** Get database. Creates database if it not already exists. */
fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            )
                .build()
        }
    }
    return INSTANCE
}
