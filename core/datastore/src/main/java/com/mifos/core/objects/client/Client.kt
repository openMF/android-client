/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.objects.Timeline
import com.mifos.core.objects.group.Group
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 08/02/14.
 */
@Parcelize
@Table(database = MifosDatabase::class, useBooleanGetterSetters = false)
@ModelContainer
data class Client(
    @PrimaryKey
    var id: Int = 0,

    @Column
    @Transient
    var groupId: Int? = 0,

    @Column
    var accountNo: String? = null,

    var clientId: Int? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var status: Status? = null,

    @Column
    @Transient
    var sync: Boolean = false,

    @Column
    var active: Boolean = false,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var clientDate: ClientDate? = null,

    var activationDate: List<Int?> = ArrayList(),

    var dobDate: List<Int?> = ArrayList(),

    var groups: List<Group?> = ArrayList(),

    var mobileNo: String? = null,

    @Column
    var firstname: String? = null,

    @Column
    var middlename: String? = null,

    @Column
    var lastname: String? = null,

    @Column
    var displayName: String? = null,

    @Column
    var officeId: Int = 0,

    @Column
    var officeName: String? = null,

    @Column
    var staffId: Int = 0,

    @Column
    var staffName: String? = null,

    var timeline: Timeline? = null,

    @Column
    var fullname: String? = null,

    @Column
    var imageId: Int = 0,

    @Column
    var imagePresent: Boolean = false,

    @Column
    var externalId: String? = null
) : MifosBaseModel(), Parcelable {

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