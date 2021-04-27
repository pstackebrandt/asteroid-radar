package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Room

class Room {

    /**
     * Describes interactions with database.
     */
    @Dao
    interface AsteroidDao {
        @Query("select * from DatabaseAsteroid")
        fun getAsteroids(): LiveData<List<DatabaseEntities.DatabaseAsteroid>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertAll(vararg videos: DatabaseEntities.DatabaseAsteroid)
    }

    /** The database */
    @Database(entities = [DatabaseEntities.DatabaseAsteroid::class], version = 1)
    abstract class AsteroidsDatabase : RoomDatabase() {
        abstract val asteroidDao: AsteroidDao
    }

    private lateinit var INSTANCE: AsteroidsDatabase

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
}