package com.mifos.core.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey

class GroupDb : RealmObject {

    @PrimaryKey
    var _id: Int? = null

    var accountNo: String? = null

    var sync: Boolean = false

    var name: String? = null

    var status: StatusDb? = null

    var active: Boolean? = null

    var groupDate: GroupDateDb? = null

    var activationDate: RealmList<Int> = realmListOf()

    var officeId: Int? = null

    var officeName: String? = null

    var centerId: Int? = 0

    var centerName: String? = null

    var staffId: Int? = null

    var staffName: String? = null

    var hierarchy: String? = null

    var groupLevel: Int = 0

    @Ignore
    var timeline: TimelineDb? = null

    var externalId: String? = null

}