package com.mifos.core.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Created by Aditya Gupta on 21/02/24.
 */

class GroupDateDb : RealmObject {

    @PrimaryKey
    var groupId: Long = 0

    var chargeId: Long = 0

    var day: Int = 0

    var month: Int = 0

    var year: Int = 0

}