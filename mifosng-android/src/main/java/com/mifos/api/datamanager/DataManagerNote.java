package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperNote;
import com.mifos.api.mappers.note.NoteMapper;
import com.mifos.objects.noncore.Note;

import org.apache.fineract.client.services.NotesApi;

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
    public final org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager;

    @Inject
    public DataManagerNote(BaseApiManager baseApiManager,
                           DatabaseHelperNote databaseHelperNote,
                           org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperNote = databaseHelperNote;
        this.sdkBaseApiManager = sdkBaseApiManager;
    }

    private NotesApi getNotesApi() {
        return sdkBaseApiManager.getNoteApi();
    }

    /**
     * This Method Request the REST API of Note and In response give the List of Notes
     */
    public Observable<List<Note>> getNotes(String entityType, int entityId) {
        return getNotesApi().retrieveNotesByResource(entityType, (long) entityId)
        .map(NoteMapper.INSTANCE::mapFromEntityList);
    }
}
