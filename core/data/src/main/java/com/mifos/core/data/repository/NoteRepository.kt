package com.mifos.core.data.repository

import android.database.Observable
import com.mifos.core.objects.noncore.Note
import kotlinx.coroutines.flow.StateFlow

interface NoteRepository {

    fun getNotes(entityType: String?, entityId: Int): List<Note>

}