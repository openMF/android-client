package com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.databinding.FragmentCollectionSheetDialogBinding;
import com.mifos.mifosxdroid.online.collectionsheetindividual.NewIndividualCollectionSheetFragment;
import com.mifos.utils.Constants;


/**
 * Created by aksh on 2/7/18.
 */

public class CollectionSheetDialogFragment extends BottomSheetDialogFragment {

    private FragmentCollectionSheetDialogBinding binding;
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
        binding = FragmentCollectionSheetDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnFillnow.setOnClickListener(view1 -> setBtnFillnow());
        binding.btnCancel.setOnClickListener(view1 -> setBtnCancel());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.tvDueDate.setText(date);
        binding.tvMembers.setText(Integer.toString(members));
    }

    public void setBtnFillnow() {
        ((NewIndividualCollectionSheetFragment) getTargetFragment()).getResponse(Constants.FILLNOW);
    }

    public void setBtnCancel() {
        getDialog().dismiss();
    }
}
