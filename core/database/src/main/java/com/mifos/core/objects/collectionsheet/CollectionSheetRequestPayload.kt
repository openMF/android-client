package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */
@Parcelize
data class CollectionSheetRequestPayload(
    var calendarId: Int? = null,

    var dateFormat: String = "dd MMMM yyyy",

    var locale: String = "en",

    var transactionDate: String? = null
) : Parcelable