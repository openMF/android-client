/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.savingsaccount;

import android.R.layout;
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

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableDialogFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.InterestType;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.services.data.SavingsPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nellyk on 1/22/2016.
 * <p/>
 * Use this Dialog Fragment to Create and/or Update charges
 */
public class SavingsAccountFragment extends ProgressableDialogFragment
        implements MFDatePicker.OnDatePickListener, SavingsAccountMvpView {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.sp_product)
    Spinner sp_product;

    @BindView(R.id.et_client_external_id)
    EditText et_client_external_id;

    @BindView(R.id.tv_submittedon_date)
    TextView tv_submittedon_date;

    @BindView(R.id.et_nominal_annual)
    EditText et_nominal_annual;

    @BindView(R.id.sp_interest_calc)
    Spinner sp_interest_calc;

    @BindView(R.id.sp_interest_comp)
    Spinner sp_interest_comp;

    @BindView(R.id.sp_interest_p_period)
    Spinner sp_interest_p_period;

    @BindView(R.id.sp_days_in_year)
    Spinner sp_days_in_year;

    @BindView(R.id.bt_submit)
    Button bt_submit;

    @Inject
    SavingsAccountPresenter mSavingsAccountPresenter;

    private View rootView;
    private SafeUIBlockingUtility safeUIBlockingUtility;
    private DialogFragment mfDatePicker;
    private int productId;
    private int clientId;
    private int interestCalculationTypeAdapterId;
    private int interestCompoundingPeriodTypeId;
    private int interestPostingPeriodTypeId;
    private int interestCalculationDaysInYearTypeId;
    private String submittion_date;
    private HashMap<String, Integer> savingsNameIdHashMap = new HashMap<String, Integer>();
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
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_savings_account, null);

        ButterKnife.bind(this, rootView);
        mSavingsAccountPresenter.attachView(this);

        inflateSubmissionDate();
        inflateSavingsSpinner();
        getSavingsAccountTemplateAPI();

        submittion_date = tv_submittedon_date.getText().toString();
        submittion_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (submittion_date).replace("-", " ");

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
                savingsPayload.setNominalAnnualInterestRate(et_nominal_annual.getEditableText()
                        .toString());
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
        mSavingsAccountPresenter.loadSavingsAccounts();
    }

    private void initiateSavingCreation(SavingsPayload savingsPayload) {
        mSavingsAccountPresenter.createSavingsAccount(savingsPayload);
    }

    private void inflateInterestPostingPeriodType() {

        final ArrayList<String> InterestPostingPeriodTypeNames = filterListObject
                (savingproductstemplate.getInterestPostingPeriodTypeOptions());

        final ArrayAdapter<String> interestPostingPeriodTypeAdapter =
                new ArrayAdapter<>(getActivity(),
                        layout.simple_spinner_item, InterestPostingPeriodTypeNames);

        interestPostingPeriodTypeAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        sp_interest_p_period.setAdapter(interestPostingPeriodTypeAdapter);
        sp_interest_p_period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestPostingPeriodTypeId = savingproductstemplate
                        .getInterestPostingPeriodTypeOptions().get(i).getId();
                Log.d("interestPosting " + InterestPostingPeriodTypeNames.get(i), String.valueOf
                        (interestPostingPeriodTypeId));
                if (interestPostingPeriodTypeId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string.interestPostingPeriodTypeId)
                            , Toast.LENGTH_SHORT).show();

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
        final ArrayAdapter<String> interestCalculationTypeAdapter =
                new ArrayAdapter<>(getActivity(),
                        layout.simple_spinner_item, interestCalculationTypeNames);
        interestCalculationTypeAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        sp_interest_calc.setAdapter(interestCalculationTypeAdapter);
        sp_interest_calc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestCalculationTypeAdapterId = savingproductstemplate
                        .getInterestCalculationTypeOptions().get(i).getId();
                Log.d("interestCalculation " + interestCalculationTypeNames.get(i), String
                        .valueOf(interestCalculationTypeAdapterId));
                if (interestCalculationTypeAdapterId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast
                            .LENGTH_SHORT).show();

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

        final ArrayAdapter<String> interestCalculationDaysInYearTypeAdapter =
                new ArrayAdapter<>(getActivity(),
                        layout.simple_spinner_item,
                        InterestCalculationDaysInYearTypeNames);
        interestCalculationDaysInYearTypeAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        sp_days_in_year.setAdapter(interestCalculationDaysInYearTypeAdapter);
        sp_days_in_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestCalculationDaysInYearTypeId = savingproductstemplate
                        .getInterestCalculationDaysInYearTypeOptions().get(i).getId();
                Log.d("interestCalculationD" + InterestCalculationDaysInYearTypeNames.get(i),
                        String.valueOf(interestCalculationDaysInYearTypeId));
                if (interestCalculationDaysInYearTypeId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string
                            .interestCalculationDaysInYearTypeAdapterId), Toast.LENGTH_SHORT)
                            .show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void interestCompoundingPeriodType() {

        final ArrayList<String> InterestCompoundingPeriodType = filterListObject
                (savingproductstemplate.getInterestCompoundingPeriodTypeOptions());

        final ArrayAdapter<String> interestCompoundingPeriodTypeAdapter =
                new ArrayAdapter<>(getActivity(),
                        layout.simple_spinner_item,
                        InterestCompoundingPeriodType);
        interestCompoundingPeriodTypeAdapter.setDropDownViewResource(
                layout.simple_spinner_dropdown_item);
        sp_interest_comp.setAdapter(interestCompoundingPeriodTypeAdapter);
        sp_interest_comp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interestCompoundingPeriodTypeId = savingproductstemplate
                        .getInterestCompoundingPeriodTypeOptions().get(i).getId();
                Log.d("clientTypeId " + InterestCompoundingPeriodType.get(i), String.valueOf
                        (interestCompoundingPeriodTypeId));
                if (interestCompoundingPeriodTypeId != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_intrested_cmp),
                            Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    private void getSavingsAccountTemplateAPI() {
        mSavingsAccountPresenter.loadSavingsAccountTemplate();
    }

    //TODO Replace this method with Presenter rxJava filter method
    private ArrayList<String> filterListObject(List<InterestType> interestTypes) {

        ArrayList<String> InterestValueList = new ArrayList<>();
        for (InterestType interestType : interestTypes) {
            InterestValueList.add(interestType.getValue());
        }

        return InterestValueList;
    }

    @Override
    public void showSavingsAccounts(List<ProductSavings> productSavings) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        final List<String> savingsList = new ArrayList<String>();

        for (ProductSavings savingsname : productSavings) {
            savingsList.add(savingsname.getName());
            savingsNameIdHashMap.put(savingsname.getName(), savingsname.getId());
        }
        ArrayAdapter<String> savingsAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, savingsList);
        savingsAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_product.setAdapter(savingsAdapter);
        sp_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                    l) {
                productId = savingsNameIdHashMap.get(savingsList.get(i));
                Log.d("productId " + savingsList.get(i), String.valueOf(productId));
                if (productId != -1) {
                } else {
                    Toast.makeText(getActivity(), getString(R.string
                            .error_select_product), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showSavingsAccountCreatedSuccessfully(Savings savings) {
        Toast.makeText(getActivity(), "The Savings Account has been submitted for " +
                "Approval", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSavingsAccountTemplate(SavingProductsTemplate savingProductsTemplate) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        savingproductstemplate = savingProductsTemplate;
        interestCompoundingPeriodType();
        inflateinterestCalculationDaysInYearType();
        inflateInterestCalculationTypeSpinner();
        inflateInterestPostingPeriodType();
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
        mSavingsAccountPresenter.detachView();
    }
}
