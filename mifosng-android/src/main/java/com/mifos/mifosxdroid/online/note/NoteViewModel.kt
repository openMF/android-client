package com.mifos.mifosxdroid.online.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.noncore.Note
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: NoteRepository) : ViewModel() {

    private val _noteUiState = MutableLiveData<NoteUiState>()

    val noteUiState: LiveData<NoteUiState>
        get() = _noteUiState

    /**
     * This method load the Notes.
     * Response: List<Note>
    </Note> */
    fun loadNote(type: String?, id: Int) {
        _noteUiState.value = NoteUiState.ShowProgressbar
        _noteUiState.value = NoteUiState.ShowResetVisibility
        repository.getNotes(type, id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Note>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _noteUiState.value = NoteUiState.ShowError(R.string.failed_to_fetch_datatable)
                }

                override fun onNext(note: List<Note>) {
                    if (note.isNotEmpty()) {
                        _noteUiState.value = NoteUiState.ShowNote(note)
                    } else {
                        _noteUiState.value = NoteUiState.ShowEmptyNotes
                    }
                }
            })
    }
}