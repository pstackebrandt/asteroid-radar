package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

/** Contains the database */
private lateinit var INSTANCE: AsteroidsDatabase

/**
 * Describes interactions with database according to data of Asteroids.
 */
@Dao
interface AsteroidDao {
    /** get all asteroids from database */
    @Query("select * from DatabaseAsteroid")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    /** Insert asteroids into database. Replace asteroids,
     * that already exist. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

/** Describes the database */
@Database(entities = [DatabaseAsteroid::class], version = 1)
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
            ).build()
        }
    }
    return INSTANCE
}