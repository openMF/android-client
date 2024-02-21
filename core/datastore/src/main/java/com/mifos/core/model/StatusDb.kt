package com.mifos.core.model

import io.realm.kotlin.types.RealmObject

/**
 * Created by Aditya Gupta on 21/02/24.
 */

class StatusDb : RealmObject {

    var _id: Int = 0

    var code: String? = null

    var value: String? = null

}