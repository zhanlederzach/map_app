package kz.example.placestovisit.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Utils class for easy managing of Permissions
 */
object PermissionsUtils {

    /**
     * Determine whether you have been granted a particular permission.
     * @param context application context
     * @param permissions names of the permissions being checked
     * @return true if all permissions is granted
     */
    fun isGranted(context: Context, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

}