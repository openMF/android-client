package com.mifos.core.model

import io.realm.kotlin.types.RealmObject

/**
 * Created by Aditya Gupta on 21/02/24.
 */

class ClientDateDb : RealmObject {

    var clientId: Long = 0

    var chargeId: Long = 0

    var day: Int = 0

    var month: Int = 0

    var year: Int = 0

}