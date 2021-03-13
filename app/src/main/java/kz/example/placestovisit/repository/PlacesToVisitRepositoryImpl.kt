package kz.example.placestovisit.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import kz.example.placestovisit.api.PlacesToVisitApi
import kz.example.placestovisit.model.GeoLocationDetailsModel
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes
import javax.inject.Inject

class PlacesToVisitRepositoryImpl @Inject constructor(
    private val placesToVisitApi: PlacesToVisitApi,
    private val gson: Gson
) : PlacesToVisitRepository {

    override fun getPointsOfInteresets(latitude: Double, longitude: Double): Single<List<GeoSearchModel>> {
        return placesToVisitApi.getPointsOfInteresets("${latitude}|${longitude}").flatMap { response ->
            if (response.isSuccessful) {
                val json = response.body()?.get("query")?.asJsonObject?.get("geosearch")
                val type = object : TypeToken<List<GeoSearchModel>>() {}.type
                val list: List<GeoSearchModel> = gson.fromJson(json, type)
                Single.just(list)
            } else {
                Single.error(Throwable("Not Found"))
            }
        }
    }

    override fun getPointsOfInteresetsDetails(pageId: Int): Single<GeoLocationDetailsModel> {
        return placesToVisitApi.getPointsOfInteresetsDetails(pageId).flatMap { response ->
            if (response.isSuccessful) {
                Single.just(response.body())
            } else {
                Single.error(Throwable("Not Found"))
            }
        }
    }

    override fun getDirections(url: String): Single<Routes> {
        return placesToVisitApi.getDirections(url).flatMap { response ->
            if (response.isSuccessful) {
                val routes: Routes = gson.fromJson(response.body(), Routes::class.java)
                Single.just(routes)
            } else {
                Single.error(Throwable("Not Found"))
            }
        }
    }

}

interface PlacesToVisitRepository {
    fun getPointsOfInteresets(latitude: Double, longitude: Double): Single<List<GeoSearchModel>>
    fun getPointsOfInteresetsDetails(pageId: Int): Single<GeoLocationDetailsModel>
    fun getDirections(url: String): Single<Routes>
}
