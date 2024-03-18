package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 06-07-2017.
 */
@Parcelize
data class ClientCollectionSheet(
    var clientId: Int = 0,

    var clientName: String? = null,

    var loans: ArrayList<LoanCollectionSheet>? = null,

    var attendanceType: AttendanceTypeOption? = null,

    var savings: ArrayList<SavingsCollectionSheet> = ArrayList()
) : Parcelable