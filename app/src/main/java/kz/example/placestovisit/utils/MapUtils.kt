package kz.example.placestovisit.utils

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

object MapUtils {

    fun meterDistanceBetweenPoints(
        latA: Double,
        lngA: Double,
        latB: Double,
        lngB: Double
    ): Double {
        val pk = (180f / Math.PI).toFloat()
        val a1 = latA / pk
        val a2 = lngA / pk
        val b1 = latB / pk
        val b2 = lngB / pk
        val t1 =
            cos(x = a1) * cos(x = a2) * cos(x = b1) * cos(x = b2)
        val t2 = cos(x = a1) * sin(x = a2) * cos(x = b1) * sin(x = b2)
        val t3 = sin(x = a1) * sin(b1)
        val tt = acos(x = t1 + t2 + t3)
        return 6366000 * tt
    }
}