package com.mifos.mifosxdroid.online.note

import com.mifos.core.objects.noncore.Note
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface NoteRepository {

    fun getNotes(entityType: String?, entityId: Int): Observable<List<Note>>

}