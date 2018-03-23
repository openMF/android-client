package com.mifos.mifosxdroid.online.note;

import com.mifos.api.datamanager.DataManagerNote;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.noncore.Note;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by rahul on 4/3/17.
 */
public class NotePresenter extends BasePresenter<NoteMvpView> {

    private final DataManagerNote dataManagerNote;
    private CompositeDisposable compositeDisposable;

    @Inject
    public NotePresenter(DataManagerNote dataManagerNote) {
        this.dataManagerNote = dataManagerNote;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(NoteMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    /**
     * This method load the Notes.
     * Response: List<Note>
     */
    public void loadNote(String type, int id) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        getMvpView().showResetVisibility();
        compositeDisposable.add(dataManagerNote.getNotes(type, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Note>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_fetch_datatable);
                    }

                    @Override
                    public void onNext(List<Note> note) {
                        getMvpView().showProgressbar(false);
                        if (!note.isEmpty()) {
                            getMvpView().showNote(note);
                        } else {
                            getMvpView().showEmptyNotes();
                        }
                    }
                }));
    }

}