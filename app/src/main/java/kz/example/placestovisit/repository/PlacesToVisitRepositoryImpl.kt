package kz.example.placestovisit.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import kz.example.placestovisit.api.PlacesToVisitApi
import kz.example.placestovisit.model.GeoLocationDetailsModel
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes
import kz.example.placestovisit.repository.exceptions.mapBodyMessages
import kz.example.placestovisit.repository.exceptions.mapNetworkErrors
import javax.inject.Inject

class PlacesToVisitRepositoryImpl @Inject constructor(
    private val placesToVisitApi: PlacesToVisitApi,
    private val gson: Gson
) : PlacesToVisitRepository {

    override fun getPointsOfInteresets(latitude: Double, longitude: Double): Single<List<GeoSearchModel>> {
        return placesToVisitApi.getPointsOfInteresets("${latitude}|${longitude}")
            .mapBodyMessages(gson)
            .mapNetworkErrors(gson)
            .map { response ->
                val json = response.get("query")?.asJsonObject?.get("geosearch")
                val type = object : TypeToken<List<GeoSearchModel>>() {}.type
                gson.fromJson(json, type) as List<GeoSearchModel>
            }
    }

    override fun getPointsOfInteresetsDetails(pageId: Int): Single<GeoLocationDetailsModel> {
        return placesToVisitApi.getPointsOfInteresetsDetails(pageId)
            .mapBodyMessages(gson)
            .mapNetworkErrors(gson)
            .map { response ->
                response
            }
    }

    override fun getDirections(url: String): Single<Routes> {
        return placesToVisitApi.getDirections(url)
            .mapBodyMessages(gson)
            .mapNetworkErrors(gson)
            .map { response ->
                gson.fromJson(response, Routes::class.java)
            }
    }

}

interface PlacesToVisitRepository {
    fun getPointsOfInteresets(latitude: Double, longitude: Double): Single<List<GeoSearchModel>>
    fun getPointsOfInteresetsDetails(pageId: Int): Single<GeoLocationDetailsModel>
    fun getDirections(url: String): Single<Routes>
}
