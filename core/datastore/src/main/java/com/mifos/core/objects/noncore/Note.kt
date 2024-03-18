package com.mifos.core.objects.noncore

import android.os.Parcelable
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by rahul on 4/3/17.
 */
@Parcelize
data class Note(
    var id: Int? = null,

    var clientId: Int? = null,

    var noteContent: String? = null,

    var createdById: Int? = null,

    var createdByUsername: String? = null,

    var createdOn: Long = 0,

    var updatedById: Int? = null,

    var updatedByUsername: String? = null,

    var updatedOn: Long = 0
) : MifosBaseModel(), Parcelable