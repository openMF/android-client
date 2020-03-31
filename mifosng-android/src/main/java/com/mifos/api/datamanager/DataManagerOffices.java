package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperOffices;
import com.mifos.objects.organisation.Office;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * This DataManager is for Managing Offices API, In which Request is going to Server
 * and In Response, We are getting Offices API Observable Response using Retrofit2.
 * DataManagerOffices saving response in Database and response to Presenter as accordingly.
 *
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
public class DataManagerOffices {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperOffices mDatabaseHelperOffices;

    @Inject
    public DataManagerOffices(BaseApiManager baseApiManager,
                              DatabaseHelperOffices databaseHelperOffices) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperOffices = databaseHelperOffices;
    }


    public Observable<List<Office>> getOffices() {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getOfficeApi().getAllOffices()
                        .concatMap(new Func1<List<Office>, Observable<? extends List<Office>>>() {
                            @Override
                            public Observable<? extends List<Office>> call(List<Office> offices) {
                                mDatabaseHelperOffices.saveAllOffices(offices);
                                return Observable.just(offices);
                            }
                        });
            case 1:
                /**
                 * return all List of Offices from DatabaseHelperOffices
                 */
                return mDatabaseHelperOffices.readAllOffices();

            default:
                List<Office> offices = new ArrayList<>();
                return Observable.just(offices);
        }
    }
}
