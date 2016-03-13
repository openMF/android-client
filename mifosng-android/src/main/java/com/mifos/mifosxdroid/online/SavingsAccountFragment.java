/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.InterestType;
import com.mifos.objects.accounts.savings.InterestCalculationType;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.InterestCalculationDaysInYearType;
import com.mifos.objects.organisation.InterestCompoundingPeriod;
import com.mifos.objects.organisation.InterestPostingPeriodType;
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
public class SavingsAccountFragment extends DialogFragment implements MFDatePicker.OnDatePickListener {

    public static final String TAG = "SavingsAccountFragment";
    private View rootView;
    private SafeUIBlockingUtility safeUIBlockingUtility;

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
    @InjectView(R.id.bt_submit)
    Button bt_submit;
    private DialogFragment mfDatePicker;
    private int productId;
    private int clientId;
    private int interestCalculationTypeAdapterId;
    private int interestCompoundingPeriodTypeId;
    private int interestPostingPeriodTypeId;
    private int interestCalculationDaysInYearTypeId;
    private String submittion_date;
    private HashMap<String, Integer> savingsNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> interestCalculationTypeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> interestPostingPeriodTypeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> interestCompoundingPeriodTypeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> interestCalculationDaysInYearHashMap = new HashMap<String, Integer>();
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
        InterestCompoundingPeriodType();
        inflateInterestCalculationTypeSpinner();
        inflateInterestPostingPeriodType();
        inflateinterestCalculationDaysInYearType();

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
                savingsPayload.setNominalAnnualInterestRate(et_nominal_annual.getEditableText().toString());
                savingsPayload.setInterestCompoundingPeriodType(interestCompoundingPeriodTypeId);
                savingsPayload.setInterestPostingPeriodType(interestPostingPeriodTypeId);
                savingsPayload.setInterestCalculationType(interestCalculationTypeAdapterId);
                savingsPayload.getInterestCalculationDaysInYearType();

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
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        App.apiManager.getSavingsAccounts(new Callback<List<ProductSavings>>()
        {

	        @Override
	        public void success(List<ProductSavings> savings, Response response)
	        {
		        final List<String> savingsList = new ArrayList<String>();

		        for (ProductSavings savingsname : savings)
		        {
			        savingsList.add(savingsname.getName());
			        savingsNameIdHashMap.put(savingsname.getName(), savingsname.getId());
		        }
		        ArrayAdapter<String> savingsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, savingsList);
		        savingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        sp_product.setAdapter(savingsAdapter);
		        sp_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		        {
			        @Override
			        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
			        {
				        productId = savingsNameIdHashMap.get(savingsList.get(i));
				        Log.d("productId " + savingsList.get(i), String.valueOf(productId));
				        if (productId != -1)
				        {
				        }
				        else
				        {
					        Toast.makeText(getActivity(), getString(R.string.error_select_product), Toast.LENGTH_SHORT).show();
				        }
			        }

			        @Override
			        public void onNothingSelected(AdapterView<?> parent)
			        {

			        }
		        });
		        safeUIBlockingUtility.safelyUnBlockUI();
	        }

