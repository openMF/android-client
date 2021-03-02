package com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.collectionsheetindividual.NewIndividualCollectionSheetFragment;
import com.mifos.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by aksh on 2/7/18.
 */

public class CollectionSheetDialogFragment extends BottomSheetDialogFragment {
    @BindView(R.id.tv_due_date)
    TextView tvDueDate;

    @BindView(R.id.tv_members)
    TextView tvMembers;

    @BindView(R.id.btn_fillnow)
    Button btnFillnow;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    private View rootView;
    private String date;
    private int members;

    public CollectionSheetDialogFragment() {

    }

    public static CollectionSheetDialogFragment newInstance(String date, int members) {

        CollectionSheetDialogFragment collectionSheetDialogFragment =
                new CollectionSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.REPAYMENT_DATE, date);
        args.putInt(Constants.MEMBERS, members);
        collectionSheetDialogFragment.setArguments(args);
        return collectionSheetDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        date = getArguments().getString(Constants.REPAYMENT_DATE);
        members = getArguments().getInt(Constants.MEMBERS);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_collection_sheet_dialog, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvDueDate.setText(date);
        tvMembers.setText(Integer.toString(members));
    }

    @OnClick(R.id.btn_fillnow)
    public void setBtnFillnow() {
        ((NewIndividualCollectionSheetFragment) getTargetFragment()).getResponse(Constants.FILLNOW);
    }

    @OnClick(R.id.btn_cancel)
    public void setBtnCancel() {
        getDialog().dismiss();
    }
}
