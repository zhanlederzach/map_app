package kz.example.placestovisit.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Legs (
    @SerializedName("start_address")
    var startAddress: String?,
    @SerializedName("end_address")
    var endAddress: String?,
    @SerializedName("start_location")
    var startLocation: GeoPoint?,
    @SerializedName("end_location")
    var endLocation: GeoPoint?,

    var steps: List<Step>?,
    var distance: Distance?,
    var duration: Duration?
) : Serializable