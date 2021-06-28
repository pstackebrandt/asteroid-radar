package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = Constants.BASE_URL
private const val API_KEY = Constants.API_KEY

/**
 * Describes amount of asteroids.
 */
enum class AsteroidsApiFilter(val value: String) {
    VIEW_WEEK_ASTEROIDS("view.week.asteroids"),
    VIEW_TODAY_ASTEROIDS("view.today.asteroids"),
    VIEW_SAVED_ASTEROIDS("view.saved.asteroids")
}

/**
 * Build the Moshi object that Retrofit will be using, making sure
 * to add the Kotlin adapter for full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a
 * Moshi converter with our Moshi object pointing to the desired URL
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getAsteroids] method
 */
interface AsteroidApiService {

    /**
     * Returns a Coroutine [List] of [Asteroid] which can be fetched with
     * await() if in a Coroutine scope.
     */
    //@GET("neo/rest/v1/feed?start_date=2021-04-17&end_date=2021-04-18&api_key=MyKey")
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date")
        startDate: String,
        @Query("end_date")
        endDate: String,
        @Query("api_key")
        key: String = API_KEY
    )
            : Any

    /**
     * Returns a Coroutine [List] of Daily Picture data which can be fetched with
     * await() if in a Coroutine scope.
     */
    // @GET("planetary/apod?api_key=6Q4cMVxnARH5Jlgx976YDc41iz860XEOthEXQtKR")
    // https://api.nasa.gov/planetary/apod?api_key=6Q4cMVxnARH5Jlgx976YDc41iz860XEOthEXQtKR
    @GET("planetary/apod")
    suspend fun getDailyPictureData(
        @Query("api_key")
        key: String = API_KEY
    )
            : Any

    /**
     * A public Api object that exposes the lazy-initialized Retrofit service
     */
    object AsteroidsApi {
        val retrofitService: AsteroidApiService by lazy {
            retrofit.create(AsteroidApiService::class.java)
        }
    }
}
