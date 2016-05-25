/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.App;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.loan.SavingsApproval;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author nellyk
 */
public class SavingsAccountApproval extends DialogFragment implements MFDatePicker
        .OnDatePickListener {

    public static final String TAG = "SavingsAccountApproval";
    public static int savingsAccountNumber;
    public static DepositType savingsAccountType;
    View rootView;
    @InjectView(R.id.et_s_approval_date)
    TextView et_s_approval_date;
    @InjectView(R.id.bt_approve_savings)
    Button bt_approve_savings;
    @InjectView(R.id.et_savings_approval_reason)
    EditText et_savings_approval_reason;
    String approvaldate;
    private DialogFragment mfDatePicker;

    public static SavingsAccountApproval newInstance(int savingsAccountNumber, DepositType type) {
        SavingsAccountApproval savingsAccountApproval = new SavingsAccountApproval();
        Bundle args = new Bundle();
        args.putInt(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountNumber);
        args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, type);
        savingsAccountApproval.setArguments(args);
        return savingsAccountApproval;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            savingsAccountNumber = getArguments().getInt(Constants.SAVINGS_ACCOUNT_NUMBER);
            savingsAccountType = getArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE);
        }

        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null)
            savingsAccountNumber = getArguments().getInt(Constants.ENTITY_TYPE_SAVINGS);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.dialog_fragment_approve_savings, null);
        ButterKnife.inject(this, rootView);
        inflateApprovalDate();

        approvaldate = et_s_approval_date.getText().toString();
        approvaldate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(approvaldate)
                .replace("-", " ");

        bt_approve_savings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SavingsApproval savingsApproval = new SavingsApproval();
                savingsApproval.setNote(et_savings_approval_reason.getEditableText().toString());
                savingsApproval.setApprovedOnDate(approvaldate);

                initiateSavingsApproval(savingsApproval);

            }
        });

        return rootView;
    }

    @Override
    public void onDatePicked(String date) {
        et_s_approval_date.setText(date);

    }


    private void initiateSavingsApproval(final SavingsApproval savingsApproval) {

        App.apiManager.approveSavingsApplication(savingsAccountNumber,
                savingsApproval,
                new Callback<GenericResponse>() {
                    @Override
                    public void success(GenericResponse genericResponse, Response response) {
                        Toast.makeText(getActivity(), "Savings Approved", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    public void inflateApprovalDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        et_s_approval_date.setText(MFDatePicker.getDatePickedAsString());

        et_s_approval_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }
}
