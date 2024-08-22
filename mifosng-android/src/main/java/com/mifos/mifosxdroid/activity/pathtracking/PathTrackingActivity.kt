/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.activity.pathtracking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import com.mifos.core.objects.user.UserLatLng
import com.mifos.feature.path_tracking.PathTrackingScreen
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author fomenkoo
 */
@AndroidEntryPoint
class PathTrackingActivity : MifosBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PathTrackingScreen(
                onBackPressed = { finish() },
//                onPathTrackingClick = { userLatLngs ->
//                    onUserLocationClick(userLatLngs)
//                }
            )
        }
    }

    private fun onUserLocationClick(userLatLngs: List<UserLatLng>) {
        val uri = if (userLatLngs.isNotEmpty()) {
            val originLatLng = userLatLngs[0]
            val destinationLatLng = userLatLngs[userLatLngs.size - 1]
            "http://maps.google.com/maps?f=d&hl=en&saddr=${originLatLng.lat},${originLatLng.lng}&daddr=${destinationLatLng.lat},${destinationLatLng.lng}"
        } else {
            // Handle the case when userLatLngs is empty
            ""
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setClassName(
            "com.google.android.apps.maps", "com.google.android.maps.MapsActivity"
        )
        startActivity(Intent.createChooser(intent, getString(R.string.start_tracking)))
    }
}