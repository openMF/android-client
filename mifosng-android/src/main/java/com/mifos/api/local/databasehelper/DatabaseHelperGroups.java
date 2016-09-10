package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupPayload;
import com.mifos.objects.group.GroupPayload_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func0;

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
     * This Method Saving the Single Group in the Database
     *
     * @param group
     * @return Observable.just(Group)
     */
    public Observable<Group> saveGroup(final Group group) {
        return Observable.defer(new Func0<Observable<Group>>() {
            @Override
            public Observable<Group> call() {
                group.save();
                return Observable.just(group);
            }
        });
    }

    /**
     * Reading All groups from Database table of Group and return the GroupList
     *
     * @return List Of Groups
     */
    public Observable<Page<Group>> readAllGroups() {
        return Observable.defer(new Func0<Observable<Page<Group>>>() {
            @Override
            public Observable<Page<Group>> call() {
                Page<Group> groupPage = new Page<>();
                groupPage.setPageItems(SQLite.select()
                        .from(Group.class)
                        .queryList());
                return Observable.just(groupPage);
            }
        });
    }


    public Observable<Group> saveGroupPayload(final GroupPayload groupPayload) {
        return Observable.defer(new Func0<Observable<Group>>() {
            @Override
            public Observable<Group> call() {
                groupPayload.save();
                return Observable.just(new Group());
            }
        });
    }


    public Observable<List<GroupPayload>> realAllGroupPayload() {
        return Observable.defer(new Func0<Observable<List<GroupPayload>>>() {
            @Override
            public Observable<List<GroupPayload>> call() {

                List<GroupPayload> groupPayloads = SQLite.select()
                        .from(GroupPayload.class)
                        .queryList();

                return Observable.just(groupPayloads);
            }
        });
    }

    /**
     * This Method for deleting the group payload from the Database according to Id and
     * again fetch the group List from the Database GroupPayload_Table
     * @param id is Id of the Client Payload in which reference client was saved into Database
     * @return List<ClientPayload></>
     */
    public Observable<List<GroupPayload>> deleteAndUpdateGroupPayloads(final int id) {
        return Observable.defer(new Func0<Observable<List<GroupPayload>>>() {
            @Override
            public Observable<List<GroupPayload>> call() {

                Delete.table(GroupPayload.class, GroupPayload_Table.id.eq(id));

                List<GroupPayload> groupPayloads = SQLite.select()
                        .from(GroupPayload.class)
                        .queryList();

                return Observable.just(groupPayloads);
            }
        });
    }


    public Observable<GroupPayload> updateDatabaseGroupPayload(final GroupPayload groupPayload) {
        return Observable.defer(new Func0<Observable<GroupPayload>>() {
            @Override
            public Observable<GroupPayload> call() {
                groupPayload.update();
                return Observable.just(groupPayload);
            }
        });
    }

}
