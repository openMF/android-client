package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * This DatabaseHelper Managing all Database logic and staff (Saving, Update, Delete).
 * Whenever DataManager send response to save or request to read from Database then this class
 * save the response or read the all values from database and return as accordingly.
 * Created by Rajan Maurya on 28/06/16.
 */
@Singleton
public class DatabaseHelperGroups {

    @Inject
    public DatabaseHelperGroups() {
    }


    /**
     * Saving Groups in Database using DBFlow.
     * save() method save the value reference to primary key if its exist the update if not the
     * insert.
     *
     * @param groupPage
     * @return null
     */
    @Nullable
    public Observable<Void> saveGroups(final Page<Group> groupPage) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Group group : groupPage.getPageItems()) {
                    group.save();
                }
            }
        });
        return null;
    }


    /**
     * Reading All groups from table of Group and return the GroupList
     *
     * @return List Of Groups
     */
    //TODO Implement Observable Transaction to load Client List
    public Observable<Page<Group>> readAllGroups() {
        return Observable.create(new Observable.OnSubscribe<Page<Group>>() {
            @Override
            public void call(Subscriber<? super Page<Group>> subscriber) {

                Page<Group> groupPage = new Page<>();
                groupPage.setPageItems(SQLite.select()
                        .from(Group.class)
                        .queryList());
                subscriber.onNext(groupPage);
                subscriber.onCompleted();
            }
        });

    }

}
