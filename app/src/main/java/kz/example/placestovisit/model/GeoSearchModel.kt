package kz.example.placestovisit.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GeoSearchModel(
    @SerializedName("pageid") val pageId: String,
    @SerializedName("ns") val ns: Int,
    @SerializedName("title") val title: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("dist") val dist: Double,
    @SerializedName("primary") val primary: String
) : Serializable