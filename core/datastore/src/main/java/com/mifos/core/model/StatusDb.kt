package com.mifos.core.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class StatusDb : RealmObject {

    var _id: Int = 0

    var code: String? = null

    var value: String? = null

}