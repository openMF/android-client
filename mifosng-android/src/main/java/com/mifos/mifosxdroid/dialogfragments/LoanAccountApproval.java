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
import com.mifos.objects.accounts.loan.LoanApproval;
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
public class LoanAccountApproval extends DialogFragment implements MFDatePicker.OnDatePickListener {

    public static final String TAG = "LoanAccountApproval";
    View rootView;

    @InjectView(R.id.tv_loan_approval_dates)
    TextView tv_loan_approval_dates;
    @InjectView(R.id.bt_approve_loan)
    Button bt_approve_loan;
    @InjectView(R.id.et_approved_amount)
    EditText et_approved_amount;
    @InjectView(R.id.et_approval_note)
    EditText et_approval_note;
    private DialogFragment mfDatePicker;
    public static int loanAccountNumber;
    String approvaldate;

    public static LoanAccountApproval newInstance(int loanAccountNumber) {
        LoanAccountApproval loanAccountApproval = new LoanAccountApproval();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        loanAccountApproval.setArguments(args);
        return loanAccountApproval;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        }

        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.dialog_fragment_approve_loan, null);
        ButterKnife.inject(this, rootView);
        inflateApprovalDate();
        approvaldate = tv_loan_approval_dates.getText().toString();
        approvaldate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(approvaldate).replace("-", " ");
        bt_approve_loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoanApproval loanApproval = new LoanApproval();
                loanApproval.setNote(et_approval_note.getEditableText().toString());
                loanApproval.setApprovedOnDate(approvaldate);
                loanApproval.setApprovedLoanAmount(et_approved_amount.getEditableText().toString());

                initiateLoanApproval(loanApproval);

            }
        });

        return rootView;
    }

    @Override
    public void onDatePicked(String date) {
        tv_loan_approval_dates.setText(date);

    }


    private void initiateLoanApproval(final LoanApproval loanApproval) {

        App.apiManager.approveLoan(loanAccountNumber,
                loanApproval,
                new Callback<GenericResponse>() {
                    @Override
                    public void success(GenericResponse genericResponse, Response response) {
                        Toast.makeText(getActivity(), "Loan Approved", Toast.LENGTH_LONG).show();

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

        tv_loan_approval_dates.setText(MFDatePicker.getDatePickedAsString());

        tv_loan_approval_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }
        });

    }


}