	        @Override
	        public void failure(RetrofitError retrofitError)
	        {
		        safeUIBlockingUtility.safelyUnBlockUI();
	        }
        });
    }

    private void inflateInterestPostingPeriodType() {
        App.apiManager.getSavingsAccountTemplate(new Callback<Response>()
        {

	        @Override
	        public void success(final Response result, Response response)
	        {
		        final List<InterestPostingPeriodType> interestPostingPeriodType = new ArrayList<InterestPostingPeriodType>();
		        // you can use this array to populate your spinner
		        final ArrayList<String> InterestPostingPeriodTypeNames = new ArrayList<String>();
		        //Try to get response body
		        BufferedReader reader = null;
		        StringBuilder sb = new StringBuilder();
		        try
		        {
			        reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
			        String line;

			        while ((line = reader.readLine()) != null)
			        {
				        sb.append(line);
			        }

			        JSONObject obj = new JSONObject(sb.toString());
			        if (obj.has("interestPostingPeriodTypeOptions"))
			        {
				        JSONArray interestPostingPeriodTypes = obj.getJSONArray("interestPostingPeriodTypeOptions");
				        for (int i = 0; i < interestPostingPeriodTypes.length(); i++)
				        {
					        JSONObject interestPostingPeriodTypesObject = interestPostingPeriodTypes.getJSONObject(i);
					        InterestPostingPeriodType interestPostingPeriod = new InterestPostingPeriodType();
					        interestPostingPeriod.setId(interestPostingPeriodTypesObject.optInt("id"));
					        interestPostingPeriod.setValue(interestPostingPeriodTypesObject.optString("value"));
					        interestPostingPeriodType.add(interestPostingPeriod);
					        InterestPostingPeriodTypeNames.add(interestPostingPeriodTypesObject.optString("value"));
					        interestPostingPeriodTypeNameIdHashMap.put(interestPostingPeriod.getValue(), interestPostingPeriod.getId());
				        }


			        }
			        String stringResult = sb.toString();
		        }
		        catch (Exception e)
		        {
			        Log.e(TAG, "", e);
		        }
		        final ArrayAdapter<String> interestPostingPeriodTypeAdapter = new ArrayAdapter<String>(getActivity(),
				        android.R.layout.simple_spinner_item, InterestPostingPeriodTypeNames);
		        interestPostingPeriodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        sp_interest_p_period.setAdapter(interestPostingPeriodTypeAdapter);
		        sp_interest_p_period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		        {

			        @Override
			        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
			        {
				        interestPostingPeriodTypeId = interestPostingPeriodTypeNameIdHashMap.get(InterestPostingPeriodTypeNames.get(i));
				        Log.d("interestPosting " + InterestPostingPeriodTypeNames.get(i), String.valueOf(interestPostingPeriodTypeId));
				        if (interestPostingPeriodTypeId != -1)
				        {


				        }
				        else
				        {

					        Toast.makeText(getActivity(), getString(R.string.interestPostingPeriodTypeId), Toast.LENGTH_SHORT).show();

				        }

			        }

			        @Override
			        public void onNothingSelected(AdapterView<?> parent)
			        {

			        }
		        });

		        safeUIBlockingUtility.safelyUnBlockUI();

	        }

	        @Override
	        public void failure(RetrofitError retrofitError)
	        {

		        System.out.println(retrofitError.getLocalizedMessage());

		        safeUIBlockingUtility.safelyUnBlockUI();
	        }
        });

    }

    private void inflateInterestCalculationTypeSpinner() {
        App.apiManager.getSavingsAccountTemplate(new Callback<Response>() {
            @Override

            public void success(final Response result, Response response) {
                Log.d(TAG, "");

                final List<InterestCalculationType> interestCalculationType = new ArrayList<>();
                // you can use this array to populate your spinner
                final ArrayList<String> interestCalculationTypeNames = new ArrayList<String>();
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
                    if (obj.has("interestCalculationTypeOptions")) {
                        JSONArray interestCalculationTypes = obj.getJSONArray("interestCalculationTypeOptions");
                        for (int i = 0; i < interestCalculationTypes.length(); i++) {
                            JSONObject interestCalculationTypesObject = interestCalculationTypes.getJSONObject(i);
                            InterestCalculationType interestCalculationTypet = new InterestCalculationType();
                            interestCalculationTypet.setId(interestCalculationTypesObject.optInt("id"));
                            interestCalculationTypet.setValue(interestCalculationTypesObject.optString("value"));
                            interestCalculationType.add(interestCalculationTypet);
                            interestCalculationTypeNames.add(interestCalculationTypesObject.optString("value"));
                            interestCalculationTypeNameIdHashMap.put(interestCalculationTypet.getValue(), interestCalculationTypet.getId());
                        }


                    }
                    String stringResult = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                final ArrayAdapter<String> interestCalculationTypeAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, interestCalculationTypeNames);
                interestCalculationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_interest_calc.setAdapter(interestCalculationTypeAdapter);
                sp_interest_calc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        interestCalculationTypeAdapterId = interestCalculationTypeNameIdHashMap.get(interestCalculationTypeNames.get(i));
                        Log.d("interestCalculation " + interestCalculationTypeNames.get(i), String.valueOf(interestCalculationTypeAdapterId));
                        if (interestCalculationTypeAdapterId != -1) {


                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });
    }

    private void inflateinterestCalculationDaysInYearType() {
        App.apiManager.getSavingsAccountTemplate(new Callback<Response>() {
            @Override

            public void success(final Response result, Response response) {
                Log.d(TAG, "");

                final List<InterestCalculationDaysInYearType> interestCalculationDaysInYearType = new ArrayList<InterestCalculationDaysInYearType>();
                // you can use this array to populate your spinner
                final ArrayList<String> InterestCalculationDaysInYearTypeNames = new ArrayList<String>();
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
                    if (obj.has("interestCalculationDaysInYearTypeOptions")) {
                        JSONArray interestCalculationDaysInYearTypes = obj.getJSONArray("interestCalculationDaysInYearTypeOptions");
                        for (int i = 0; i < interestCalculationDaysInYearTypes.length(); i++) {
                            JSONObject interestCalculationDaysInYearTypeObject = interestCalculationDaysInYearTypes.getJSONObject(i);
                            InterestCalculationDaysInYearType interestCalculationDaysInYearT = new InterestCalculationDaysInYearType();
                            interestCalculationDaysInYearT.setId(interestCalculationDaysInYearTypeObject.optInt("id"));
                            interestCalculationDaysInYearT.setValue(interestCalculationDaysInYearTypeObject.optString("value"));
                            interestCalculationDaysInYearType.add(interestCalculationDaysInYearT);
                            InterestCalculationDaysInYearTypeNames.add(interestCalculationDaysInYearTypeObject.optString("value"));
                            interestCalculationDaysInYearHashMap.put(interestCalculationDaysInYearT.getValue(), interestCalculationDaysInYearT.getId());
                        }


                    }
                    String stringResult = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                final ArrayAdapter<String> interestCalculationDaysInYearTypeAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, InterestCalculationDaysInYearTypeNames);
                interestCalculationDaysInYearTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_days_in_year.setAdapter(interestCalculationDaysInYearTypeAdapter);
                sp_days_in_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        interestCalculationDaysInYearTypeId = interestCalculationDaysInYearHashMap.get(InterestCalculationDaysInYearTypeNames.get(i));
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

                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });

    }

    private void InterestCompoundingPeriodType() {

        App.apiManager.getSavingsAccountTemplate(new Callback<Response>() {
            @Override

            public void success(final Response result, Response response) {
                Log.d(TAG, "");

                final List<InterestCompoundingPeriod> interestCompoundingPeriodTypeList = new ArrayList<InterestCompoundingPeriod>();
                // you can use this array to populate your spinner
                final ArrayList<String> InterestCompoundingPeriodType = new ArrayList<String>();
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
                    if (obj.has("interestCompoundingPeriodTypeOptions")) {
                        JSONArray interestCompoundingPeriodType = obj.getJSONArray("interestCompoundingPeriodTypeOptions");
                        for (int i = 0; i < interestCompoundingPeriodType.length(); i++) {
                            JSONObject interestCompoundingPeriodTypeObject = interestCompoundingPeriodType.getJSONObject(i);
                            InterestCompoundingPeriod interestCompoundingPeriodTypes = new InterestCompoundingPeriod();
                            interestCompoundingPeriodTypes.setId(interestCompoundingPeriodTypeObject.optInt("id"));
                            interestCompoundingPeriodTypes.setValue(interestCompoundingPeriodTypeObject.optString("value"));
                            interestCompoundingPeriodTypeList.add(interestCompoundingPeriodTypes);
                            InterestCompoundingPeriodType.add(interestCompoundingPeriodTypeObject.optString("value"));
                            interestCompoundingPeriodTypeNameIdHashMap.put(interestCompoundingPeriodTypes.getValue(), interestCompoundingPeriodTypes.getId());
                        }


                    }
                    String stringResult = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                final ArrayAdapter<String> interestCompoundingPeriodTypeAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, InterestCompoundingPeriodType);
                interestCompoundingPeriodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_interest_comp.setAdapter(interestCompoundingPeriodTypeAdapter);
                sp_interest_comp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        interestCompoundingPeriodTypeId = interestCompoundingPeriodTypeNameIdHashMap.get(InterestCompoundingPeriodType.get(i));
                        Log.d("clientTypeId " + InterestCompoundingPeriodType.get(i), String.valueOf(interestCompoundingPeriodTypeId));
                        if (interestCompoundingPeriodTypeId != -1) {


                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_intrested_cmp), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });

    }

    private void initiateSavingCreation(SavingsPayload savingsPayload) {

        safeUIBlockingUtility.safelyBlockUI();

        App.apiManager.createSavingsAccount(savingsPayload, new Callback<Savings>()
        {
	        @Override
	        public void success(Savings savings, Response response)
	        {
		        safeUIBlockingUtility.safelyUnBlockUI();
		        Toast.makeText(getActivity(), "The Savings Account has been submitted for Approval", Toast.LENGTH_LONG).show();
	        }

	        @Override
	        public void failure(RetrofitError error)
	        {
		        safeUIBlockingUtility.safelyUnBlockUI();
		        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
	        }
        });


    }

    public void inflatesubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tv_submittedon_date.setText(MFDatePicker.getDatePickedAsString());

        tv_submittedon_date.setOnClickListener(new View.OnClickListener()
        {
	        @Override
	        public void onClick(View view)
	        {
		        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
	        }
        });

    }

	private void getSavingsAccountTemplateAPI(){

		App.apiManager.getSavingsAccountTemplateTemp(new Callback<SavingProductsTemplate>()
		{
			@Override
			public void success(SavingProductsTemplate savingProductsTemplate, Response response)
			{
				if (response.getStatus() == 200)
				{
					savingproductstemplate = savingProductsTemplate;
				}
			}

			@Override
			public void failure(RetrofitError error)
			{
				System.out.println(error.getLocalizedMessage());

				safeUIBlockingUtility.safelyUnBlockUI();
			}
		});

	}

	private List filterListObject(SavingProductsTemplate savingProductsTemplate ) {

		List<String> InterestValueList = new ArrayList<>();
		for(InterestType interestType : savingProductsTemplate.getInterestCompoundingPeriodTypeOptions()) {
			InterestValueList.add(interestType.getValue());
		}

		return InterestValueList;
	}
}
