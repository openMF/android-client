package com.mifos.core.data.repository_imp

import android.database.Observable
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.network.datamanager.DataManagerNote
import com.mifos.core.objects.noncore.Note
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(private val dataManagerNote: DataManagerNote) :
    NoteRepository {

    override fun getNotes(entityType: String?, entityId: Int): List<Note> {
        return dataManagerNote.getNotes(entityType, entityId)
    }
}