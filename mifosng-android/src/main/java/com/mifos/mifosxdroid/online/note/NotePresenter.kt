package com.mifos.mifosxdroid.online.note

import com.mifos.api.datamanager.DataManagerNote
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.noncore.Note
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by rahul on 4/3/17.
 */
class NotePresenter @Inject constructor(private val dataManagerNote: DataManagerNote) : BasePresenter<NoteMvpView>() {
    private val subscriptions: CompositeSubscription = CompositeSubscription()
    override fun attachView(mvpView: NoteMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.unsubscribe()
    }

    /**
     * This method load the Notes.
     * Response: List<Note>
    </Note> */
    fun loadNote(type: String?, id: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mvpView?.showResetVisibility()
        subscriptions.add(dataManagerNote.getNotes(type, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Note>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_fetch_datatable)
                    }

                    override fun onNext(note: List<Note>) {
                        mvpView?.showProgressbar(false)
                        if (note.isNotEmpty()) {
                            mvpView?.showNote(note)
                        } else {
                            mvpView?.showEmptyNotes()
                        }
                    }
                }))
    }

}