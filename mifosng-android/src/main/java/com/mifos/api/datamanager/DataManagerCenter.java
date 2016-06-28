package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperCenter;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.utils.PrefManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Rajan Maurya on 28/6/16.
 */
@Singleton
public class DataManagerCenter {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperCenter mDatabaseHelperCenter;

    @Inject
    public DataManagerCenter(BaseApiManager baseApiManager,
                             DatabaseHelperCenter databaseHelperCenter) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperCenter = databaseHelperCenter;
    }


    public Observable<Page<Center>> getCenters(boolean paged, int offset, int limit) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getCenterApi().getCenters(paged, offset, limit)
                        .concatMap(new Func1<Page<Center>, Observable<? extends Page<Center>>>() {
                            @Override
                            public Observable<? extends Page<Center>> call(Page<Center>
                                                                                   centerPage) {
                                //Saving Clients in Database
                                mDatabaseHelperCenter.saveAllCenters(centerPage);
                                return Observable.just(centerPage);
                            }
                        });
            case 1:
                /**
                 * Return All Clients List from DatabaseHelperClient only one time.
                 * If offset is zero this means this is first request and
                 * return all clients from DatabaseHelperClient
                 */
                if (offset == 0)
                    return mDatabaseHelperCenter.readAllCenters();

            default:
                return Observable.just(new Page<Center>());
        }
    }

}
