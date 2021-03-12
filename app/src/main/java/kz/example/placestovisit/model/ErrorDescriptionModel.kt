package kz.example.placestovisit.model

import com.google.gson.annotations.SerializedName

class ErrorDescriptionModel(
    @SerializedName("code") val code: String?,
    @SerializedName("info") val info: String?,
    @SerializedName("*") val codeInfo: String?
)