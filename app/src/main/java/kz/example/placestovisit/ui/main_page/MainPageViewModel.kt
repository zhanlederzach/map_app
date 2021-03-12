package kz.example.placestovisit.ui.main_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.gson.JsonObject
import com.main.ui_core.base.BaseViewModel
import com.main.ui_core.base.Loading
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes
import kz.example.placestovisit.repository.PlacesToVisitRepository
import kz.example.placestovisit.utils.MapUtils
import javax.inject.Inject
import kotlin.math.abs

class MainPageViewModel @Inject constructor(
    private val placesToVisitRepository: PlacesToVisitRepository
) : BaseViewModel() {

    val pointsLiveData: LiveData<PointOfInterestState>
        get() = _pointsLiveData
    val directionsLiveData: LiveData<DestinationPathWithDescription>
        get() = _directionsLiveData

    private val _directionsLiveData = MutableLiveData<DestinationPathWithDescription>()
    private val _pointsLiveData = MutableLiveData<PointOfInterestState>()

    private var currentLocation: LatLng? = null

    fun loadClosePointOfInterests(latitude: Double, longitude: Double) {
        if (currentLocation != null &&
            MapUtils.meterDistanceBetweenPoints(latitude, longitude, currentLocation!!.latitude, currentLocation!!.longitude) > 0) {
            return
        }
        currentLocation = LatLng(latitude, longitude)
        addDisposable(
            placesToVisitRepository.getPointsOfInteresets(latitude, longitude).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { loadingLiveData.postValue(Loading.Show) }
                .doFinally { loadingLiveData.postValue(Loading.Hide) }
                .subscribe(
                    { result ->
                        _pointsLiveData.value = PointOfInterestState.Result(result)
                    },
                    { error ->
                        _pointsLiveData.value = PointOfInterestState.Error(error.message)
                    }
                )
        )
    }

    fun getDirectionWithDesctination(
        origin: LatLng,
        destination: LatLng,
        geoSearchModel: GeoSearchModel?
    ) {
        addDisposable(
            Single.zip(
                placesToVisitRepository.getDirections(getRequestedUrl(origin, destination)!!),
                placesToVisitRepository.getPointsOfInteresetsDetails(geoSearchModel?.pageId!!.toInt()),
                BiFunction <Routes, JsonObject, Pair<Routes, Routes>> { routes, routes2 ->
                    Pair(routes, routes)
                }
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { loadingLiveData.postValue(Loading.Show) }
                .doFinally { loadingLiveData.postValue(Loading.Hide) }
                .subscribe(
                    { result ->
                        _directionsLiveData.value = DestinationPathWithDescription.Result(result.first, geoSearchModel)
                    },
                    { error ->
                        _directionsLiveData.value = DestinationPathWithDescription.Error(error.message)
                    }
                )
        )
    }

    private fun getRequestedUrl(origin: LatLng, destination: LatLng): String? {
        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude
        val strDestination =
            "destination=" + destination.latitude + "," + destination.longitude
        val sensor = "sensor=false"
        val mode = "mode=driving"
        val param = "$strOrigin&$strDestination&$sensor&$mode&"
        val output = "json"
        val APIKEY = "key=AIzaSyC-O7tQQV5-rY6tKSIchaaNyrftPuRw1Mk"
        val result = "https://maps.googleapis.com/maps/api/directions/$output?$param$APIKEY"
        return result
    }

    fun removeDisposables() {
        onCleared()
    }

    sealed class PointOfInterestState {
        data class Result(val points: List<GeoSearchModel>) : PointOfInterestState()
        data class Error(val error: String?) : PointOfInterestState()
    }

    sealed class DestinationPathWithDescription {
        data class Result(val points: Routes, val geoSearchModel: GeoSearchModel?) : DestinationPathWithDescription()
        data class Error(val error: String?) : DestinationPathWithDescription()
    }
}