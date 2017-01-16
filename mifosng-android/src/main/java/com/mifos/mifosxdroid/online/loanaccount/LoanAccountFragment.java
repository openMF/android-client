/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loanaccount;

import android.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableDialogFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.AmortizationTypeOptions;
import com.mifos.objects.templates.loans.FundOptions;
import com.mifos.objects.templates.loans.InterestCalculationPeriodType;
import com.mifos.objects.templates.loans.InterestTypeOptions;
import com.mifos.objects.templates.loans.LoanOfficerOptions;
import com.mifos.objects.templates.loans.LoanTemplate;
import com.mifos.objects.templates.loans.TransactionProcessingStrategyOptions;
import com.mifos.services.data.LoansPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;


/**
 * Created by nellyk on 1/22/2016.
 * <p/>
 * Use this  Fragment to Create and/or Update loan
 */
public class LoanAccountFragment extends ProgressableDialogFragment
        implements MFDatePicker.OnDatePickListener, LoanAccountMvpView, OnItemSelectedListener {

    public final String LOG_TAG = getClass().getSimpleName();

    View rootView;

    @BindView(R.id.sp_lproduct)
    Spinner spLoanProduct;

    @BindView(R.id.sp_loan_purpose)
    Spinner spLoanPurpose;

    @BindView(R.id.tv_submittedon_date)
    TextView tvSubmittedOnDate;

    @BindView(R.id.et_client_external_id)
    EditText etClientExternalId;

    @BindView(R.id.et_principal)
    EditText etPrincipal;

    @BindView(R.id.et_loanterm)
    EditText etLoanTerm;

    @BindView(R.id.et_numberofrepayments)
    EditText etNumberOfRepayments;

    @BindView(R.id.et_repaidevery)
    EditText etRepaidEvery;

    @BindView(R.id.sp_payment_periods)
    Spinner spPaymentPeriods;

    @BindView(R.id.et_nominal_interest_rate)
    EditText etNominalInterestRate;

    @BindView(R.id.tv_nominal_rate_year_month)
    TextView tvNominalRatePerYearMonth;

    @BindView(R.id.sp_amortization)
    Spinner spAmortization;

    @BindView(R.id.sp_interestcalculationperiod)
    Spinner spInterestCalculationPeriod;

    @BindView(R.id.sp_repaymentstrategy)
    Spinner spRepaymentStrategy;

    @BindView(R.id.sp_interest_type)
    Spinner spInterestType;

    @BindView(R.id.sp_loan_officer)
    Spinner spLoanOfficer;

    @BindView(R.id.sp_fund)
    Spinner spFund;

    @BindView(R.id.cb_calculateinterest)
    CheckBox cbCalculateInterest;

    @BindView(R.id.tv_disbursementon_date)
    TextView tvDisbursementOnDate;

    @BindView(R.id.bt_loan_submit)
    Button btnLoanSubmit;

    @Inject
    LoanAccountPresenter mLoanAccountPresenter;

    ResponseBody mResponse;

    String submittion_date;
    String disbursementon_date;

    private DialogFragment mfDatePicker;
    private int productId;
    private int clientId;
    private int loanPurposeId;
    private int loanTermFrequency;
    private int transactionProcessingStrategyId;
    private int amortizationTypeId;
    private int interestCalculationPeriodTypeId;
    private int fundId;
    private int loanOfficerId;
    private int interestTypeId;

    List<LoanProducts> mLoanProducts = new ArrayList<>();
    LoanTemplate mLoanTemplate = new LoanTemplate();

    List<String> mListLoanProducts = new ArrayList<>();
    List<String> mListLoanPurposeOptions = new ArrayList<>();
    List<String> mListAmortizationTypeOptions = new ArrayList<>();
    List<String> mListInterestCalculationPeriodTypeOptions  = new ArrayList<>();
    List<String> mListTransactionProcessingStrategyOptions = new ArrayList<>();
    List<String> mListTermFrequencyTypeOptions = new ArrayList<>();
    List<String> mListLoanFundOptions = new ArrayList<>();
    List<String> mListLoanOfficerOptions = new ArrayList<>();
    List<String> mListInterestTypeOptions = new ArrayList<>();

    ArrayAdapter<String> mLoanProductAdapter;
    ArrayAdapter<String> mLoanPurposeOptionsAdapter;
    ArrayAdapter<String> mAmortizationTypeOptionsAdapter;
    ArrayAdapter<String> mInterestCalculationPeriodTypeOptionsAdapter;
    ArrayAdapter<String> mTransactionProcessingStrategyOptionsAdapter;
    ArrayAdapter<String> mTermFrequencyTypeOptionsAdapter;
    ArrayAdapter<String> mLoanFundOptionsAdapter;
    ArrayAdapter<String> mLoanOfficerOptionsAdapter;
    ArrayAdapter<String> mInterestTypeOptionsAdapter;

    public static LoanAccountFragment newInstance(int clientId) {
        LoanAccountFragment loanAccountFragment = new LoanAccountFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        loanAccountFragment.setArguments(args);
        return loanAccountFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_add_loan, null);

        ButterKnife.bind(this, rootView);
        mLoanAccountPresenter.attachView(this);

        inflatesubmissionDate();
        inflatedisbusmentDate();
        inflateLoansProductSpinner();

        disbursementon_date = tvDisbursementOnDate.getText().toString();
        submittion_date = tvSubmittedOnDate.getText().toString();
        submittion_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (submittion_date).replace("-", " ");
        disbursementon_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (disbursementon_date).replace("-", " ");

        inflateSpinners();

        return rootView;
    }

    @OnClick(R.id.bt_loan_submit)
    public void submit() {

        LoansPayload loansPayload = new LoansPayload();
        loansPayload.setAllowPartialPeriodInterestCalcualtion(cbCalculateInterest
                .isChecked());
        loansPayload.setAmortizationType(amortizationTypeId);
        loansPayload.setClientId(clientId);
        loansPayload.setDateFormat("dd MMMM yyyy");
        loansPayload.setExpectedDisbursementDate(disbursementon_date);
        loansPayload.setInterestCalculationPeriodType(interestCalculationPeriodTypeId);
        loansPayload.setLoanType("individual");
        loansPayload.setLocale("en");
        loansPayload.setNumberOfRepayments(etNumberOfRepayments.getEditableText()
                .toString());
        loansPayload.setPrincipal(etPrincipal.getEditableText().toString());
        loansPayload.setProductId(productId);
        loansPayload.setRepaymentEvery(etRepaidEvery.getEditableText().toString());
        loansPayload.setSubmittedOnDate(submittion_date);
        loansPayload.setLoanPurposeId(loanPurposeId);
        loansPayload.setLoanTermFrequency(loanTermFrequency);
        loansPayload.setTransactionProcessingStrategyId(transactionProcessingStrategyId);
        loansPayload.setFundId(fundId);
        loansPayload.setInterestType(interestTypeId);
        loansPayload.setLoanOfficerId(loanOfficerId);

        initiateLoanCreation(loansPayload);
    }

    @Override
    public void onDatePicked(String date) {
        tvSubmittedOnDate.setText(date);
        tvDisbursementOnDate.setText(date);

    }

    private void inflateSpinners() {

        //Inflating the LoanProducts Spinner
        mLoanProductAdapter = new ArrayAdapter<>(getActivity(), layout.simple_spinner_item,
                mListLoanProducts);
        mLoanProductAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spLoanProduct.setAdapter(mLoanProductAdapter);
        spLoanProduct.setOnItemSelectedListener(this);

        //Inflating the LoanPurposeOptions
        mLoanPurposeOptionsAdapter = new ArrayAdapter<>(getActivity(), layout.simple_spinner_item,
                mListLoanPurposeOptions);
        mLoanPurposeOptionsAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spLoanPurpose.setAdapter(mLoanPurposeOptionsAdapter);
        spLoanPurpose.setOnItemSelectedListener(this);

        //Inflating AmortizationTypeOptions Spinner
        mAmortizationTypeOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListAmortizationTypeOptions);
        mAmortizationTypeOptionsAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        spAmortization.setAdapter(mAmortizationTypeOptionsAdapter);
        spAmortization.setOnItemSelectedListener(this);

        //Inflating InterestCalculationPeriodTypeOptions Spinner
        mInterestCalculationPeriodTypeOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListInterestCalculationPeriodTypeOptions);
        mInterestCalculationPeriodTypeOptionsAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        spInterestCalculationPeriod.setAdapter(mInterestCalculationPeriodTypeOptionsAdapter);
        spInterestCalculationPeriod.setOnItemSelectedListener(this);

        //Inflate TransactionProcessingStrategyOptions Spinner
        mTransactionProcessingStrategyOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListTransactionProcessingStrategyOptions);
        mTransactionProcessingStrategyOptionsAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        spRepaymentStrategy.setAdapter(mTransactionProcessingStrategyOptionsAdapter);
        spRepaymentStrategy.setOnItemSelectedListener(this);

        //Inflate TermFrequencyTypeOptionsAdapter Spinner
        mTermFrequencyTypeOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListTermFrequencyTypeOptions);
        mTermFrequencyTypeOptionsAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        spPaymentPeriods.setAdapter(mTermFrequencyTypeOptionsAdapter);
        spPaymentPeriods.setOnItemSelectedListener(this);

        //Inflate FondOptions Spinner
        mLoanFundOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListLoanFundOptions);
        mLoanFundOptionsAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spFund.setAdapter(mLoanFundOptionsAdapter);
        spFund.setOnItemSelectedListener(this);

        //Inflating LoanOfficerOptions Spinner
        mLoanOfficerOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListLoanOfficerOptions);
        mLoanOfficerOptionsAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spLoanOfficer.setAdapter(mLoanOfficerOptionsAdapter);
        spLoanOfficer.setOnItemSelectedListener(this);

        //Inflating InterestTypeOptions Spinner
        mInterestTypeOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListInterestTypeOptions);
        mInterestTypeOptionsAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spInterestType.setAdapter(mInterestTypeOptionsAdapter);
        spInterestType.setOnItemSelectedListener(this);

    }

    private void inflateLoansProductSpinner() {
        mLoanAccountPresenter.loadAllLoans();
    }

    private void inflateLoanPurposeSpinner() {
        mLoanAccountPresenter.loadLoanAccountTemplate(clientId, productId);
    }

    private void initiateLoanCreation(LoansPayload loansPayload) {
        mLoanAccountPresenter.createLoansAccount(loansPayload);
    }

    public void inflatesubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tvSubmittedOnDate.setText(MFDatePicker.getDatePickedAsString());
    }

    @OnClick(R.id.tv_submittedon_date)
    public void setSubmittedOnDate() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    public void inflatedisbusmentDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tvDisbursementOnDate.setText(MFDatePicker.getDatePickedAsString());
    }

    @OnClick(R.id.tv_disbursementon_date)
    public void setDisbursementDate() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @Override
    public void showAllLoan(List<LoanProducts> loans) {
        mLoanProducts = loans;
        for (LoanProducts loanProducts : mLoanProducts) {
            mListLoanProducts.add(loanProducts.getName());
        }
        mLoanProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoanAccountTemplate(LoanTemplate loanTemplate) {
        mLoanTemplate = loanTemplate;

        for (com.mifos.objects.templates.loans.LoanPurposeOptions loanPurposeOptions :
                mLoanTemplate.getLoanPurposeOptions()) {
            mListLoanPurposeOptions.add(loanPurposeOptions.getName());
        }
        mLoanPurposeOptionsAdapter.notifyDataSetChanged();

        for (AmortizationTypeOptions amortizationTypeOptions :
                mLoanTemplate.getAmortizationTypeOptions()) {
            mListAmortizationTypeOptions.add(amortizationTypeOptions.getValue());
        }
        mAmortizationTypeOptionsAdapter.notifyDataSetChanged();

        for (InterestCalculationPeriodType interestCalculationPeriodType : mLoanTemplate
                .getInterestCalculationPeriodTypeOptions()) {
            mListInterestCalculationPeriodTypeOptions.add(interestCalculationPeriodType.getValue());
        }
        mInterestCalculationPeriodTypeOptionsAdapter.notifyDataSetChanged();

        for (TransactionProcessingStrategyOptions transactionProcessingStrategyOptions :
                mLoanTemplate.getTransactionProcessingStrategyOptions()) {
            mListTransactionProcessingStrategyOptions.add(transactionProcessingStrategyOptions
                    .getName());
        }
        mTransactionProcessingStrategyOptionsAdapter.notifyDataSetChanged();

        for (com.mifos.objects.templates.loans.TermFrequencyTypeOptions termFrequencyTypeOptions :
                 mLoanTemplate.getTermFrequencyTypeOptions()) {
            mListTermFrequencyTypeOptions.add(termFrequencyTypeOptions.getValue());
        }
        mTermFrequencyTypeOptionsAdapter.notifyDataSetChanged();

        for (FundOptions fundOptions : mLoanTemplate.getFundOptions()) {
            mListLoanFundOptions.add(fundOptions.getName());
        }
        mLoanFundOptionsAdapter.notifyDataSetChanged();

        for (LoanOfficerOptions loanOfficerOptions : mLoanTemplate.getLoanOfficerOptions()) {
            mListLoanOfficerOptions.add(loanOfficerOptions.getDisplayName());
        }
        mLoanOfficerOptionsAdapter.notifyDataSetChanged();

        for (InterestTypeOptions interestTypeOptions : mLoanTemplate.getInterestTypeOptions()) {
            mListInterestTypeOptions.add(interestTypeOptions.getValue());
        }
        mInterestTypeOptionsAdapter.notifyDataSetChanged();

        showDefaultValues(mLoanTemplate.getInterestRateFrequencyType().getValue());

    }

    @Override
    public void showLoanAccountCreatedSuccessfully(Loans loans) {
        Toast.makeText(getActivity(), "The Loan has been submitted for Approval", Toast
                .LENGTH_LONG).show();
    }

    @Override
    public void showFetchingError(String s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanAccountPresenter.detachView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.sp_lproduct :
                productId = mLoanProducts.get(position).getId();
                inflateLoanPurposeSpinner();
                break;

            case R.id.sp_loan_purpose :
                loanPurposeId = mLoanTemplate.getLoanPurposeOptions().get(position).getId();
                break;

            case R.id.sp_amortization :
                amortizationTypeId = mLoanTemplate.getAmortizationTypeOptions()
                        .get(position).getId();
                break;

            case R.id.sp_interestcalculationperiod :
                interestCalculationPeriodTypeId = mLoanTemplate
                        .getInterestCalculationPeriodTypeOptions().get(position).getId();
                break;

            case R.id.sp_repaymentstrategy :
                transactionProcessingStrategyId = mLoanTemplate
                        .getTransactionProcessingStrategyOptions().get(position).getId();
                break;

            case R.id.sp_payment_periods :
                loanTermFrequency = mLoanTemplate.getTermFrequencyTypeOptions().get(position)
                        .getId();
                break;

            case R.id.sp_fund :
                fundId = mLoanTemplate.getFundOptions().get(position).getId();
                break;

            case R.id.sp_loan_officer :
                loanOfficerId = mLoanTemplate.getLoanOfficerOptions().get(position).getId();
                break;

            case R.id.sp_interest_type :
                interestTypeId = mLoanTemplate.getInterestTypeOptions().get(position).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showDefaultValues(String nominalRatePerYearMonth) {
        tvNominalRatePerYearMonth.setText(nominalRatePerYearMonth);
    }
}
