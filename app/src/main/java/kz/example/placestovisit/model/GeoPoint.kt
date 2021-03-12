package kz.example.placestovisit.model

import java.io.Serializable

data class GeoPoint(
    val lat: Double,
    val lng: Double
) : Serializable