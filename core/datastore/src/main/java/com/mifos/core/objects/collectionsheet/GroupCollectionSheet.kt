package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */

@Parcelize
data class GroupCollectionSheet(
    var clients: MutableList<ClientCollectionSheet> = ArrayList(),

    var groupId: Int = 0,

    var groupName: String? = null,

    var levelId: Int = 0,

    var levelName: String? = null,

    var staffId: Int = 0,

    var staffName: String? = null
) : Parcelable