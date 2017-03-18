package com.mifos.mifosxdroid.online.note;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.noncore.Note;

import java.util.List;

/**
 * Created by rahul on 4/3/17.
 */
public interface NoteMvpView extends MvpView {

    void showUserInterface();

    void showNote(List<Note> note);

    void showEmptyNotes();

    void showResetVisibility();

    void showError(int message);
}
