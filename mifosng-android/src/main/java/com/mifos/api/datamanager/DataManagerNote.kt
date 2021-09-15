package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import javax.inject.Singleton
import javax.inject.Inject
import com.mifos.api.local.databasehelper.DatabaseHelperNote
import com.mifos.api.mappers.note.NoteMapper
import com.mifos.objects.noncore.Note
import org.apache.fineract.client.services.NotesApi
import rx.Observable

/**
 * This DataManager is for Managing Notes API, In which Request is going to Server
 * and In Response, We are getting Notes API Observable Response using Retrofit2
 * Created by rahul on 4/3/17.
 */
@Singleton
class DataManagerNote @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperNote: DatabaseHelperNote,
    val sdkBaseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    private val notesApi: NotesApi get() = sdkBaseApiManager.getNoteApi()

    /**
     * This Method Request the REST API of Note and In response give the List of Notes
     */
    fun getNotes(entityType: String?, entityId: Int): Observable<List<Note>> {
        return notesApi.retrieveNotesByResource(entityType, entityId.toLong())
            .map { NoteMapper.mapFromEntityList(it) }
    }
}