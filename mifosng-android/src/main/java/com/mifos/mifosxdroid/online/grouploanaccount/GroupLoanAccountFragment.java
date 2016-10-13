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
    Spinner sp_lproduct;

    @BindView(R.id.sp_loan_purpose)
    Spinner sp_loan_purpose;

    @BindView(R.id.tv_submittedon_date)
    TextView tv_submittedon_date;

    @BindView(R.id.et_client_external_id)
    EditText et_client_external_id;

    @BindView(R.id.et_nominal_annual)
    EditText et_nominal_annual;

    @BindView(R.id.et_principal)
    EditText et_principal;

    @BindView(R.id.et_loanterm)
    EditText et_loanterm;

    @BindView(R.id.et_numberofrepayments)
    EditText et_numberofrepayments;

    @BindView(R.id.et_repaidevery)
    EditText et_repaidevery;

    @BindView(R.id.sp_payment_periods)
    Spinner sp_payment_periods;

    @BindView(R.id.et_nominal_interest_rate)
    EditText et_nominal_interest_rate;

    @BindView(R.id.sp_amortization)
    Spinner sp_amortization;

    @BindView(R.id.sp_interestcalculationperiod)
    Spinner sp_interestcalculationperiod;

    @BindView(R.id.sp_fund)
    Spinner sp_fund;

    @BindView(R.id.sp_loan_officer)
    Spinner sp_loan_officer;

    @BindView(R.id.sp_interest_type)
    Spinner sp_interest_type;

    @BindView(R.id.sp_repaymentstrategy)
    Spinner sp_repaymentstrategy;

    @BindView(R.id.ck_calculateinterest)
    CheckBox ck_calculateinterest;

    @BindView(R.id.disbursementon_date)
    TextView tv_disbursementon_date;

    @BindView(R.id.bt_loan_submit)
    Button bt_loan_submit;

    @Inject
    GroupLoanAccountPresenter mGroupLoanAccountPresenter;

    GroupLoanTemplate mResponse;

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

    List<String> amortizationType = new ArrayList<>();
    List<String> interestCalculationPeriodType = new ArrayList<>();
    List<String> transactionProcessingStrategy = new ArrayList<>();
    List<String> termFrequencyType = new ArrayList<>();
    List<String> loans = new ArrayList<>();
    List<String> loanPurposeType = new ArrayList<>();
    List<String> interestTypeOptions = new ArrayList<>();
    List<String> loanOfficerOptions = new ArrayList<>();
    List<String> fundOptions = new ArrayList<>();
    List<String> mListLoanProductsNames = new ArrayList<>();

    ArrayAdapter<String> amortizationTypeAdapter;
    ArrayAdapter<String> interestCalculationPeriodTypeAdapter;
    ArrayAdapter<String> transactionProcessingStrategyAdapter;
    ArrayAdapter<String> termFrequencyTypeAdapter;
    ArrayAdapter<String> loanProductAdapter;
    ArrayAdapter<String> loanPurposeTypeAdapter;
    ArrayAdapter<String> interestTypeOptionsAdapter;
    ArrayAdapter<String> loanOfficerOptionsAdapter;
    ArrayAdapter<String> fundOptionsAdapter;

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


        disbursementon_date = tv_disbursementon_date.getText().toString();
        submittion_date = tv_submittedon_date.getText().toString();
        submittion_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (submittion_date).replace("-", " ");
        disbursementon_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (disbursementon_date).replace("-", " ");


        bt_loan_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GroupLoanPayload loansPayload = new GroupLoanPayload();
                loansPayload.setAllowPartialPeriodInterestCalcualtion(ck_calculateinterest
                        .isChecked());
                loansPayload.setAmortizationType(amortizationTypeId);
                loansPayload.setGroupId(groupId);
                loansPayload.setDateFormat("dd MMMM yyyy");
                loansPayload.setExpectedDisbursementDate(disbursementon_date);
                loansPayload.setInterestCalculationPeriodType(interestCalculationPeriodTypeId);
                loansPayload.setLoanType("group");
                loansPayload.setLocale("en");
                loansPayload.setNumberOfRepayments(et_numberofrepayments.getEditableText()
                        .toString());
                loansPayload.setPrincipal(et_principal.getEditableText().toString());
                loansPayload.setProductId(productId);
                loansPayload.setRepaymentEvery(et_repaidevery.getEditableText().toString());
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
        sp_amortization.setAdapter(amortizationTypeAdapter);
        sp_amortization.setOnItemSelectedListener(this);

        interestCalculationPeriodTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, interestCalculationPeriodType);
        interestCalculationPeriodTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_interestcalculationperiod.setAdapter(interestCalculationPeriodTypeAdapter);
        sp_interestcalculationperiod.setOnItemSelectedListener(this);

        transactionProcessingStrategyAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, transactionProcessingStrategy);
        transactionProcessingStrategyAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_repaymentstrategy.setAdapter(transactionProcessingStrategyAdapter);
        sp_repaymentstrategy.setOnItemSelectedListener(this);

        termFrequencyTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, termFrequencyType);
        termFrequencyTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_payment_periods.setAdapter(termFrequencyTypeAdapter);
        sp_payment_periods.setOnItemSelectedListener(this);

        loanProductAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListLoanProductsNames);
        loanProductAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_lproduct.setAdapter(loanProductAdapter);
        sp_lproduct.setOnItemSelectedListener(this);

        loanPurposeTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, loanPurposeType);
        loanPurposeTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_loan_purpose.setAdapter(loanPurposeTypeAdapter);
        sp_loan_purpose.setOnItemSelectedListener(this);

        interestTypeOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, interestTypeOptions);
        interestTypeOptionsAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_interest_type.setAdapter(interestTypeOptionsAdapter);
        sp_interest_type.setOnItemSelectedListener(this);

        loanOfficerOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, loanOfficerOptions);
        loanOfficerOptionsAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_loan_officer.setAdapter(loanOfficerOptionsAdapter);
        sp_loan_officer.setOnItemSelectedListener(this);

        fundOptionsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, fundOptions);
        fundOptionsAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_fund.setAdapter(fundOptionsAdapter);
        sp_fund.setOnItemSelectedListener(this);
    }

    @Override
    public void onDatePicked(String date) {
        tv_submittedon_date.setText(date);
        tv_disbursementon_date.setText(date);

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

        tv_submittedon_date.setText(MFDatePicker.getDatePickedAsString());

        tv_submittedon_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }

    public void inflatedisbusmentDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tv_disbursementon_date.setText(MFDatePicker.getDatePickedAsString());

        tv_disbursementon_date.setOnClickListener(new View.OnClickListener() {
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
                .fillLoanProductSpinner(loans));
        loanProductAdapter.notifyDataSetChanged();
    }


    @Override
    public void showGroupLoanTemplate(GroupLoanTemplate groupLoanTemplate) {
        mGroupLoanTemplate = groupLoanTemplate;
        amortizationType.addAll(mGroupLoanAccountPresenter.fillamortizationSpinnerOptions
                (groupLoanTemplate.getAmortizationTypeOptions()));
        amortizationTypeAdapter.notifyDataSetChanged();

        interestCalculationPeriodType.addAll(mGroupLoanAccountPresenter.
                fillinterestcalculationperiodSpinnerOptions(
                groupLoanTemplate.getInterestCalculationPeriodTypeOptions()));
        interestCalculationPeriodTypeAdapter.notifyDataSetChanged();

        transactionProcessingStrategy.addAll(mGroupLoanAccountPresenter.
                filltransactionProcessingStrategySpinnerOptions
                (groupLoanTemplate.getTransactionProcessingStrategyOptions()));
        transactionProcessingStrategyAdapter.notifyDataSetChanged();

        termFrequencyType.addAll(mGroupLoanAccountPresenter.filltermFrequencyTypeSpinnerOptions
                (groupLoanTemplate.getTermFrequencyTypeOptions()));
        termFrequencyTypeAdapter.notifyDataSetChanged();

        loanPurposeType.addAll(mGroupLoanAccountPresenter.fillloanPurposeTypeSpinnerOptions
                (groupLoanTemplate.getLoanPurposeOptions()));
        loanPurposeTypeAdapter.notifyDataSetChanged();

        interestTypeOptions.addAll(mGroupLoanAccountPresenter.fillinterestTypeOptionsSpinnerOptions
                (groupLoanTemplate.getInterestTypeOptions()));
        interestTypeOptionsAdapter.notifyDataSetChanged();

        loanOfficerOptions.addAll(mGroupLoanAccountPresenter.fillloanOfficerSpinnerOptions
                (groupLoanTemplate.getLoanOfficerOptions()));
        loanOfficerOptionsAdapter.notifyDataSetChanged();

        fundOptions.addAll(mGroupLoanAccountPresenter.fillfundSpinnerOptions
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
