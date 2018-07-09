package nl.biancavanschaik.android.museumkaart.data

import android.content.Context
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class CameraPreferences(
        private val context: Context
) {
    fun save(camera: CameraPosition) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
                .edit()
                .putFloat(PREF_CAMERA_BEARING, camera.bearing)
                .putFloat(PREF_CAMERA_LATITUDE, camera.target.latitude.toFloat())
                .putFloat(PREF_CAMERA_LONGITUDE, camera.target.longitude.toFloat())
                .putFloat(PREF_CAMERA_TILT, camera.tilt)
                .putFloat(PREF_CAMERA_ZOOM, camera.zoom)
                .apply()
    }

    fun load() : CameraPosition? {
        val prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(PREF_CAMERA_LATITUDE)) {
            return null
        }
        return CameraPosition.Builder()
                .bearing(prefs.getFloat(PREF_CAMERA_BEARING, 0f))
                .target(LatLng(
                        prefs.getFloat(PREF_CAMERA_LATITUDE, 0f).toDouble(),
                        prefs.getFloat(PREF_CAMERA_LONGITUDE, 0f).toDouble()
                ))
                .tilt(prefs.getFloat(PREF_CAMERA_TILT, 0f))
                .zoom(prefs.getFloat(PREF_CAMERA_ZOOM, 0f))
                .build()
    }
}

const val NAME = "camera.prefs"
const val PREF_CAMERA_BEARING = "camera.bearing"
const val PREF_CAMERA_LATITUDE = "camera.latitude"
const val PREF_CAMERA_LONGITUDE = "camera.longitude"
const val PREF_CAMERA_TILT = "camera.tilt"
const val PREF_CAMERA_ZOOM = "camera.zoom"
