/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loanaccountapproval;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.api.GenericResponse;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.loan.LoanApproval;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author nellyk
 */
public class LoanAccountApproval extends MifosBaseFragment
        implements MFDatePicker.OnDatePickListener, LoanAccountApprovalMvpView {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.tv_loan_approval_dates)
    TextView tv_loan_approval_dates;

    @BindView(R.id.tv_expected_disbursement_dates)
    TextView tv_expected_disbursement_dates;

    @BindView(R.id.bt_approve_loan)
    Button bt_approve_loan;

    @BindView(R.id.et_approved_amount)
    EditText et_approved_amount;

    @BindView(R.id.et_transaction_amount)
    EditText et_transaction_amount;

    @BindView(R.id.et_approval_note)
    EditText et_approval_note;

    @Inject
    LoanAccountApprovalPresenter mLoanAccountApprovalPresenter;

    View rootView;

    private String approvalDate;
    private String disbursementDate;
    public int loanAccountNumber;
    private boolean isDisbursebemntDate = false;
    private boolean isApprovalDate = false;

    private DialogFragment mfDatePicker;
    private LoanWithAssociations loanWithAssociations;

    public static LoanAccountApproval newInstance(int loanAccountNumber,
                                                  LoanWithAssociations loanWithAssociations) {
        LoanAccountApproval loanAccountApproval = new LoanAccountApproval();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        args.putParcelable(Constants.LOAN_SUMMARY, loanWithAssociations);
        loanAccountApproval.setArguments(args);
        return loanAccountApproval;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
            loanWithAssociations = getArguments().getParcelable(Constants.LOAN_SUMMARY);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.dialog_fragment_approve_loan, null);

        ButterKnife.bind(this, rootView);
        mLoanAccountApprovalPresenter.attachView(this);

        showUserInterface();

        return rootView;
    }

    @OnClick(R.id.bt_approve_loan)
    void onClickApproveLoan() {
        LoanApproval loanApproval = new LoanApproval();
        loanApproval.setNote(et_approval_note.getEditableText().toString());
        loanApproval.setApprovedOnDate(approvalDate);
        /* Notify the user if Approved Amount &
         * Transaction Amount field is blank */
        if (et_approved_amount.getEditableText().toString().isEmpty()
                || et_transaction_amount.getEditableText().toString().isEmpty()) {
            new RequiredFieldException(getString(R.string.amount), getString(R.string
                    .message_field_required)).notifyUserWithToast(getActivity());
            return;
        }
        loanApproval.setApprovedLoanAmount(et_approved_amount.getEditableText().toString());
        loanApproval.setExpectedDisbursementDate(disbursementDate);
        initiateLoanApproval(loanApproval);
    }

    @OnClick(R.id.tv_loan_approval_dates)
    void setApprovalDate() {
        isApprovalDate = true;
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @OnClick(R.id.tv_expected_disbursement_dates)
    void setDisbursementDate() {
        isDisbursebemntDate = true;
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    public void showApprovalDate() {
        approvalDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(approvalDate)
                .replace("-", " ");
    }

    public void showDisbursebemntDate() {
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(disbursementDate)
                .replace("-", " ");
    }

    @Override
    public void showUserInterface() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tv_loan_approval_dates.setText(MFDatePicker.getDatePickedAsString());
        approvalDate = tv_loan_approval_dates.getText().toString();

        tv_expected_disbursement_dates.setText(DateHelper.getDateAsString(loanWithAssociations
                .getTimeline().getExpectedDisbursementDate()));
        disbursementDate = tv_expected_disbursement_dates.getText().toString();

        showApprovalDate();

        et_approved_amount.setText(String.valueOf(loanWithAssociations.getApprovedPrincipal()));
        et_transaction_amount.setText(String.valueOf(loanWithAssociations.getApprovedPrincipal()));
    }



    @Override
    public void onDatePicked(String date) {
        if (isApprovalDate) {
            tv_loan_approval_dates.setText(date);
            approvalDate = date;
            showApprovalDate();
            isApprovalDate = false;
        } else if (isDisbursebemntDate) {
            tv_expected_disbursement_dates.setText(date);
            disbursementDate = date;
            showDisbursebemntDate();
            isDisbursebemntDate = false;
        }

    }

    private void initiateLoanApproval(final LoanApproval loanApproval) {
        mLoanAccountApprovalPresenter.approveLoan(loanAccountNumber, loanApproval);
    }


    @Override
    public void showLoanApproveSuccessfully(GenericResponse genericResponse) {
        Toast.makeText(getActivity(), "Loan Approved", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void showLoanApproveFailed(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressbar(boolean show) {
        if (show) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanAccountApprovalPresenter.detachView();
    }
}
