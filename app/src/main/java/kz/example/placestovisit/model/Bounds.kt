package kz.example.placestovisit.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Bounds (
    @SerializedName("northeast")
    var northEast: GeoPoint?,
    @SerializedName("southwest")
    var southWest: GeoPoint?
) : Serializable