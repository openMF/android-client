/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.ProgressableDialogFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.InterestType;
import com.mifos.objects.accounts.savings.FieldOfficerOptions;
import com.mifos.objects.accounts.savings.LockinPeriodFrequencyType;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.services.data.SavingsPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.SafeUIBlockingUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nellyk on 1/22/2016.
 * <p/>
 * Use this Dialog Fragment to Create and/or Update charges
 */
public class SavingsAccountFragment extends ProgressableDialogFragment implements MFDatePicker.OnDatePickListener {

    public static final String TAG = "SavingsAccountFragment";
    private View rootView;

    @InjectView(R.id.sp_product)
    Spinner sp_product;
    @InjectView(R.id.et_client_external_id)
    EditText et_client_external_id;
    @InjectView(R.id.tv_submittedon_date)
    TextView tv_submittedon_date;
    @InjectView(R.id.et_nominal_annual)
    EditText et_nominal_annual;
    @InjectView(R.id.sp_interest_calc)
    Spinner sp_interest_calc;
    @InjectView(R.id.sp_interest_comp)
    Spinner sp_interest_comp;
    @InjectView(R.id.sp_interest_p_period)
    Spinner sp_interest_p_period;
    @InjectView(R.id.sp_days_in_year)
    Spinner sp_days_in_year;
    @InjectView(R.id.sp_field_officer)
    Spinner sp_field_officer;
    @InjectView(R.id.tv_currency)
    TextView tv_currency;
    @InjectView(R.id.ck_allow_overdraft)
    CheckBox ck_allow_overdraft;
    @InjectView(R.id.ck_withdrawal_fee)
    CheckBox ck_withdrawal_fee;
    @InjectView(R.id.et_overdraft_limit)
    EditText et_overdraft_limit;
    @InjectView(R.id.sp_lock_in_period_frequency)
    Spinner sp_lock_in_period_frequency;
    @InjectView(R.id.et_lock_in_period_duration)
    EditText et_lock_in_period_duration;
    @InjectView(R.id.bt_submit)
    Button bt_submit;
    private DialogFragment mfDatePicker;
    private int productId;
    private int fieldOfficerId;
    private int clientId;
    private int interestCalculationTypeAdapterId;
    private int interestCompoundingPeriodTypeId;
    private int interestPostingPeriodTypeId;
    private int interestCalculationDaysInYearTypeId;
    private int lockInPeriodFrequencyTypeId;
    private boolean allowOverdraft;
    private boolean withdrawalFeeForTransfers;
    private String currencyLabel;
    private String submittion_date;
    private HashMap<String, Integer> savingsNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> fieldOfficerIdHashMap = new HashMap<String, Integer>();
    private SavingProductsTemplate savingproductstemplate = new SavingProductsTemplate();

    public static SavingsAccountFragment newInstance(int clientId) {
        SavingsAccountFragment savingsAccountFragment = new SavingsAccountFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        savingsAccountFragment.setArguments(args);
        return savingsAccountFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_savings_account, null);
        ButterKnife.inject(this, rootView);
        inflatesubmissionDate();
        inflateSavingsSpinner();
        getSavingsAccountTemplateAPI();

        submittion_date = tv_submittedon_date.getText().toString();
        submittion_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload(submittion_date).replace("-", " ");

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SavingsPayload savingsPayload = new SavingsPayload();
                savingsPayload.setExternalId(et_client_external_id.getEditableText().toString());
                savingsPayload.setLocale("en");
                savingsPayload.setSubmittedOnDate(submittion_date);
                savingsPayload.setDateFormat("dd MMMM yyyy");
                savingsPayload.setClientId(clientId);
                savingsPayload.setProductId(productId);
                savingsPayload.setFieldOfficerId(fieldOfficerId);
                savingsPayload.setNominalAnnualInterestRate(et_nominal_annual.getEditableText().toString());
                savingsPayload.setInterestCompoundingPeriodType(interestCompoundingPeriodTypeId);
                savingsPayload.setInterestPostingPeriodType(interestPostingPeriodTypeId);
                savingsPayload.setInterestCalculationType(interestCalculationTypeAdapterId);
                savingsPayload.getInterestCalculationDaysInYearType();
                savingsPayload.setLockinPeriodFrequency(et_lock_in_period_duration.getEditableText().toString());
                savingsPayload.setLockinPeriodFrequencyType(lockInPeriodFrequencyTypeId);

                if(allowOverdraft){
                    savingsPayload.setAllowOverdraft(true);
                    savingsPayload.setOverdraftLimit(et_overdraft_limit.getEditableText().toString());
                }
                if(withdrawalFeeForTransfers){
                    savingsPayload.setWithdrawalFeeForTransfers(true);
                }

