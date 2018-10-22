package nl.biancavanschaik.android.museumkaart.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.requestPermission(permission: String, @IntRange(from = 0) requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
}