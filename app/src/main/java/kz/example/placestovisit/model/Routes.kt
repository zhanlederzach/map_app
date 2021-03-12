package kz.example.placestovisit.model

import java.io.Serializable

data class Routes (
    var status: String,
    var routes: List<Route>
) : Serializable