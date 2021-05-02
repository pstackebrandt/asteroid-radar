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
private const val ASTEROIDS_API_DATE_FORMAT = "yyyy-MM-dd"

enum class AsteroidsApiFilter(val value: String) {
    VIEW_WEEK_ASTEROIDS("view.week.asteroids"),
    VIEW_TODAY_ASTEROIDS
        ("view.today.asteroids"),
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
     * TODO actualize comment
     * Returns a Coroutine [List] of [Asteroid] which can be fetched with
     * await() if in a Coroutine scope. The @GET annotation indicates that the
     * "planetary/apod" endpoint will be requested with the GET HTTP method
     */
    //@GET("neo/rest/v1/feed?start_date=2021-04-17&end_date=2021-04-18&api_key=6Q4cMVxnARH5Jlgx976YDc41iz860XEOthEXQtKR")
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date")
        startDate: String = "2021-04-17",
        @Query("end_date")
        endDate: String = "2021-04-20",
        @Query("api_key")
        key: String = API_KEY
    )
    : Any

    // I need a JsonObject or something I can transform into a JsonObject.
    // I tried those:
    // : Response<String>  // Error message: "Expected a string but was BEGIN_OBJECT at path $ "
    // : List<Asteroid>    // Error message: "Expected a string but was BEGIN_OBJECT at path $ "
    // : Any // Works, but will be a LinkedHashTreeMap, I don't know how to transform it to JsonObject.
    // : LinkedHashMap<String,Any> // Error
    // : Response<JSONObject> //java.lang.IllegalArgumentException: Unable to create converter for class org.json.JSONObject for method AsteroidApiService.getAsteroids
    // : LinkedHashTreeMap<*, *> //java.lang.IllegalArgumentException: Unable to create converter for com.google.gson.internal.LinkedHashTreeMap<?, ?>
    //    for method AsteroidApiService.getAsteroids

    // https://api.nasa.gov/neo/rest/v1/feed?start_date=2021-04-17&end_date=2021-04-18&api_key=6Q4cMVxnARH5Jlgx976YDc41iz860XEOthEXQtKR

    /**
     * A public Api object that exposes the lazy-initialized Retrofit service
     */
    object AsteroidsApi {
        val retrofitService: AsteroidApiService by lazy {
            retrofit.create(AsteroidApiService::class.java)
        }
    }
}