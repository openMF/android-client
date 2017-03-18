package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperNote;
import com.mifos.objects.noncore.Note;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * This DataManager is for Managing Notes API, In which Request is going to Server
 * and In Response, We are getting Notes API Observable Response using Retrofit2
 * Created by rahul on 4/3/17.
 */
@Singleton
public class DataManagerNote {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperNote mDatabaseHelperNote;

    @Inject
    public DataManagerNote(BaseApiManager baseApiManager,
                           DatabaseHelperNote databaseHelperNote) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperNote = databaseHelperNote;
    }


    /**
     * This Method Request the REST API of Note and In response give the List of Notes
     */
    public Observable<List<Note>> getNotes(String entityType, int entityId) {
        return mBaseApiManager.getNoteApi().getNotes(entityType, entityId);
    }
}
