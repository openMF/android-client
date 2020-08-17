/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loanaccount;

import android.R.layout;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.loan.AccountLinkingOptions;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.AmortizationTypeOptions;
import com.mifos.objects.templates.loans.FundOptions;
import com.mifos.objects.templates.loans.InterestCalculationPeriodType;
import com.mifos.objects.templates.loans.InterestTypeOptions;
import com.mifos.objects.templates.loans.LoanOfficerOptions;
import com.mifos.objects.templates.loans.LoanTemplate;
import com.mifos.objects.templates.loans.RepaymentFrequencyDaysOfWeekTypeOptions;
import com.mifos.objects.templates.loans.RepaymentFrequencyNthDayTypeOptions;
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

    @BindView(R.id.sp_linking_options)
    Spinner spLinkingOptions;

    @BindView(R.id.et_loanterm)
    EditText etLoanTerm;

    @BindView(R.id.et_numberofrepayments)
    EditText etNumberOfRepayments;

    @BindView(R.id.et_repaidevery)
    EditText etRepaidEvery;

    @BindView(R.id.sp_payment_periods)
    Spinner spPaymentPeriods;

    @BindView(R.id.tv_repaid_nthfreq_label_on)
    TextView tvRepaidNthFreqLabelOn;

    @BindView(R.id.sp_repayment_freq_nth_day)
    Spinner spRepaymentFreqNthDay;

    @BindView(R.id.sp_loan_term_periods)
    Spinner spLoanTermFrequencyType;

    @BindView(R.id.sp_repayment_freq_day_of_week)
    Spinner spRepaymentFreqDayOfWeek;

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

    @BindView(R.id.btn_loan_submit)
    Button btnLoanSubmit;

    @Inject
    LoanAccountPresenter mLoanAccountPresenter;

    String submissionDate;
    String disbursementDate;

    private boolean hasDataTables;
    private DialogFragment mfDatePicker;
    private int productId;
    private int clientId;
    private int loanPurposeId;
    private int loanTermFrequency;
    private int loanTermFrequencyType;
    private Integer termFrequency;
    private Integer repaymentEvery;
    private int transactionProcessingStrategyId;
    private int amortizationTypeId;
    private int interestCalculationPeriodTypeId;
    private Integer fundId;
    private int loanOfficerId;
    private int interestTypeId;
    private Integer repaymentFrequencyNthDayType;
    private Integer repaymentFrequencyDayOfWeek;
    private Double interestRatePerPeriod;
    private Integer linkAccountId;
    private boolean isDisbursebemntDate = false;
    private boolean isSubmissionDate = false;

    List<LoanProducts> mLoanProducts = new ArrayList<>();
    List<RepaymentFrequencyNthDayTypeOptions>
            mRepaymentFrequencyNthDayTypeOptions = new ArrayList<>();
    List<RepaymentFrequencyDaysOfWeekTypeOptions>
            mRepaymentFrequencyDaysOfWeekTypeOptions = new ArrayList<>();
    LoanTemplate mLoanTemplate = new LoanTemplate();

    List<String> mListLoanProducts = new ArrayList<>();
    List<String> mListLoanPurposeOptions = new ArrayList<>();
    List<String> mListAccountLinkingOptions = new ArrayList<>();
    List<String> mListAmortizationTypeOptions = new ArrayList<>();
    List<String> mListInterestCalculationPeriodTypeOptions  = new ArrayList<>();
    List<String> mListTransactionProcessingStrategyOptions = new ArrayList<>();
    List<String> mListTermFrequencyTypeOptions = new ArrayList<>();
    List<String> mListLoanTermFrequencyTypeOptions = new ArrayList<>();
    List<String> mListRepaymentFrequencyNthDayTypeOptions = new ArrayList<>();
    List<String> mListRepaymentFrequencyDayOfWeekTypeOptions = new ArrayList<>();
    List<String> mListLoanFundOptions = new ArrayList<>();
    List<String> mListLoanOfficerOptions = new ArrayList<>();
    List<String> mListInterestTypeOptions = new ArrayList<>();

    ArrayAdapter<String> mLoanProductAdapter;
    ArrayAdapter<String> mLoanPurposeOptionsAdapter;
    ArrayAdapter<String> mAccountLinkingOptionsAdapter;
    ArrayAdapter<String> mAmortizationTypeOptionsAdapter;
    ArrayAdapter<String> mInterestCalculationPeriodTypeOptionsAdapter;
    ArrayAdapter<String> mTransactionProcessingStrategyOptionsAdapter;
    ArrayAdapter<String> mTermFrequencyTypeOptionsAdapter;
    ArrayAdapter<String> mLoanTermFrequencyTypeAdapter;
    ArrayAdapter<String> mRepaymentFrequencyNthDayTypeOptionsAdapter;
    ArrayAdapter<String> mRepaymentFrequencyDayOfWeekTypeOptionsAdapter;
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
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_add_loan, null);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        ButterKnife.bind(this, rootView);
        mLoanAccountPresenter.attachView(this);


        inflateSubmissionDate();
        inflateDisbursementDate();
        inflateLoansProductSpinner();

        disbursementDate = tvDisbursementOnDate.getText().toString();
        submissionDate = tvSubmittedOnDate.getText().toString();
        submissionDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (submissionDate).replace("-", " ");
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (disbursementDate).replace("-", " ");

        inflateSpinners();

        return rootView;
    }

    @OnClick(R.id.btn_loan_submit)
    public void submit() {

        LoansPayload loansPayload = new LoansPayload();
        loansPayload.setAllowPartialPeriodInterestCalcualtion(cbCalculateInterest
                .isChecked());
        loansPayload.setAmortizationType(amortizationTypeId);
        loansPayload.setClientId(clientId);
        loansPayload.setDateFormat("dd MMMM yyyy");
        loansPayload.setExpectedDisbursementDate(disbursementDate);
        loansPayload.setInterestCalculationPeriodType(interestCalculationPeriodTypeId);
        loansPayload.setLoanType("individual");
        loansPayload.setLocale("en");
        loansPayload.setNumberOfRepayments(etNumberOfRepayments.getEditableText()
                .toString());
        loansPayload.setPrincipal(etPrincipal.getEditableText().toString());
        loansPayload.setProductId(productId);
        loansPayload.setRepaymentEvery(etRepaidEvery.getEditableText().toString());
        loansPayload.setSubmittedOnDate(submissionDate);
        loansPayload.setLoanPurposeId(loanPurposeId);
        loansPayload.setLoanTermFrequency(
                Integer.parseInt(etLoanTerm.getEditableText().toString()));
        loansPayload.setLoanTermFrequencyType(loanTermFrequency);

        //loanTermFrequencyType and repaymentFrequencyType should be the same.
        loansPayload.setRepaymentFrequencyType(loanTermFrequency);
        loansPayload.setRepaymentFrequencyDayOfWeekType(
                repaymentFrequencyDayOfWeek != null ? repaymentFrequencyDayOfWeek : null);
        loansPayload.setRepaymentFrequencyNthDayType(
                repaymentFrequencyNthDayType != null ? repaymentFrequencyNthDayType : null);
        loansPayload.setTransactionProcessingStrategyId(transactionProcessingStrategyId);
        loansPayload.setFundId(fundId);
        loansPayload.setInterestType(interestTypeId);
        loansPayload.setLoanOfficerId(loanOfficerId);
        loansPayload.setLinkAccountId(linkAccountId);
        interestRatePerPeriod = Double.parseDouble(
                etNominalInterestRate.getEditableText().toString());
        loansPayload.setInterestRatePerPeriod(interestRatePerPeriod);

        if (hasDataTables) {
            DataTableListFragment fragment = DataTableListFragment.newInstance(
                    mLoanTemplate.getDataTables(),
                    loansPayload, Constants.CLIENT_LOAN);

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST);
            fragmentTransaction.replace(R.id.container, fragment).commit();

        } else {
            initiateLoanCreation(loansPayload);
        }

    }

    @Override
    public void onDatePicked(String date) {

        if (isSubmissionDate) {
            tvSubmittedOnDate.setText(date);
            submissionDate = date;
            isSubmissionDate = false;
        }

        if (isDisbursebemntDate) {
            tvDisbursementOnDate.setText(date);
            disbursementDate = date;
            isDisbursebemntDate = false;
        }
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

        //Inflating Linking Options
        mAccountLinkingOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListAccountLinkingOptions);
        mAccountLinkingOptionsAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spLinkingOptions.setAdapter(mAccountLinkingOptionsAdapter);
        spLinkingOptions.setOnItemSelectedListener(this);

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

        //Inflate LoanTerm Frequency Type adapter
        mLoanTermFrequencyTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListLoanTermFrequencyTypeOptions);
        mLoanTermFrequencyTypeAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        spLoanTermFrequencyType.setAdapter(mLoanTermFrequencyTypeAdapter);
        spLoanTermFrequencyType.setOnItemSelectedListener(this);

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

    private void inflateRepaidMonthSpinners() {

        mRepaymentFrequencyNthDayTypeOptionsAdapter = new ArrayAdapter<>(
                getActivity(), layout.simple_spinner_item,
                mListRepaymentFrequencyNthDayTypeOptions);
        mRepaymentFrequencyNthDayTypeOptionsAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spRepaymentFreqNthDay.setAdapter(mRepaymentFrequencyNthDayTypeOptionsAdapter);
        spRepaymentFreqNthDay.setOnItemSelectedListener(this);

        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter = new ArrayAdapter<>(
                getActivity(), layout.simple_spinner_item,
                mListRepaymentFrequencyDayOfWeekTypeOptions);
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spRepaymentFreqDayOfWeek.setAdapter(mRepaymentFrequencyDayOfWeekTypeOptionsAdapter);
        spRepaymentFreqDayOfWeek.setOnItemSelectedListener(this);
        spRepaymentFreqNthDay.setSelection(mListRepaymentFrequencyNthDayTypeOptions.size() - 1);
        spRepaymentFreqDayOfWeek.setSelection(
                mListRepaymentFrequencyDayOfWeekTypeOptions.size() - 1);
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

    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tvSubmittedOnDate.setText(MFDatePicker.getDatePickedAsString());
    }

    @OnClick(R.id.tv_submittedon_date)
    public void setTvSubmittedOnDate() {
        isSubmissionDate = true;
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    public void inflateDisbursementDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tvDisbursementOnDate.setText(MFDatePicker.getDatePickedAsString());
    }

    @OnClick(R.id.tv_disbursementon_date)
    public void setTvDisbursementOnDate() {
        isDisbursebemntDate = true;
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @Override
    public void showAllLoan(List<LoanProducts> loans) {
        mLoanProducts = loans;
        mListLoanProducts.clear();
        for (LoanProducts loanProducts : mLoanProducts) {
            mListLoanProducts.add(loanProducts.getName());
        }
        mLoanProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoanAccountTemplate(LoanTemplate loanTemplate) {
        mLoanTemplate = loanTemplate;

        hasDataTables = mLoanTemplate.getDataTables().size() > 0;

        mListRepaymentFrequencyNthDayTypeOptions.clear();
        mRepaymentFrequencyNthDayTypeOptions = mLoanTemplate
                .getRepaymentFrequencyNthDayTypeOptions();
        for (RepaymentFrequencyNthDayTypeOptions options : mRepaymentFrequencyNthDayTypeOptions) {
            mListRepaymentFrequencyNthDayTypeOptions.add(options.getValue());
        }
        mListRepaymentFrequencyNthDayTypeOptions.add(
                getResources().getString(R.string.select_week_hint));

        mListRepaymentFrequencyDayOfWeekTypeOptions.clear();
        mRepaymentFrequencyDaysOfWeekTypeOptions = mLoanTemplate
                .getRepaymentFrequencyDaysOfWeekTypeOptions();
        for (RepaymentFrequencyDaysOfWeekTypeOptions options
                : mRepaymentFrequencyDaysOfWeekTypeOptions) {
            mListRepaymentFrequencyDayOfWeekTypeOptions.add(options.getValue());
        }
        mListRepaymentFrequencyDayOfWeekTypeOptions.add(
                getResources().getString(R.string.select_day_hint));

        mListLoanPurposeOptions.clear();
        for (com.mifos.objects.templates.loans.LoanPurposeOptions loanPurposeOptions :
                mLoanTemplate.getLoanPurposeOptions()) {
            mListLoanPurposeOptions.add(loanPurposeOptions.getName());
        }
        mLoanPurposeOptionsAdapter.notifyDataSetChanged();

        mListAccountLinkingOptions.clear();
        for (AccountLinkingOptions options : mLoanTemplate.getAccountLinkingOptions()) {
            mListAccountLinkingOptions.add(options.getProductName());
        }
        mListAccountLinkingOptions.add(
                getResources().getString(R.string.select_linkage_account_hint));
        mAccountLinkingOptionsAdapter.notifyDataSetChanged();

        mListAmortizationTypeOptions.clear();
        for (AmortizationTypeOptions amortizationTypeOptions :
                mLoanTemplate.getAmortizationTypeOptions()) {
            mListAmortizationTypeOptions.add(amortizationTypeOptions.getValue());
        }
        mAmortizationTypeOptionsAdapter.notifyDataSetChanged();

        mListInterestCalculationPeriodTypeOptions.clear();
        for (InterestCalculationPeriodType interestCalculationPeriodType : mLoanTemplate
                .getInterestCalculationPeriodTypeOptions()) {
            mListInterestCalculationPeriodTypeOptions.add(interestCalculationPeriodType.getValue());
        }
        mInterestCalculationPeriodTypeOptionsAdapter.notifyDataSetChanged();

        mListTransactionProcessingStrategyOptions.clear();
        for (TransactionProcessingStrategyOptions transactionProcessingStrategyOptions :
                mLoanTemplate.getTransactionProcessingStrategyOptions()) {
            mListTransactionProcessingStrategyOptions.add(transactionProcessingStrategyOptions
                    .getName());
        }
        mTransactionProcessingStrategyOptionsAdapter.notifyDataSetChanged();

        mListTermFrequencyTypeOptions.clear();
        for (com.mifos.objects.templates.loans.TermFrequencyTypeOptions termFrequencyTypeOptions :
                 mLoanTemplate.getTermFrequencyTypeOptions()) {
            mListTermFrequencyTypeOptions.add(termFrequencyTypeOptions.getValue());
        }
        mTermFrequencyTypeOptionsAdapter.notifyDataSetChanged();

        mListLoanTermFrequencyTypeOptions.clear();
        for (com.mifos.objects.templates.loans.TermFrequencyTypeOptions termFrequencyTypeOptions :
                mLoanTemplate.getTermFrequencyTypeOptions()) {
            mListLoanTermFrequencyTypeOptions.add(termFrequencyTypeOptions.getValue());
        }
        mLoanTermFrequencyTypeAdapter.notifyDataSetChanged();

        mListLoanFundOptions.clear();
        for (FundOptions fundOptions : mLoanTemplate.getFundOptions()) {
            mListLoanFundOptions.add(fundOptions.getName());
        }
        mLoanFundOptionsAdapter.notifyDataSetChanged();

        mListLoanOfficerOptions.clear();
        for (LoanOfficerOptions loanOfficerOptions : mLoanTemplate.getLoanOfficerOptions()) {
            mListLoanOfficerOptions.add(loanOfficerOptions.getDisplayName());
        }
        mLoanOfficerOptionsAdapter.notifyDataSetChanged();

        mListInterestTypeOptions.clear();
        for (InterestTypeOptions interestTypeOptions : mLoanTemplate.getInterestTypeOptions()) {
            mListInterestTypeOptions.add(interestTypeOptions.getValue());
        }
        mInterestTypeOptionsAdapter.notifyDataSetChanged();

        showDefaultValues();

    }

    @Override
    public void showLoanAccountCreatedSuccessfully(Loans loans) {
        Toast.makeText(getActivity(), R.string.loan_creation_success, Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void showMessage(int messageId) {
        Toaster.show(rootView, messageId);
    }

    @Override
    public void showFetchingError(String s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean show) {
        showProgress(show);
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

            //LoanTermFrequencyType must be same as the RepaidFrequencyType
            case R.id.sp_payment_periods :
                loanTermFrequency = mLoanTemplate.getTermFrequencyTypeOptions().get(position)
                        .getId();
                spLoanTermFrequencyType.setSelection(loanTermFrequency);
                if (loanTermFrequency == 2) {
                    // Show and inflate Nth day and week spinners
                    showHideRepaidMonthSpinners(View.VISIBLE);
                    inflateRepaidMonthSpinners();
                } else {
                    showHideRepaidMonthSpinners(View.GONE);
                }
                break;

            case R.id.sp_loan_term_periods:
                loanTermFrequency = mLoanTemplate.getTermFrequencyTypeOptions().get(position)
                        .getId();
                spPaymentPeriods.setSelection(loanTermFrequency);
                if (loanTermFrequency == 2) {
                    // Show and inflate Nth day and week spinners
                    showHideRepaidMonthSpinners(View.VISIBLE);
                    inflateRepaidMonthSpinners();
                } else {
                    showHideRepaidMonthSpinners(View.GONE);
                }
                break;

            case R.id.sp_repayment_freq_nth_day:
                if (mListRepaymentFrequencyNthDayTypeOptions.get(position)
                        .equals(getResources().getString(R.string.select_week_hint))) {
                    repaymentFrequencyNthDayType = null;
                } else {
                    repaymentFrequencyNthDayType = mLoanTemplate
                            .getRepaymentFrequencyNthDayTypeOptions()
                            .get(position).getId();
                }
                break;

            case R.id.sp_repayment_freq_day_of_week:
                if (mListRepaymentFrequencyDayOfWeekTypeOptions.get(position)
                        .equals(getResources().getString(R.string.select_day_hint))) {
                    repaymentFrequencyDayOfWeek = null;
                } else {
                    repaymentFrequencyDayOfWeek = mLoanTemplate
                            .getRepaymentFrequencyDaysOfWeekTypeOptions()
                            .get(position).getId();
                }
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

            case R.id.sp_linking_options:
                if (mListAccountLinkingOptions.get(position)
                        .equals(getResources().getString(R.string.select_linkage_account_hint))) {
                    linkAccountId = null;
                } else {
                    linkAccountId = mLoanTemplate.getAccountLinkingOptions().get(position).getId();
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showHideRepaidMonthSpinners(int visibility) {
        spRepaymentFreqNthDay.setVisibility(visibility);
        spRepaymentFreqDayOfWeek.setVisibility(visibility);
        tvRepaidNthFreqLabelOn.setVisibility(visibility);
    }

    private void showDefaultValues() {
        interestRatePerPeriod = mLoanTemplate.getInterestRatePerPeriod();
        loanTermFrequency = mLoanTemplate.getTermPeriodFrequencyType().getId();
        termFrequency = mLoanTemplate.getTermFrequency();
        etPrincipal.setText(mLoanTemplate.getPrincipal().toString());
        etNumberOfRepayments.setText(mLoanTemplate.getNumberOfRepayments().toString());
        tvNominalRatePerYearMonth
                .setText(mLoanTemplate.getInterestRateFrequencyType().getValue());
        etNominalInterestRate.setText(mLoanTemplate.getInterestRatePerPeriod().toString());
        etLoanTerm.setText(termFrequency.toString());
        if (mLoanTemplate.getRepaymentEvery() != null) {
            repaymentEvery = mLoanTemplate.getRepaymentEvery();
            etRepaidEvery.setText(repaymentEvery.toString());
        }
        if (mLoanTemplate.getFundId() != null) {
            fundId = mLoanTemplate.getFundId();
            spFund.setSelection(mLoanTemplate.getFundNameFromId(fundId));
        }
        spLinkingOptions.setSelection(mListAccountLinkingOptions.size());
    }
}