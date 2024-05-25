package com.mifos.core.objects.group

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/02/17.
 */
@Parcelize
data class CenterInfo(
    var activeClients: Int? = null,

    var activeLoans: Int? = null,

    var activeClientLoans: Int? = null,

    var activeGroupLoans: Int? = null,

    var activeBorrowers: Int? = null,

    var activeClientBorrowers: Int? = null,

    var activeGroupBorrowers: Int? = null,

    var overdueLoans: Int? = null,

    var overdueClientLoans: Int? = null,

    var overdueGroupLoans: Int? = null
) : Parcelable