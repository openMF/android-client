/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.grouploanaccount;

import android.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.mifos.objects.templates.loans.GroupLoanTemplate;
import com.mifos.services.data.GroupLoanPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nellyk on 1/22/2016.
 * <p/>
 * Use this  Fragment to Create and/or Update loan
 */
public class GroupLoanAccountFragment extends ProgressableDialogFragment
        implements MFDatePicker.OnDatePickListener, GroupLoanAccountMvpView,
        AdapterView.OnItemSelectedListener {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.sp_lproduct)
    Spinner spLoanProduct;

    @BindView(R.id.sp_loan_purpose)
    Spinner spLoanPurpose;

    @BindView(R.id.tv_submittedon_date)
    TextView tvSubmittedonDate;

    @BindView(R.id.et_client_external_id)
    EditText etClientExternalId;

    @BindView(R.id.et_principal)
    EditText etPrincipal;

    @BindView(R.id.et_loanterm)
    EditText etLoanterm;

    @BindView(R.id.et_numberofrepayments)
    EditText etNumberOfRepayments;

    @BindView(R.id.et_repaidevery)
    EditText etRepaidevery;

    @BindView(R.id.sp_payment_periods)
    Spinner spPaymentPeriods;

    @BindView(R.id.et_nominal_interest_rate)
    EditText etNominalInterestRate;

    @BindView(R.id.sp_amortization)
    Spinner spAmortization;

    @BindView(R.id.sp_interestcalculationperiod)
    Spinner spInterestCalculationPeriod;

    @BindView(R.id.sp_fund)
    Spinner spFund;

    @BindView(R.id.sp_loan_officer)
    Spinner spLoanOfficer;

    @BindView(R.id.sp_interest_type)
    Spinner spInterestType;

    @BindView(R.id.sp_repaymentstrategy)
    Spinner spRepaymentStrategy;

    @BindView(R.id.cb_calculateinterest)
    CheckBox cbCalculateInterest;

    @BindView(R.id.tv_disbursementon_date)
    TextView tvDisbursementonDate;

    @BindView(R.id.btn_loan_submit)
    Button btLoanSubmit;

    @Inject
    GroupLoanAccountPresenter mGroupLoanAccountPresenter;

    String submittion_date;
    String disbursementon_date;

    View rootView;

    private OnDialogFragmentInteractionListener mListener;
    private DialogFragment mfDatePicker;

    private int productId;
    private int groupId;
    private int loanPurposeId;
    private int loanTermFrequency;
    private int transactionProcessingStrategyId;
    private int amortizationTypeId;
    private int interestCalculationPeriodTypeId;
    private int fundId;
    private int loanOfficerId;
    private int interestTypeMethodId;

    private List<String> amortizationType = new ArrayList<>();
    private List<String> interestCalculationPeriodType = new ArrayList<>();
    private List<String> transactionProcessingStrategy = new ArrayList<>();
    private List<String> termFrequencyType = new ArrayList<>();
    private List<String> loans = new ArrayList<>();
    private List<String> loanPurposeType = new ArrayList<>();
    private List<String> interestTypeOptions = new ArrayList<>();
    private List<String> loanOfficerOptions = new ArrayList<>();
    private List<String> fundOptions = new ArrayList<>();
    private List<String> mListLoanProductsNames = new ArrayList<>();

    private ArrayAdapter<String> amortizationTypeAdapter;
    private ArrayAdapter<String> interestCalculationPeriodTypeAdapter;
    private ArrayAdapter<String> transactionProcessingStrategyAdapter;
    private ArrayAdapter<String> termFrequencyTypeAdapter;
    private ArrayAdapter<String> loanProductAdapter;
    private ArrayAdapter<String> loanPurposeTypeAdapter;
    private ArrayAdapter<String> interestTypeOptionsAdapter;
    private ArrayAdapter<String> loanOfficerOptionsAdapter;
    private ArrayAdapter<String> fundOptionsAdapter;

    private GroupLoanTemplate mGroupLoanTemplate;
    private List<LoanProducts> mLoanProducts;



    public static GroupLoanAccountFragment newInstance(int groupId) {
        GroupLoanAccountFragment grouploanAccountFragment = new GroupLoanAccountFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.GROUP_ID, groupId);
        grouploanAccountFragment.setArguments(args);
        return grouploanAccountFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            groupId = getArguments().getInt(Constants.GROUP_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_add_loan, null);

        ButterKnife.bind(this, rootView);
        mGroupLoanAccountPresenter.attachView(this);

        inflateSubmissionDate();
        inflatedisbusmentDate();
        inflateLoansProductSpinner();
        inflateLoanSpinner();


        disbursementon_date = tvDisbursementonDate.getText().toString();
        submittion_date = tvSubmittedonDate.getText().toString();
        submittion_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (submittion_date).replace("-", " ");
        disbursementon_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (disbursementon_date).replace("-", " ");


        btLoanSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GroupLoanPayload loansPayload = new GroupLoanPayload();
                loansPayload.setAllowPartialPeriodInterestCalcualtion(cbCalculateInterest
                        .isChecked());
                loansPayload.setAmortizationType(amortizationTypeId);
                loansPayload.setGroupId(groupId);
                loansPayload.setDateFormat("dd MMMM yyyy");
                loansPayload.setExpectedDisbursementDate(disbursementon_date);
                loansPayload.setInterestCalculationPeriodType(interestCalculationPeriodTypeId);
                loansPayload.setLoanType("group");
                loansPayload.setLocale("en");
                loansPayload.setNumberOfRepayments(etNumberOfRepayments.getEditableText()
                        .toString());
                loansPayload.setPrincipal(etPrincipal.getEditableText().toString());
                loansPayload.setProductId(productId);
                loansPayload.setRepaymentEvery(etRepaidevery.getEditableText().toString());
                loansPayload.setSubmittedOnDate(submittion_date);
                loansPayload.setLoanPurposeId(loanPurposeId);
                loansPayload.setLoanTermFrequency(loanTermFrequency);
                loansPayload.setTransactionProcessingStrategyId(transactionProcessingStrategyId);

                initiateLoanCreation(loansPayload);

            }
        });

        return rootView;
    }

    public void inflateLoanSpinner() {
        amortizationTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, amortizationType);
        amortizationTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spAmortization.setAdapter(amortizationTypeAdapter);
        spAmortization.setOnItemSelectedListener(this);

        interestCalculationPeriodTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, interestCalculationPeriodType);
        interestCalculationPeriodTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spInterestCalculationPeriod.setAdapter(interestCalculationPeriodTypeAdapter);
        spInterestCalculationPeriod.setOnItemSelectedListener(this);

        transactionProcessingStrategyAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, transactionProcessingStrategy);
        transactionProcessingStrategyAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spRepaymentStrategy.setAdapter(transactionProcessingStrategyAdapter);
        spRepaymentStrategy.setOnItemSelectedListener(this);

        termFrequencyTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, termFrequencyType);
        termFrequencyTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spPaymentPeriods.setAdapter(termFrequencyTypeAdapter);
        spPaymentPeriods.setOnItemSelectedListener(this);

        loanProductAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListLoanProductsNames);
        loanProductAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spLoanProduct.setAdapter(loanProductAdapter);
        spLoanProduct.setOnItemSelectedListener(this);

        loanPurposeTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, loanPurposeType);
        loanPurposeTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spLoanPurpose.setAdapter(loanPurposeTypeAdapter);
        spLoanPurpose.setOnItemSelectedListener(this);

        interestTypeOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, interestTypeOptions);
        interestTypeOptionsAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spInterestType.setAdapter(interestTypeOptionsAdapter);
        spInterestType.setOnItemSelectedListener(this);

        loanOfficerOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, loanOfficerOptions);
        loanOfficerOptionsAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spLoanOfficer.setAdapter(loanOfficerOptionsAdapter);
        spLoanOfficer.setOnItemSelectedListener(this);

        fundOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, fundOptions);
        fundOptionsAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spFund.setAdapter(fundOptionsAdapter);
        spFund.setOnItemSelectedListener(this);
    }

    @Override
    public void onDatePicked(String date) {
        tvSubmittedonDate.setText(date);
        tvDisbursementonDate.setText(date);

    }

    private void inflateLoansProductSpinner() {
        mGroupLoanAccountPresenter.loadAllLoans();
    }


    private void inflateLoanPurposeSpinner() {
        mGroupLoanAccountPresenter.loadGroupLoansAccountTemplate(groupId, productId);
    }



    private void initiateLoanCreation(GroupLoanPayload loansPayload) {
        mGroupLoanAccountPresenter.createGroupLoanAccount(loansPayload);
    }

    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tvSubmittedonDate.setText(MFDatePicker.getDatePickedAsString());

        tvSubmittedonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }

    public void inflatedisbusmentDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tvDisbursementonDate.setText(MFDatePicker.getDatePickedAsString());

        tvDisbursementonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }

    @Override
    public void showAllLoans(List<LoanProducts> loans) {
        mLoanProducts = loans;

        mListLoanProductsNames.addAll(mGroupLoanAccountPresenter
                .filterLoanProducts(loans));
        loanProductAdapter.notifyDataSetChanged();
    }


    @Override
    public void showGroupLoanTemplate(GroupLoanTemplate groupLoanTemplate) {
        mGroupLoanTemplate = groupLoanTemplate;
        amortizationType.addAll(mGroupLoanAccountPresenter.filterAmortizations
                (groupLoanTemplate.getAmortizationTypeOptions()));
        amortizationTypeAdapter.notifyDataSetChanged();

        interestCalculationPeriodType.addAll(mGroupLoanAccountPresenter.
                filterInterestCalculationPeriods(
                groupLoanTemplate.getInterestCalculationPeriodTypeOptions()));
        interestCalculationPeriodTypeAdapter.notifyDataSetChanged();

        transactionProcessingStrategy.addAll(mGroupLoanAccountPresenter.
                filterTransactionProcessingStrategies
                (groupLoanTemplate.getTransactionProcessingStrategyOptions()));
        transactionProcessingStrategyAdapter.notifyDataSetChanged();

        termFrequencyType.addAll(mGroupLoanAccountPresenter.filterTermFrequencyTypes
                (groupLoanTemplate.getTermFrequencyTypeOptions()));
        termFrequencyTypeAdapter.notifyDataSetChanged();

        loanPurposeType.addAll(mGroupLoanAccountPresenter.filterLoanPurposeTypes
                (groupLoanTemplate.getLoanPurposeOptions()));
        loanPurposeTypeAdapter.notifyDataSetChanged();

        interestTypeOptions.addAll(mGroupLoanAccountPresenter.filterInterestTypeOptions
                (groupLoanTemplate.getInterestTypeOptions()));
        interestTypeOptionsAdapter.notifyDataSetChanged();

        loanOfficerOptions.addAll(mGroupLoanAccountPresenter.filterLoanOfficers
                (groupLoanTemplate.getLoanOfficerOptions()));
        loanOfficerOptionsAdapter.notifyDataSetChanged();

        fundOptions.addAll(mGroupLoanAccountPresenter.filterFunds
                (groupLoanTemplate.getFundOptions()));
        fundOptionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showGroupLoansAccountCreatedSuccessfully(Loans loans) {
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
    public void onDestroyView() {
        super.onDestroyView();
        mGroupLoanAccountPresenter.detachView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnDialogFragmentInteractionListener {


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_lproduct:
                productId = mLoanProducts.get(position).getId();
                inflateLoanPurposeSpinner();
                break;
            case R.id.sp_amortization:
                amortizationTypeId = mGroupLoanTemplate
                        .getAmortizationTypeOptions().get(position).getId();
                break;
            case R.id.sp_interestcalculationperiod:
                interestCalculationPeriodTypeId = mGroupLoanTemplate
                        .getInterestCalculationPeriodTypeOptions().get(position).getId();
                break;
            case R.id.sp_repaymentstrategy:
                transactionProcessingStrategyId = mGroupLoanTemplate
                        .getTransactionProcessingStrategyOptions().get(position).getId();
                break;
            case R.id.sp_payment_periods:
                loanTermFrequency = mGroupLoanTemplate
                        .getTermFrequencyTypeOptions().get(position).getId();
                break;

            case R.id.sp_loan_purpose:
                loanPurposeId = mGroupLoanTemplate.getLoanPurposeOptions().get(position).getId();
                break;
            case R.id.sp_interest_type:
                interestTypeMethodId = mGroupLoanTemplate.getInterestTypeOptions()
                        .get(position).getId();
                break;
            case R.id.sp_loan_officer:
                loanOfficerId = mGroupLoanTemplate.getLoanOfficerOptions().get(position).getId();
                break;
            case R.id.sp_fund:
                fundId = mGroupLoanTemplate.getFundOptions().get(position).getId();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
