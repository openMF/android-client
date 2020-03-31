package com.mifos.api.local;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Rajan Maurya on 23/06/16.
 */
@Database(name = MifosDatabase.NAME, version = MifosDatabase.VERSION, foreignKeysSupported = true)
public class MifosDatabase {

    // database name will be Mifos.db
    public static final String NAME = "Mifos";

    //Always Increase the Version Number
    public static final int VERSION = 2;
}
