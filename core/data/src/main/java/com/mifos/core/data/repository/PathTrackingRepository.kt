package com.mifos.core.data.repository

import com.mifos.core.objects.user.UserLocation

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface PathTrackingRepository {

    suspend fun getUserPathTracking(userId: Int): List<UserLocation>

}