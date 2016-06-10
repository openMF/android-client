/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.grouploanaccount;

import android.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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
import com.mifos.objects.accounts.loan.AmortizationType;
import com.mifos.objects.accounts.loan.InterestCalculationPeriodType;
import com.mifos.objects.accounts.loan.LoanPurposeOptions;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.accounts.loan.TermFrequencyTypeOptions;
import com.mifos.objects.accounts.loan.TransactionProcessingStrategy;
import com.mifos.objects.organisation.ProductLoans;
import com.mifos.services.data.GroupLoanPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit.client.Response;

/**
 * Created by nellyk on 1/22/2016.
 * <p/>
 * Use this  Fragment to Create and/or Update loan
 */
public class GroupLoanAccountFragment extends ProgressableDialogFragment
        implements MFDatePicker.OnDatePickListener, GroupLoanAccountMvpView {

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

    Response mResponse;

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

    private HashMap<String, Integer> loansNameIdHashMap = new HashMap<>();
    private HashMap<String, Integer> termFrequencyTypeIdHashMap = new HashMap<>();
    private HashMap<String, Integer> loanPurposeNameIdHashMap = new HashMap<>();
    private HashMap<String, Integer> interestCalculationPeriodTypeIdHashMap = new HashMap<>();
    private HashMap<String, Integer> amortizationTypeIdHashMap = new HashMap<>();
    private HashMap<String, Integer> transactionProcessingStrategyTypeIdHashMap = new HashMap<>();


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

    @Override
    public void onDatePicked(String date) {
        tv_submittedon_date.setText(date);
        tv_disbursementon_date.setText(date);

    }

    private void inflateLoansProductSpinner() {
        mGroupLoanAccountPresenter.loadAllLoans();
    }


    private void inflateAmortizationSpinner(Response result) {

        final List<AmortizationType> amortizationType = new ArrayList<>();
        // you can use this array to populate your spinner
        final ArrayList<String> amortizationTypeNames = new ArrayList<String>();
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            if (obj.has("amortizationTypeOptions")) {
                JSONArray amortizationTypes = obj.getJSONArray("amortizationTypeOptions");
                for (int i = 0; i < amortizationTypes.length(); i++) {
                    JSONObject amortizationTypeObject = amortizationTypes.getJSONObject(i);
                    AmortizationType amortization = new AmortizationType();
                    amortization.setId(amortizationTypeObject.optInt("id"));
                    amortization.setValue(amortizationTypeObject.optString("value"));
                    amortizationType.add(amortization);
                    amortizationTypeNames.add(amortizationTypeObject.optString("value"));
                    amortizationTypeIdHashMap.put(amortization.getValue(), amortization
                            .getId());
                }

            }
            String stringResult = sb.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        final ArrayAdapter<String> amortizationTypeAdapter =
                new ArrayAdapter<>(getActivity(),
                        layout.simple_spinner_item, amortizationTypeNames);
        amortizationTypeAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        sp_amortization.setAdapter(amortizationTypeAdapter);
        sp_amortization.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                    l) {
                amortizationTypeId = amortizationTypeIdHashMap.get(amortizationTypeNames
                        .get(i));
                Log.d("ammortization" + amortizationTypeNames.get(i), String.valueOf
                        (amortizationTypeId));
                if (amortizationTypeId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_fund),
                            Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void inflateLoanPurposeSpinner() {
        mGroupLoanAccountPresenter.loadGroupLoansAccountTemplate(groupId, productId);
    }

    private void inflateInterestCalculationPeriodSpinner(Response result) {

        final List<InterestCalculationPeriodType> interestCalculationPeriodType = new
                ArrayList<>();
        // you can use this array to populate your spinner
        final ArrayList<String> interestCalculationPeriodTypeNames = new
                ArrayList<String>();
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            if (obj.has("interestCalculationPeriodTypeOptions")) {
                JSONArray interestCalculationPeriodTypes = obj.getJSONArray
                        ("interestCalculationPeriodTypeOptions");
                for (int i = 0; i < interestCalculationPeriodTypes.length(); i++) {
                    JSONObject interestCalculationPeriodTypeObject =
                            interestCalculationPeriodTypes.getJSONObject(i);
                    InterestCalculationPeriodType interestCalculationPeriod = new
                            InterestCalculationPeriodType();
                    interestCalculationPeriod.setId(interestCalculationPeriodTypeObject
                            .optInt("id"));
                    interestCalculationPeriod
                            .setValue(interestCalculationPeriodTypeObject.optString("value"));
                    interestCalculationPeriodType.add(interestCalculationPeriod);
                    interestCalculationPeriodTypeNames
                            .add(interestCalculationPeriodTypeObject.optString("value"));
                    interestCalculationPeriodTypeIdHashMap.put(interestCalculationPeriod
                            .getValue(), interestCalculationPeriod.getId());
                }

            }
            String stringResult = sb.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        final ArrayAdapter<String> interestCalculationPeriodTypeAdapter =
                new ArrayAdapter<>(getActivity(),
                        layout.simple_spinner_item,
                        interestCalculationPeriodTypeNames);

        interestCalculationPeriodTypeAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        sp_interestcalculationperiod.setAdapter(interestCalculationPeriodTypeAdapter);
        sp_interestcalculationperiod.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                    l) {
                interestCalculationPeriodTypeId = interestCalculationPeriodTypeIdHashMap
                        .get(interestCalculationPeriodTypeNames.get(i));
                Log.d("interestCalculation " + interestCalculationPeriodTypeNames.get(i),
                        String.valueOf(interestCalculationPeriodTypeId));
                if (interestCalculationPeriodTypeId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string
                            .error_select_interestCalculationPeriod), Toast.LENGTH_SHORT)
                            .show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void inflatetransactionProcessingStrategySpinner(Response result) {

        final List<TransactionProcessingStrategy> transactionProcessingStrategyType = new
                ArrayList<>();
        // you can use this array to populate your spinner
        final ArrayList<String> transactionProcessingStrategyTypeNames = new
                ArrayList<String>();
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            if (obj.has("transactionProcessingStrategyOptions")) {
                JSONArray transactionProcessingStrategyTypes = obj.getJSONArray
                        ("transactionProcessingStrategyOptions");
                for (int i = 0; i < transactionProcessingStrategyTypes.length(); i++) {
                    JSONObject transactionProcessingStrategyTypeObject =
                            transactionProcessingStrategyTypes.getJSONObject(i);
                    TransactionProcessingStrategy transactionProcessingStrategy = new
                            TransactionProcessingStrategy();
                    transactionProcessingStrategy
                            .setId(transactionProcessingStrategyTypeObject.optInt("id"));
                    transactionProcessingStrategy
                            .setName(transactionProcessingStrategyTypeObject.optString("name"));
                    transactionProcessingStrategyType.add(transactionProcessingStrategy);
                    transactionProcessingStrategyTypeNames
                            .add(transactionProcessingStrategyTypeObject.optString("name"));
                    transactionProcessingStrategyTypeIdHashMap
                            .put(transactionProcessingStrategy.getName(),
                                    transactionProcessingStrategy.getId());
                }

            }
            String stringResult = sb.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        final ArrayAdapter<String> transactionProcessingStrategyAdapter =
                new ArrayAdapter<>(getActivity(),
                        layout.simple_spinner_item,
                        transactionProcessingStrategyTypeNames);

        transactionProcessingStrategyAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);

        sp_repaymentstrategy.setAdapter(transactionProcessingStrategyAdapter);
        sp_repaymentstrategy.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                    l) {
                transactionProcessingStrategyId =
                        transactionProcessingStrategyTypeIdHashMap.get
                                (transactionProcessingStrategyTypeNames.get(i));
                Log.d("transactionProcessing " + transactionProcessingStrategyTypeNames
                        .get(i), String.valueOf(transactionProcessingStrategyId));
                if (transactionProcessingStrategyId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string
                            .error_select_transactionProcessingStrategy), Toast
                            .LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void inflateFrequencyPeriodSpinner(Response result) {

        final List<TermFrequencyTypeOptions> termFrequencyType = new ArrayList<>();
        // you can use this array to populate your spinner
        final ArrayList<String> termFrequencyTypeNames = new ArrayList<String>();
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            if (obj.has("termFrequencyTypeOptions")) {
                JSONArray termFrequencyTypes = obj.getJSONArray("termFrequencyTypeOptions");
                for (int i = 0; i < termFrequencyTypes.length(); i++) {
                    JSONObject termFrequencyTypeObject = termFrequencyTypes.getJSONObject
                            (i);
                    TermFrequencyTypeOptions termFrequency = new TermFrequencyTypeOptions();
                    termFrequency.setId(termFrequencyTypeObject.optInt("id"));
                    termFrequency.setValue(termFrequencyTypeObject.optString("value"));
                    termFrequencyType.add(termFrequency);
                    termFrequencyTypeNames.add(termFrequencyTypeObject.optString("value"));
                    termFrequencyTypeIdHashMap.put(termFrequency.getValue(),
                            termFrequency.getId());
                }

            }
            String stringResult = sb.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        final ArrayAdapter<String> termFrequencyTypeAdapter =
                new ArrayAdapter<>(getActivity(),
                        layout.simple_spinner_item, termFrequencyTypeNames);
        termFrequencyTypeAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        sp_payment_periods.setAdapter(termFrequencyTypeAdapter);
        sp_payment_periods.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int i, long l) {

                        loanTermFrequency = termFrequencyTypeIdHashMap.get
                                (termFrequencyTypeNames.get(i));
                        Log.d("termFrequencyTypeId" + termFrequencyTypeNames.get(i),
                                String.valueOf(loanTermFrequency));
                        if (loanTermFrequency != -1) {


                        } else {

                            Toast.makeText(getActivity(), getString(R.string
                                            .error_select_fund),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
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
    public void showAllLoans(List<ProductLoans> loans) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        final List<String> loansList = new ArrayList<String>();
        for (ProductLoans loansname : loans) {
            loansList.add(loansname.getName());
            loansNameIdHashMap.put(loansname.getName(), loansname.getId());
        }
        ArrayAdapter<String> loansAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, loansList);
        loansAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_lproduct.setAdapter(loansAdapter);
        sp_lproduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                    l) {
                productId = loansNameIdHashMap.get(loansList.get(i));
                Log.d("productId " + loansList.get(i), String.valueOf(productId));
                if (productId != -1) {

                    inflateLoanPurposeSpinner();

                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_loan),
                            Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showLoanPurposeSpinner(Response result) {

        mResponse = result;

        inflateFrequencyPeriodSpinner(mResponse);
        inflateAmortizationSpinner(mResponse);
        inflateInterestCalculationPeriodSpinner(mResponse);
        //inflaterepaymentFrequencyTypeOptionsSpinner();
        inflatetransactionProcessingStrategySpinner(mResponse);

        final List<LoanPurposeOptions> loanPurposeOptionsType = new ArrayList<>();
        // you can use this array to populate your spinner
        final ArrayList<String> loanPurposeOptionsTypeNames = new ArrayList<String>();
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            if (obj.has("loanPurposeOptions")) {
                JSONArray loanPurposeOptionsTypes = obj.getJSONArray("loanPurposeOptions");
                for (int i = 0; i < loanPurposeOptionsTypes.length(); i++) {
                    JSONObject loanPurposeOptionsTypesObject = loanPurposeOptionsTypes
                            .getJSONObject(i);
                    LoanPurposeOptions loanpurpose = new LoanPurposeOptions();
                    loanpurpose.setId(loanPurposeOptionsTypesObject.optInt("id"));
                    loanpurpose.setName(loanPurposeOptionsTypesObject.optString("name"));
                    loanPurposeOptionsType.add(loanpurpose);
                    loanPurposeOptionsTypeNames.add(loanPurposeOptionsTypesObject
                            .optString("name"));
                    loanPurposeNameIdHashMap.put(loanpurpose.getName(), loanpurpose.getId
                            ());
                }

            }
            String stringResult = sb.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        final ArrayAdapter<String> loanPTypeAdapter =
                new ArrayAdapter<>(getActivity(), layout.simple_spinner_item,
                        loanPurposeOptionsTypeNames);

        loanPTypeAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_loan_purpose.setAdapter(loanPTypeAdapter);
        sp_loan_purpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                    l) {
                loanPurposeId = loanPurposeNameIdHashMap.get(loanPurposeOptionsTypeNames
                        .get(i));
                Log.d("loanpurpose" + loanPurposeOptionsTypeNames.get(i), String.valueOf
                        (loanPurposeId));
                if (loanPurposeId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_fund),
                            Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
}
