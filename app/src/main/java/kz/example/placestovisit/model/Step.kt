package kz.example.placestovisit.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Step (
    var distance: Distance?,
    var duration: Duration?,

    @SerializedName("start_location")
    var startLocation: GeoPoint?,
    @SerializedName("end_location")
    var endLocation: GeoPoint?,
    @SerializedName("html_instructions")
    var htmlInstructions: String?,
    @SerializedName("travel_mode")
    var travelMode: String? = null
) : Serializable