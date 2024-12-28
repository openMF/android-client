/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.dbobjects.noncore.Note
import com.mifos.core.network.datamanager.DataManagerNote
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(private val dataManagerNote: DataManagerNote) :
    NoteRepository {

    override fun getNotes(entityType: String?, entityId: Int): List<Note> {
        return dataManagerNote.getNotes(entityType, entityId)
    }
}
