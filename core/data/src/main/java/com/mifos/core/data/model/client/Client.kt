/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.model.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 08/02/14.
 */
@Parcelize
data class Client(

    var id: Int = 0,

    var groupId: Int? = 0,

    var accountNo: String? = null,

    var clientId: Int? = null,

    var status: Status? = null,

    var sync: Boolean = false,

    var active: Boolean = false,

    var clientDate: ClientDate? = null,

    var activationDate: List<Int?> = ArrayList(),

    var dobDate: List<Int?> = ArrayList(),

    var groups: List<Group?> = ArrayList(),

    var mobileNo: String? = null,

    var firstname: String? = null,

    var middlename: String? = null,

    var lastname: String? = null,

    var displayName: String? = null,

    var officeId: Int = 0,

    var officeName: String? = null,

    var staffId: Int = 0,

    var staffName: String? = null,

    var timeline: Timeline? = null,

    var fullname: String? = null,

    var imageId: Int = 0,

    var imagePresent: Boolean = false,

    var externalId: String? = null
) : Parcelable {

    /**
     * This method is returning the comma separated names of all the client's group
     * in the form of a string.
     */
    val groupNames: String
        get() {
            var groupNames = ""
            if (groups.isEmpty()) return ""
            for (group in groups) {
                groupNames += group!!.name + ", "
            }
            return groupNames.substring(0, groupNames.length - 2)
        }
}