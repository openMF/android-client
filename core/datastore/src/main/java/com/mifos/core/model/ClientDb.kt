package com.mifos.core.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Created by Aditya Gupta on 21/02/24.
 */

class ClientDb : RealmObject {

    @PrimaryKey
    var _id: Int = 0

    var groupId: Int? = 0

    var accountNo: String? = null

    var clientId: Int? = null

    var status: StatusDb? = null

    var sync: Boolean = false

    var active: Boolean = false

    var clientDate: ClientDateDb? = null

    @Ignore
    var activationDate: RealmList<Int?> = realmListOf()

    @Ignore
    var dobDate: RealmList<Int?> = realmListOf()

    @Ignore
    var groups: RealmList<GroupDb?> = realmListOf()

    var mobileNo: String? = null

    var firstname: String? = null

    var middlename: String? = null

    var lastname: String? = null

    var displayName: String? = null

    var officeId: Int = 0

    var officeName: String? = null

    var staffId: Int = 0

    var staffName: String? = null

    @Ignore
    var timeline: TimelineDb? = null

    var fullname: String? = null

    var imageId: Int = 0

    var imagePresent: Boolean = false

    var externalId: String? = null

}