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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import com.main.ui_core.base.BaseFragment
import com.main.ui_core.extensions.showToast
import kz.example.placestovisit.R
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes
import kz.example.placestovisit.ui.point_details_bottom_sheet.BottomSheetDialogDetails
import kz.example.placestovisit.utils.MapUtils
import kz.example.placestovisit.widgets.ToolbarView
import org.w3c.dom.Text
import javax.inject.Inject

class MainPageFragment : BaseFragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

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
    private lateinit var toolbarView: ToolbarView
    private lateinit var bottomSheetRoute: LinearLayout
    private var tvTextViewBottom: TextView? = null
    private var tvStepsBottom: TextView? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var googleMap: GoogleMap

    private var currLocationMarker: Marker? = null
    private var origin: LatLng = LatLng(-123.4, 120.0)
    private var currentRouteToDestination: Routes? = null
    private var polyline: Polyline? = null
    private var destination: LatLng? = null
    private var hashMapPoints: HashMap<String, GeoSearchModel> = hashMapOf()

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                var distance = 20.0
                distance = MapUtils.meterDistanceBetweenPoints(
                    location.latitude,
                    location.longitude,
                    origin.latitude,
                    origin.longitude
                )
                if (distance > DISTANCE_LIMIT_FOR_UPDATE) {
                    origin = LatLng(location.latitude, location.longitude)
                    currLocationMarker?.remove()
                    currLocationMarker = googleMap.addMarker(
                        MarkerOptions().apply {
                            position(origin)
                            title("Current Position")
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                        }
                    )
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 17.0F))
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

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    initGoogleMapProperties()
                } else {
                    showToast("Permission denied")
                }
                return
            }
        }
    }

    private fun bindViews(view: View, savedInstanceState: Bundle?) = with(view) {
        mapView = findViewById(R.id.mapView)
        toolbarView = findViewById(R.id.toolbarView)
        bottomSheetRoute = findViewById(R.id.bottomSheetRoute)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetRoute)

        tvTextViewBottom = bottomSheetRoute.findViewById(R.id.titleTextView)
        tvStepsBottom = bottomSheetRoute.findViewById(R.id.tvSteps)

        bottomSheetBehavior.apply {
            peekHeight = 0
            BottomSheetBehavior.STATE_COLLAPSED
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) { }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN ||
                        newState == BottomSheetBehavior.STATE_COLLAPSED ||
                        newState == BottomSheetBehavior.STATE_HALF_EXPANDED
                    ) {
                        toolbarView.visibility = View.GONE
                    }
                }
            })
        }

        mapView.apply {
            onCreate(savedInstanceState)
            onResume()
        }
    }

    private fun initGoogleMap() {
        showLoading()
        mapView.getMapAsync { map ->
            googleMap = map
            hideLoading()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
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
        googleMap.apply {
            isMyLocationEnabled = true
            uiSettings.isZoomControlsEnabled = true
        }
        googleMap.clear()
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener(this)
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

    private fun setObservers() {
        viewModel.pointsLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->
            when (result) {
                is MainPageViewModel.PointOfInterestState.Result -> {
                    hashMapPoints = hashMapOf()
                    result.points.forEach {
                        hashMapPoints[it.pageId] = it
                        val mark = MarkerOptions()
                            .position(LatLng(it.lat, it.lon))
                            .title(it.title)
                            .snippet(it.pageId)
                            .draggable(false)
                            .icon(MapUtils.getIconByDrawableId(requireActivity(), R.drawable.ic_point_black))
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
                    currentRouteToDestination = result.points
                    val bottomSheetDetailsDialog =
                        BottomSheetDialogDetails.newInstance(
                            result.geoSearchDescription,
                            result.points,
                            result.geoSearchModel
                        )

                    bottomSheetDetailsDialog.drawRouteListener = {
                        currentRouteToDestination.let {
                            val decodedPath = PolyUtil.decode(it?.routes?.get(0)?.overviewPolyline?.points)
                            polyline = googleMap.addPolyline(PolylineOptions().addAll(decodedPath))
                        }

                        if (!result.points.routes.isNullOrEmpty()) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(
                                LatLng(result.points.routes[0].bounds?.southWest!!.lat, result.points.routes[0].bounds?.southWest!!.lng),
                                LatLng(result.points.routes[0].bounds?.northEast!!.lat, result.points.routes[0].bounds?.northEast!!.lng)
                            ), 17))
                        }
                        bottomSheetDetailsDialog.dismiss()

                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        tvTextViewBottom?.text = result.geoSearchModel.title
                        if (!result.points.routes.isNullOrEmpty() && !result.points.routes.first().legs?.isNullOrEmpty()!!) {
                            tvStepsBottom?.text = result.points.routes.first().legs?.first()?.duration?.text
                        }
                        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                                if (slideOffset == 0f) removePreviousPath()
                            }

                            override fun onStateChanged(bottomSheet: View, newState: Int) { }
                        })

                        toolbarView.visibility = View.VISIBLE

                        toolbarView.getBackArrow().setOnClickListener {
                            bottomSheetDetailsDialog.show(requireActivity().supportFragmentManager, BOTTOM_SHEET_POINT_DETAILS)
                            MapUtils.moveCamera(googleMap, destination)
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            toolbarView.visibility = View.GONE
                            removePreviousPath()
                        }
                    }

                    bottomSheetDetailsDialog.show(requireActivity().supportFragmentManager, BOTTOM_SHEET_POINT_DETAILS)
                }
                is MainPageViewModel.DestinationPathWithDescription.Error -> {
                    showToast(result.error)
                }
            }
        })
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        removePreviousPath()
        if (marker != null && hashMapPoints[marker.snippet] != null) {
            destination = LatLng(marker.position.latitude, marker.position.longitude)
            MapUtils.moveCamera(googleMap, destination)
            viewModel.getDirectionWithDestination(origin, destination, hashMapPoints[marker.snippet])
        }
        return true
    }

    override fun onMapClick(point: LatLng?) {
        toolbarView.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        removePreviousPath()
    }

    private fun removePreviousPath() {
        polyline?.remove()
    }

}