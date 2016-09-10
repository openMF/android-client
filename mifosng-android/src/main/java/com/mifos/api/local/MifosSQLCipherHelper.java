package com.mifos.api.local;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.dbflow.android.sqlcipher.SQLCipherOpenHelper;

public class MifosSQLCipherHelper extends SQLCipherOpenHelper {

    public MifosSQLCipherHelper(DatabaseDefinition databaseDefinition,
                                DatabaseHelperListener listener) {
        super(databaseDefinition, listener);
    }

    @Override
    protected String getCipherSecret() {
        return "dbflow-rules";
    }
}