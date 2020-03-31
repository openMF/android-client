package com.mifos.mifosxdroid.online.note;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.NoteAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.noncore.Note;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rahul on 4/3/17.
 */
public class NoteFragment extends MifosBaseFragment implements NoteMvpView,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_note)
    RecyclerView rvNote;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tv_error)
    TextView tvError;

    @BindView(R.id.iv_error)
    ImageView ivError;

    @BindView(R.id.ll_error)
    LinearLayout llError;

    @Inject
    NotePresenter notePresenter;

    @Inject
    NoteAdapter noteAdapter;

    private View rootView;
    private String entityType;
    private int entityId;
    private List<Note> notes;


    public static NoteFragment newInstance(String entityType, int entityId) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ENTITY_TYPE, entityType);
        args.putInt(Constants.ENTITY_ID, entityId);
        noteFragment.setArguments(args);
        return noteFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        notes = new ArrayList<>();
        if (getArguments() != null) {
            entityType = getArguments().getString(Constants.ENTITY_TYPE);
            entityId = getArguments().getInt(Constants.ENTITY_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        notePresenter.loadNote(entityType, entityId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        ButterKnife.bind(this, rootView);
        notePresenter.attachView(this);

        showUserInterface();
        notePresenter.loadNote(entityType, entityId);

        return rootView;
    }

    @Override
    public void onRefresh() {
        notePresenter.loadNote(entityType, entityId);
    }

    @Override
    public void showUserInterface() {
        setToolbarTitle(getResources().getString(R.string.note));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvNote.setLayoutManager(mLayoutManager);
        rvNote.setHasFixedSize(true);
        rvNote.setAdapter(noteAdapter);
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void showNote(List<Note> notes) {
        this.notes = notes;
        noteAdapter.setNotes(this.notes);
    }

    @Override
    public void showEmptyNotes() {
        llError.setVisibility(View.VISIBLE);
        rvNote.setVisibility(View.GONE);
        tvError.setText(R.string.empty_notes);
    }

    @Override
    public void showResetVisibility() {
        llError.setVisibility(View.GONE);
        rvNote.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(int message) {
        Toaster.show(rootView, message);
        llError.setVisibility(View.VISIBLE);
        rvNote.setVisibility(View.GONE);
        tvError.setText(getString(R.string.failed_to_fetch_notes));
    }

    @Override
    public void showProgressbar(boolean show) {
        if (show && (noteAdapter.getItemCount() == 0)) {
            showMifosProgressBar();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            hideMifosProgressBar();
            swipeRefreshLayout.setRefreshing(show);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideMifosProgressBar();
        notePresenter.detachView();
    }
}
