package kz.example.placestovisit.ui.main_page

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.main.ui_core.base.BaseFragment
import com.main.ui_core.extensions.showToast
import kz.example.placestovisit.R
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes
import kz.example.placestovisit.ui.point_details_bottom_sheet.BottomSheetDialogDetails
import kz.example.placestovisit.utils.MapUtils
import javax.inject.Inject

class MainPageFragment : BaseFragment(), GoogleMap.OnMarkerClickListener {

    companion object {
        fun newInstance(data: Bundle? = null) = MainPageFragment()
            .apply {
            arguments = data
        }

        const val REQUEST_LOCATION_PERMISSION = 1000
        const val DISTANCE_LIMIT_FOR_UPDATE = 10
        const val BOTTOM_SHEET_POINT_DETAILS = "BOTTOM_SHEET_POINT_DETAILS"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainPageViewModel::class.java)
    }

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private var currLocationMarker: Marker? = null
    private var origin: LatLng? = null
    private var currentRoute: Routes? = null
    private var polyline: Polyline? = null

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private var isCurrentLocationLoaded = false

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                var distance = 20.0
                if (origin != null) {
                    distance = MapUtils.meterDistanceBetweenPoints(location.latitude, location.longitude, origin!!.latitude, origin!!.longitude)
                }
                if (distance > DISTANCE_LIMIT_FOR_UPDATE) {
                    origin = LatLng(location.latitude, location.longitude)
                    if (currLocationMarker != null) {
                        currLocationMarker?.remove()
                    }

                    val latLng = LatLng(location.latitude, location.longitude)
                    val markerOptions = MarkerOptions()
                    markerOptions.apply {
                        position(latLng)
                        title("Current Position")
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    }
                    currLocationMarker = googleMap.addMarker(markerOptions)
                    if (!isCurrentLocationLoaded) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0F))
                    }
                    isCurrentLocationLoaded = true
                    viewModel.loadClosePointOfInterests(location.latitude, location.longitude)
                }
            }
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 10_000
        fastestInterval = 5_000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var hashMapPoints: HashMap<String, GeoSearchModel> = hashMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view, savedInstanceState)
        initGoogleMap()
        setObservers()
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        mapView.onPause()
    }

    override fun onDestroyView() {
        viewModel.removeDisposables()
        super.onDestroyView()
    }

    private fun bindViews(view: View, savedInstanceState: Bundle?) = with(view) {
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
    }

    private fun initGoogleMap() {
        showLoading()
        mapView.getMapAsync { map ->
            googleMap = map
            hideLoading()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    initGoogleMapProperties()
                } else {
                    checkLocationPermission()
                }
            } else {
                initGoogleMapProperties()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initGoogleMapProperties() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMarkerClickListener(this)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_PERMISSION
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
                    initGoogleMapProperties()
                } else {
                    showToast("Permission denied")
                }
                return
            }
        }
    }

    private fun setObservers() {
        viewModel.pointsLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->
            when (result) {
                is MainPageViewModel.PointOfInterestState.Result -> {
                    hashMapPoints = hashMapOf()
                    result.points.forEach {
                        hashMapPoints[it.pageId] = it
                        val drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_point_black)
                        val bitmap = drawable?.toBitmap()
                        val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
                        val mark = MarkerOptions()
                            .position(LatLng(it.lat, it.lon))
                            .title(it.title)
                            .snippet(it.pageId)
                            .draggable(false)
                            .icon(icon)
                        googleMap.addMarker(mark)
                    }
                }
                is MainPageViewModel.PointOfInterestState.Error -> {
                    showToast(result.error)
                }
            }
        })
        viewModel.directionsLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->
            when (result) {
                is MainPageViewModel.DestinationPathWithDescription.Result -> {
                    currentRoute = result.points

                    val bottomSheetDetailsDialog =
                        BottomSheetDialogDetails.newInstance(result.geoSearchModel, result.points)
                    bottomSheetDetailsDialog.show(requireActivity().supportFragmentManager, BOTTOM_SHEET_POINT_DETAILS)

                    bottomSheetDetailsDialog.listener = {
                        val decodedPath = PolyUtil.decode(currentRoute!!.routes[0].overviewPolyline.points)
                        polyline = googleMap.addPolyline(PolylineOptions().addAll(decodedPath))
                        if (!result.points.routes.isNullOrEmpty()) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(
                                LatLng(result.points.routes[0].bounds?.southWest!!.lat, result.points.routes[0].bounds?.southWest!!.lng),
                                LatLng(result.points.routes[0].bounds?.northEast!!.lat, result.points.routes[0].bounds?.northEast!!.lng)
                            ), 17))
                        }
                        bottomSheetDetailsDialog.dismiss()
                    }
                }
                is MainPageViewModel.DestinationPathWithDescription.Error -> {
                    showToast(result.error)
                }
            }
        })
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (polyline != null) polyline?.remove()
        if (marker != null && hashMapPoints[marker.snippet] != null) {
            val destination = LatLng(marker.position.latitude, marker.position.longitude)
            val cameraPosition = CameraPosition.Builder()
                .target(destination)
                .zoom(17f)
                .build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            viewModel.getDirectionWithDesctination(origin!!, destination, hashMapPoints[marker.snippet])
        }
        return true
    }

}