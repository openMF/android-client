package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperDataTable;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * This DataManager is for Managing DataTable API, In which Request is going to Server
 * and In Response, We are getting DataTable API Observable Response using Retrofit2
 *
 * Created by Rajan Maurya on 3/7/16.
 */
@Singleton
public class DataManagerDataTable {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperDataTable mDatabaseHelperDataTable;

    @Inject
    public DataManagerDataTable(BaseApiManager baseApiManager,
                                DatabaseHelperDataTable databaseHelperDataTable) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperDataTable = databaseHelperDataTable;
    }


    /**
     * This Method Request the REST API of Datatable and In response give the List of DataTable
     * Type of DataTable is
     * 1. m_client
     * 2. m_savings_account
     * 3. m_loan
     * @param tableName DataTable Name
     * @return List<DataTable>
     */
    public Observable<List<DataTable>> getDataTable(String tableName) {
        return mBaseApiManager.getDataTableApi().getTableOf(tableName);
    }


}
