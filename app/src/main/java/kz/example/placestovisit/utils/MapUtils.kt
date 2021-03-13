package kz.example.placestovisit.utils

import android.app.Activity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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

    fun moveCamera(googleMap: GoogleMap, point: LatLng?) {
        if (point == null) return
        val cameraPosition = CameraPosition.Builder()
            .target(point)
            .zoom(17f)
            .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun getIconByDrawableId(activity: Activity, drawableId: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(activity, drawableId)
        val bitmap = drawable?.toBitmap()
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}