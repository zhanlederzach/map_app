package kz.example.placestovisit.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Route (
    var bounds: Bounds?,
    var legs: List<Legs>?,
    var summary: String?,
    var copyrights: String?,
    @SerializedName("overview_polyline") val overviewPolyline: PointRoute
) : Serializable