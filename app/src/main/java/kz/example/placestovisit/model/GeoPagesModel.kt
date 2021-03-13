package kz.example.placestovisit.model

import java.io.Serializable

data class GeoPagesModel(
    val pages: HashMap<String, GeoInfoModel>
) : Serializable