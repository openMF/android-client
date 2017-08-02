package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.response.SaveResponse;
import com.mifos.services.data.CenterPayload;
import com.mifos.services.data.CenterPayload_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * Created by Rajan Maurya on 28/6/16.
 */
@Singleton
public class DatabaseHelperCenter {

    @Inject
    public DatabaseHelperCenter() {
    }


    /**
     * Saving Centers in Database using DBFlow.
     * save() method save the value reference to primary key if its exist the update if not the
     * insert.
     *
     * @param centerPage
     * @return null
     */
    @Nullable
    public Observable<Void> saveAllCenters(final Page<Center> centerPage) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Center center : centerPage.getPageItems()) {
                    center.save();
                }
            }
        });
        return null;
    }

    /**
     * Reading All Centers from table of Center and return the CenterList
     *
     * @return List Of Centers
     */
    //TODO Implement Observable Transaction to load Center List
    public Observable<Page<Center>> readAllCenters() {

        return Observable.create(new Observable.OnSubscribe<Page<Center>>() {
            @Override
            public void call(Subscriber<? super Page<Center>> subscriber) {

                Page<Center> centerPage = new Page<>();
                centerPage.setPageItems(SQLite.select()
                        .from(Center.class)
                        .queryList());
                subscriber.onNext(centerPage);
                subscriber.onCompleted();
            }
        });

    }

    public Observable<SaveResponse> saveCenterPayload(final CenterPayload centerPayload) {
        return Observable.defer(new Func0<Observable<SaveResponse>>() {
            @Override
            public Observable<SaveResponse> call() {
                centerPayload.save();
                return Observable.just(new SaveResponse());
            }
        });
    }

    public Observable<List<CenterPayload>> readAllCenterPayload() {
        return Observable.defer(new Func0<Observable<List<CenterPayload>>>() {
            @Override
            public Observable<List<CenterPayload>> call() {
                List<CenterPayload> centerPayloads = SQLite.select()
                        .from(CenterPayload.class)
                        .queryList();
                return Observable.just(centerPayloads);
            }
        });
    }

    /**
     * This Method for deleting the center payload from the Database according to Id and
     * again fetch the center List from the Database CenterPayload_Table
     * @param id is Id of the Center Payload in which reference center was saved into Database
     * @return List<CenterPayload></>
     */
    public Observable<List<CenterPayload>> deleteAndUpdateCenterPayloads(final int id) {
        return Observable.defer(new Func0<Observable<List<CenterPayload>>>() {
            @Override
            public Observable<List<CenterPayload>> call() {
                Delete.table(CenterPayload.class, CenterPayload_Table.id.eq(id));

                List<CenterPayload> groupPayloads = SQLite.select()
                        .from(CenterPayload.class)
                        .queryList();
                return Observable.just(groupPayloads);
            }
        });
    }

    public Observable<CenterPayload> updateDatabaseCenterPayload(
            final CenterPayload centerPayload) {
        return Observable.defer(new Func0<Observable<CenterPayload>>() {
            @Override
            public Observable<CenterPayload> call() {
                centerPayload.update();
                return Observable.just(centerPayload);
            }
        });
    }
}
