package com.mifos.core.data.model.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 04/07/16.
 */
@Parcelize
data class ClientDate(

    var clientId: Long = 0,

    var chargeId: Long = 0,

    var day: Int = 0,

    var month: Int = 0,

    var year: Int = 0
) : Parcelable