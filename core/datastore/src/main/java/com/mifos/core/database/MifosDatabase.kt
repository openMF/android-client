package com.mifos.core.database

import com.raizlabs.android.dbflow.annotation.Database

/**
 * Created by Rajan Maurya on 23/06/16.
 */
@Database(name = MifosDatabase.NAME, version = MifosDatabase.VERSION, foreignKeysSupported = true)
object MifosDatabase {
    // database name will be Mifos.db
    const val NAME = "Mifos"

    //Always Increase the Version Number
    const val VERSION = 2
}