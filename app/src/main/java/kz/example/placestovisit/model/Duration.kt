package kz.example.placestovisit.model

import java.io.Serializable

data class Duration (
    var text: String?,
    var value: Double
) : Serializable