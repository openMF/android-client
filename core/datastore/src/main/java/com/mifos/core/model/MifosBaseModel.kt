package com.mifos.core.model

import com.google.gson.Gson
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by Rajan Maurya on 23/06/16.
 */
open class MifosBaseModel : BaseModel() {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}