                initiateSavingCreation(savingsPayload);
            }
        });
        return rootView;
    }

    @Override
    public void onDatePicked(String date) {
        tv_submittedon_date.setText(date);
    }

    private void inflateSavingsSpinner() {
        showProgress(true);
        App.apiManager.getSavingsAccounts(new Callback<List<ProductSavings>>() {

            @Override
            public void success(List<ProductSavings> savings, Response response) {
                /* Activity is null - Fragment has been detached; no need to do anything. */
                if (getActivity() == null) return;

                final List<String> savingsList = new ArrayList<String>();

                for (ProductSavings savingsname : savings) {
                    savingsList.add(savingsname.getName());
                    savingsNameIdHashMap.put(savingsname.getName(), savingsname.getId());
                }
                ArrayAdapter<String> savingsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, savingsList);
                savingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_product.setAdapter(savingsAdapter);
                sp_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        productId = savingsNameIdHashMap.get(savingsList.get(i));
                        inflateCurrencyTextView(productId);
                        inflateFieldOfficerSpinner();
                        Log.d("productId " + savingsList.get(i), String.valueOf(productId));
                        if (productId != -1) {
                        }
                        else {
                            Toast.makeText(getActivity(), getString(R.string.error_select_product), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                showProgress(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
            }
        });
    }

    private void inflateInterestPostingPeriodType() {

        final ArrayList<String> InterestPostingPeriodTypeNames = filterListObject
                (savingproductstemplate.getInterestPostingPeriodTypeOptions());

        final ArrayAdapter<String> interestPostingPeriodTypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, InterestPostingPeriodTypeNames);
        interestPostingPeriodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_interest_p_period.setAdapter(interestPostingPeriodTypeAdapter);
        sp_interest_p_period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestPostingPeriodTypeId = savingproductstemplate.getInterestPostingPeriodTypeOptions().get(i).getId();
                Log.d("interestPosting " + InterestPostingPeriodTypeNames.get(i), String.valueOf(interestPostingPeriodTypeId));
                if (interestPostingPeriodTypeId != -1) {


                }
                else {

                    Toast.makeText(getActivity(), getString(R.string.interestPostingPeriodTypeId), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void inflateInterestCalculationTypeSpinner() {

        final ArrayList<String> interestCalculationTypeNames = filterListObject
                (savingproductstemplate.getInterestCalculationTypeOptions());
        final ArrayAdapter<String> interestCalculationTypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, interestCalculationTypeNames);
        interestCalculationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_interest_calc.setAdapter(interestCalculationTypeAdapter);
        sp_interest_calc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestCalculationTypeAdapterId = savingproductstemplate.getInterestCalculationTypeOptions().get(i).getId();
                Log.d("interestCalculation " + interestCalculationTypeNames.get(i), String.valueOf(interestCalculationTypeAdapterId));
                if (interestCalculationTypeAdapterId != -1) {


                }
                else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_interestCalculationType), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void inflateinterestCalculationDaysInYearType() {

        final ArrayList<String> InterestCalculationDaysInYearTypeNames = filterListObject
                (savingproductstemplate.getInterestCalculationDaysInYearTypeOptions());

        final ArrayAdapter<String> interestCalculationDaysInYearTypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, InterestCalculationDaysInYearTypeNames);
        interestCalculationDaysInYearTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_days_in_year.setAdapter(interestCalculationDaysInYearTypeAdapter);
        sp_days_in_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestCalculationDaysInYearTypeId = savingproductstemplate
                        .getInterestCalculationDaysInYearTypeOptions().get(i).getId();
                Log.d("interestCalculationD" + InterestCalculationDaysInYearTypeNames.get(i), String.valueOf(interestCalculationDaysInYearTypeId));
                if (interestCalculationDaysInYearTypeId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string.interestCalculationDaysInYearTypeAdapterId), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void InterestCompoundingPeriodType() {

        final ArrayList<String> InterestCompoundingPeriodType = filterListObject
                (savingproductstemplate.getInterestCompoundingPeriodTypeOptions());

        final ArrayAdapter<String> interestCompoundingPeriodTypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, InterestCompoundingPeriodType);
        interestCompoundingPeriodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_interest_comp.setAdapter(interestCompoundingPeriodTypeAdapter);
        sp_interest_comp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestCompoundingPeriodTypeId = savingproductstemplate
                        .getInterestCompoundingPeriodTypeOptions().get(i).getId();
                Log.d("clientTypeId " + InterestCompoundingPeriodType.get(i), String.valueOf(interestCompoundingPeriodTypeId));
                if (interestCompoundingPeriodTypeId != -1) {


                }
                else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_interest_cmp), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void inflateFieldOfficerSpinner() {
        showProgress(true);
        App.apiManager.getSavingsAccountTemplate(clientId, productId,new Callback<Response>() {

            @Override
            public void success(final Response result, Response response) {
                /* Activity is null - Fragment has been detached; no need to do anything. */
                if (getActivity() == null) return;
                Log.d(TAG, "");

                final List<FieldOfficerOptions> fieldOfficerOptionsList = new ArrayList<>();
                // you can use this array to populate your spinner
                final ArrayList<String> fieldOfficerNames = new ArrayList<String>();
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
                    if (obj.has("fieldOfficerOptions")) {
                        JSONArray fieldOfficerTypes = obj.getJSONArray("fieldOfficerOptions");

                        for (int i = 0; i < fieldOfficerTypes.length(); i++) {
                            JSONObject fieldOfficerObject = fieldOfficerTypes.getJSONObject(i);
                            FieldOfficerOptions officer = new FieldOfficerOptions();
                            officer.setId(fieldOfficerObject.optInt("id"));
                            officer.setDisplayName(fieldOfficerObject.optString("displayName"));
                            fieldOfficerOptionsList.add(officer);
                            fieldOfficerNames.add(fieldOfficerObject.optString("displayName"));
                            fieldOfficerIdHashMap.put(officer.getDisplayName(), officer.getId());
                        }
                    }
                    String stringResult = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                final ArrayAdapter<String> FieldOfficerOptionsAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, fieldOfficerNames);
                FieldOfficerOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_field_officer.setAdapter(FieldOfficerOptionsAdapter);
                sp_field_officer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        fieldOfficerId = fieldOfficerIdHashMap.get(fieldOfficerNames.get(i));
                        Log.d("fieldOfficerID " + fieldOfficerNames.get(i), String.valueOf(fieldOfficerId));
                        if (fieldOfficerId != -1) {

                        }
                        else {
                            Toast.makeText(getActivity(), getString(R.string.error_select_field_officer), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                showProgress(false);

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

                showProgress(false);

            }
        });
    }

    public void inflateCurrencyTextView(int savingsProductId) {
        showProgress(true);
        App.apiManager.getSavingsAccountTemplate(clientId, savingsProductId,new Callback<Response>() {

            @Override
            public void success(final Response result, Response response) {
                /* Activity is null - Fragment has been detached; no need to do anything. */
                if (getActivity() == null) return;
                Log.d(TAG, "");

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
                    currencyLabel = "Currency: " + obj.getJSONObject("currency").getString("displayLabel");
                    Log.d(TAG, "The currency text view label is \'".concat(currencyLabel).concat("\'"));

                    String stringResult = sb.toString();

                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                tv_currency.setText(currencyLabel);
                showProgress(false);

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

                showProgress(false);

            }
        });
    }

    private void inflateLockInPeriodFrequencyType() {

        final ArrayList<String> LockinPeriodFrequencyType = filterListObject
                (savingproductstemplate.getLockinPeriodFrequencyTypeOptions());

        final ArrayAdapter<String> lockInPeriodFrequencyTypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, LockinPeriodFrequencyType);
        lockInPeriodFrequencyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_lock_in_period_frequency.setAdapter(lockInPeriodFrequencyTypeAdapter);
        sp_lock_in_period_frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lockInPeriodFrequencyTypeId = savingproductstemplate
                        .getLockinPeriodFrequencyTypeOptions().get(i).getId();
                Log.d("lockInPeriodFrequency" + LockinPeriodFrequencyType.get(i), String.valueOf(lockInPeriodFrequencyTypeId));
                if (lockInPeriodFrequencyTypeId != -1) {


                }
                else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_lockInPeriodFrequencyType), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void inflateAllowOverdraftCheckbox(){
        et_overdraft_limit.setVisibility(View.GONE);
        ck_allow_overdraft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    et_overdraft_limit.setVisibility(View.GONE);
                    allowOverdraft = false;
                }
                else{
                    et_overdraft_limit.setVisibility(View.VISIBLE);
                    allowOverdraft = true;
                }
            }

            });
    }

    private void inflateTransferWithdrawalFeeCheckbox(){
        ck_withdrawal_fee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                withdrawalFeeForTransfers = isChecked;
            }
        });
    }

    private void initiateSavingCreation(SavingsPayload savingsPayload) {
        showProgress(true);

        App.apiManager.createSavingsAccount(savingsPayload, new Callback<Savings>() {
            @Override
            public void success(Savings savings, Response response) {
                showProgress(false);
                Toast.makeText(getActivity(), "The Savings Account has been submitted for Approval", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                showProgress(false);
                Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void inflatesubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tv_submittedon_date.setText(MFDatePicker.getDatePickedAsString());

        tv_submittedon_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }
        });

    }

    private void getSavingsAccountTemplateAPI() {
        showProgress(true);
        App.apiManager.getSavingsAccountTemplate(new Callback<SavingProductsTemplate>() {
            @Override
            public void success(SavingProductsTemplate savingProductsTemplate, Response response) {
                /* Activity is null - Fragment has been detached; no need to do anything. */
                if (getActivity() == null) return;

                if (response.getStatus() == 200) {
                    savingproductstemplate = savingProductsTemplate;
                    InterestCompoundingPeriodType();
                    inflateinterestCalculationDaysInYearType();
                    inflateInterestCalculationTypeSpinner();
                    inflateInterestPostingPeriodType();
                    inflateLockInPeriodFrequencyType();
                    inflateFieldOfficerSpinner();
                    inflateAllowOverdraftCheckbox();
                    inflateTransferWithdrawalFeeCheckbox();
                }

                    showProgress(false);
            }


            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.getLocalizedMessage());

                showProgress(false);
            }
        });

    }

    private ArrayList<String> filterListObject(List<InterestType> interestTypes) {

        ArrayList<String> InterestValueList = new ArrayList<>();
        for (InterestType interestType : interestTypes) {
            InterestValueList.add(interestType.getValue());
        }

        return InterestValueList;
    }

}
