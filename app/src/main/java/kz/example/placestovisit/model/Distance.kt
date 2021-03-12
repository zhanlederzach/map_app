package kz.example.placestovisit.model

import java.io.Serializable

data class Distance(
    var text: String?,
    var value: Double
) : Serializable