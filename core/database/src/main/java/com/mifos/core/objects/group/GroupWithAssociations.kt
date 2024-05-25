/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.group

import android.os.Parcelable
import com.mifos.core.objects.Timeline
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Status
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 29/06/14.
 */
@Parcelize
data class GroupWithAssociations(
    var id: Int? = null,

    var accountNo: String? = null,

    var name: String? = null,

    var status: Status? = null,

    var active: Boolean? = null,

    var activationDate: List<Int?> = ArrayList(),

    var officeId: Int? = null,

    var officeName: String? = null,

    var staffId: Int? = null,

    var staffName: String? = null,

    var hierarchy: String? = null,

    var groupLevel: Int? = null,

    var clientMembers: List<Client> = ArrayList(),

    var timeline: Timeline? = null
) : Parcelable