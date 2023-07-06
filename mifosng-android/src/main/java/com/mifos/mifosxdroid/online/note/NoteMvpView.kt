package com.mifos.mifosxdroid.online.note

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.noncore.Note

/**
 * Created by rahul on 4/3/17.
 */
interface NoteMvpView : MvpView {
    fun showUserInterface()
    fun showNote(note: List<Note>)
    fun showEmptyNotes()
    fun showResetVisibility()
    fun showError(message: Int)
}