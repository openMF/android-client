package com.mifos.core.data.model.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 18/09/16.
 */
@Parcelize
data class GroupDate(

    var groupId: Long = 0,

    var chargeId: Long = 0,

    var day: Int = 0,

    var month: Int = 0,

    var year: Int = 0
) : Parcelable