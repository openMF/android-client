/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.group

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName
import com.mifos.objects.Timeline
import com.mifos.objects.client.Client
import com.mifos.objects.client.Status
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 29/06/14.
 */
@Parcelize
data class GroupWithAssociations (
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
) :Parcelable