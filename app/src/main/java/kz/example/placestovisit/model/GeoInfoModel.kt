package kz.example.placestovisit.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GeoInfoModel(
    @SerializedName("pageid") val pageId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("contentmodel") val contentModel: String,
    @SerializedName("pagelanguage") val pageLanguage: String
) : Serializable