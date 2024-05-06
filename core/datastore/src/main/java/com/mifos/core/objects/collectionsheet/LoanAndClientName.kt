package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 17-07-2017.
 */
@Parcelize
class LoanAndClientName(
    val loan: LoanCollectionSheet?,
    val clientName: String?,
    val id: Int
) : Parcelable