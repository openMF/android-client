/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments.loanaccountdisbursement;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.templates.loans.LoanDisburseTemplate;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nellyk on 1/22/2016.
 */
public class LoanAccountDisbursement extends DialogFragment implements
        MFDatePicker.OnDatePickListener, LoanAccountDisbursementMvpView,
        AdapterView.OnItemSelectedListener {

    public final String LOG_TAG = getClass().getSimpleName();

    public int loanAccountNumber;

    SafeUIBlockingUtility safeUIBlockingUtility;

    @BindView(R.id.tv_loan_disbursement_dates)
    TextView tvLoanDisbursementDates;

    @BindView(R.id.bt_disburse_loan)
    Button btnDisburseLoan;

    @BindView(R.id.sp_loan_payment_type)
    Spinner spPaymentType;

    @BindView(R.id.et_disbursed_amount)
    EditText etDisbursedAmount;

    @BindView(R.id.et_disbursement_note)
    EditText etDisbursementNote;

    @Inject
    LoanAccountDisbursementPresenter mLoanAccountDisbursementPresenter;

    int paymentTypeId;

    String disbursement_dates;

    View rootView;

    private OnDialogFragmentInteractionListener mListener;
    private DialogFragment mfDatePicker;
    private List<String> paymentType = new ArrayList<>();
    private ArrayAdapter<String> paymentTypeAdapter;
    private LoanDisburseTemplate mLoanDisburseTemplate;

    public static LoanAccountDisbursement newInstance(int loanAccountNumber) {
        LoanAccountDisbursement loanAccountDisbursement = new LoanAccountDisbursement();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        loanAccountDisbursement.setArguments(args);
        return loanAccountDisbursement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.dialog_fragment_disburse_loan, null);

        ButterKnife.bind(this, rootView);
        mLoanAccountDisbursementPresenter.attachView(this);

        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());

        inflateDisbursementDate();
        inflatePaymentTypeSpinner();
        disbursement_dates = tvLoanDisbursementDates.getText().toString();
        disbursement_dates = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (disbursement_dates).replace("-", " ");
        return rootView;
    }

    @OnClick(R.id.bt_disburse_loan)
    public void submitDisburseLoan() {
        LoanDisbursement loanDisbursement = new LoanDisbursement();
        loanDisbursement.setNote(etDisbursedAmount.getEditableText().toString());
        loanDisbursement.setActualDisbursementDate(disbursement_dates);
        loanDisbursement.setTransactionAmount(etDisbursedAmount.getEditableText()
                .toString());
        loanDisbursement.setPaymentId(paymentTypeId);
        initiateLoanDisbursement(loanDisbursement);
    }


    @Override
    public void onDatePicked(String date) {
        tvLoanDisbursementDates.setText(date);

    }

    private void inflatePaymentTypeSpinner() {
        mLoanAccountDisbursementPresenter.loadLoanTemplate(loanAccountNumber);
        paymentTypeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, paymentType);
        paymentTypeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentType.setAdapter(paymentTypeAdapter);
        spPaymentType.setOnItemSelectedListener(this);
    }

    private void initiateLoanDisbursement(final LoanDisbursement loanDisbursement) {
        mLoanAccountDisbursementPresenter.dispurseLoan(loanAccountNumber, loanDisbursement);
    }

    public void inflateDisbursementDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tvLoanDisbursementDates.setText(MFDatePicker.getDatePickedAsString());
    }

    @OnClick(R.id.tv_loan_disbursement_dates)
    public void inflateDatePicker() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @Override
    public void showLoanTemplate(LoanDisburseTemplate loanDisburseTemplate) {
        mLoanDisburseTemplate = loanDisburseTemplate;
        paymentType.addAll(mLoanAccountDisbursementPresenter.filterPaymentType
                (loanDisburseTemplate.getPaymentTypeOptions()));
        paymentTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDispurseLoanSuccessfully(Loans loans) {
        Toast.makeText(getActivity(), "The Loan has been Disbursed", Toast
                .LENGTH_LONG).show();
    }

    @Override
    public void showError(String s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            safeUIBlockingUtility.safelyBlockUI();
        } else {
            safeUIBlockingUtility.safelyUnBlockUI();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanAccountDisbursementPresenter.detachView();
    }

    public interface OnDialogFragmentInteractionListener {


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_loan_payment_type:
                paymentTypeId = mLoanDisburseTemplate.getPaymentTypeOptions().get(position).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}