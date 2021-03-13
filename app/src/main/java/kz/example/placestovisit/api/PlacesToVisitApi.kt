package kz.example.placestovisit.api

import com.google.gson.JsonObject
import io.reactivex.Single
import kz.example.placestovisit.model.GeoLocationDetailsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PlacesToVisitApi {

    @GET("https://en.wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=10000&gslimit=50&format=json")
    fun getPointsOfInteresets(
        @Query("gscoord") gscoord: String
    ): Single<Response<JsonObject>>

    @GET("https://en.wikipedia.org/w/api.php?action=query&prop=info|description|images&format=json")
    fun getPointsOfInteresetsDetails(
        @Query("pageids") pageIds: Int
    ): Single<Response<GeoLocationDetailsModel>>

    @GET
    fun getDirections(
        @Url url: String
    ): Single<Response<JsonObject>>
}