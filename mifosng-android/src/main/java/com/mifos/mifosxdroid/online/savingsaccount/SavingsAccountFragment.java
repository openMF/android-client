/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.savingsaccount;

import android.R.layout;
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
import com.mifos.mifosxdroid.core.ProgressableDialogFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.services.data.SavingsPayload;
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
 * Use this Dialog Fragment to Create and/or Update charges
 */
public class SavingsAccountFragment extends ProgressableDialogFragment implements
        MFDatePicker.OnDatePickListener, SavingsAccountMvpView, AdapterView.OnItemSelectedListener {

    public static final String LOG_TAG = SavingsAccountFragment.class.getSimpleName();

    @BindView(R.id.sp_product)
    Spinner sp_product;

    @BindView(R.id.et_client_external_id)
    EditText et_client_external_id;

    @BindView(R.id.tv_submittedon_date)
    TextView tv_submission_date;

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

    private DialogFragment mfDatePicker;
    private int productId;
    private int clientId;
    private int interestCalculationTypeAdapterId;
    private int interestCompoundingPeriodTypeId;
    private int interestPostingPeriodTypeId;
    private int interestCalculationDaysInYearTypeId;
    private String submission_date;

    List<String> mListInterestPostingPeriodType = new ArrayList<>();
    List<String> mListInterestCalculationTypeNames = new ArrayList<>();
    List<String> mListInterestCalculationDaysInYearType = new ArrayList<>();
    List<String> mListInterestCompoundingPeriodType = new ArrayList<>();
    List<String> mListSavingProductsNames = new ArrayList<>();

    ArrayAdapter<String> mInterestPostingPeriodTypeAdapter;
    ArrayAdapter<String> mInterestCalculationTypeAdapter;
    ArrayAdapter<String> mInterestCalculationDaysInYearTypeAdapter;
    ArrayAdapter<String> mInterestCompoundingPeriodTypeAdapter;
    ArrayAdapter<String> mSavingProductsAdapter;

    private SavingProductsTemplate mSavingProductsTemplate;
    private List<ProductSavings> mProductSavings;

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
        if (getArguments() != null) clientId = getArguments().getInt(Constants.CLIENT_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_savings_account, null);

        ButterKnife.bind(this, rootView);
        mSavingsAccountPresenter.attachView(this);

        inflateSubmissionDate();
        inflateSavingsSpinners();

        mSavingsAccountPresenter.loadSavingsAccountsAndTemplate();

        return rootView;
    }

    public void inflateSavingsSpinners() {

        mInterestPostingPeriodTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListInterestPostingPeriodType);
        mInterestPostingPeriodTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_interest_p_period.setAdapter(mInterestPostingPeriodTypeAdapter);
        sp_interest_p_period.setOnItemSelectedListener(this);


        mInterestCalculationTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListInterestCalculationTypeNames);
        mInterestCalculationTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_interest_calc.setAdapter(mInterestCalculationTypeAdapter);
        sp_interest_calc.setOnItemSelectedListener(this);


        mInterestCalculationDaysInYearTypeAdapter  = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListInterestCalculationDaysInYearType);
        mInterestCalculationDaysInYearTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_days_in_year.setAdapter(mInterestCalculationDaysInYearTypeAdapter);
        sp_days_in_year.setOnItemSelectedListener(this);


        mInterestCompoundingPeriodTypeAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mListInterestCompoundingPeriodType);
        mInterestCompoundingPeriodTypeAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_interest_comp.setAdapter(mInterestCompoundingPeriodTypeAdapter);
        sp_interest_comp.setOnItemSelectedListener(this);


        mSavingProductsAdapter = new ArrayAdapter<>(getActivity(), layout
                .simple_spinner_item, mListSavingProductsNames);
        mSavingProductsAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        sp_product.setAdapter(mSavingProductsAdapter);
        sp_product.setOnItemSelectedListener(this);

    }


    @OnClick(R.id.bt_submit)
    void submitSavingsAccount() {

        SavingsPayload savingsPayload = new SavingsPayload();
        savingsPayload.setExternalId(et_client_external_id.getEditableText().toString());
        savingsPayload.setLocale("en");
        savingsPayload.setSubmittedOnDate(submission_date);
        savingsPayload.setDateFormat("dd MMMM yyyy");
        savingsPayload.setClientId(clientId);
        savingsPayload.setProductId(productId);
        savingsPayload.setNominalAnnualInterestRate(et_nominal_annual.getEditableText()
                .toString());
        savingsPayload.setInterestCompoundingPeriodType(interestCompoundingPeriodTypeId);
        savingsPayload.setInterestPostingPeriodType(interestPostingPeriodTypeId);
        savingsPayload.setInterestCalculationType(interestCalculationTypeAdapterId);
        savingsPayload.getInterestCalculationDaysInYearType();

        mSavingsAccountPresenter.createSavingsAccount(savingsPayload);
    }

    @Override
    public void onDatePicked(String date) {
        tv_submission_date.setText(date);
        setSubmissionDate();
    }

    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tv_submission_date.setText(MFDatePicker.getDatePickedAsString());
        setSubmissionDate();
        tv_submission_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }

    public void setSubmissionDate() {
        submission_date = tv_submission_date.getText().toString();
        submission_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (submission_date).replace("-", " ");
    }

    @Override
    public void showSavingsAccounts(List<ProductSavings> productSavings) {
        mProductSavings = productSavings;

        mListSavingProductsNames.addAll(mSavingsAccountPresenter
                .filterSavingProductsNames(productSavings));
        mSavingProductsAdapter.notifyDataSetChanged();

    }

    @Override
    public void showSavingsAccountTemplate(SavingProductsTemplate savingProductsTemplate) {
        mSavingProductsTemplate = savingProductsTemplate;

        mListInterestCompoundingPeriodType.addAll(mSavingsAccountPresenter.filterSpinnerOptions
                (savingProductsTemplate.getInterestCompoundingPeriodTypeOptions()));
        mInterestCompoundingPeriodTypeAdapter.notifyDataSetChanged();

        mListInterestCalculationDaysInYearType.addAll(mSavingsAccountPresenter.filterSpinnerOptions(
                savingProductsTemplate.getInterestCalculationDaysInYearTypeOptions()));
        mInterestCalculationDaysInYearTypeAdapter.notifyDataSetChanged();

        mListInterestCalculationTypeNames.addAll(mSavingsAccountPresenter.filterSpinnerOptions
                (savingProductsTemplate.getInterestCalculationTypeOptions()));
        mInterestCalculationTypeAdapter.notifyDataSetChanged();

        mListInterestPostingPeriodType.addAll(mSavingsAccountPresenter.filterSpinnerOptions
                (savingProductsTemplate.getInterestPostingPeriodTypeOptions()));
        mInterestPostingPeriodTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSavingsAccountCreatedSuccessfully(Savings savings) {
        Toast.makeText(getActivity(),
                getResources().getString(R.string.savings_account_submitted_for_approval),
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void showFetchingError(int errorMessage) {
        Toaster.show(rootView, getResources().getString(errorMessage));
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_interest_comp:
                interestCompoundingPeriodTypeId = mSavingProductsTemplate
                        .getInterestCompoundingPeriodTypeOptions().get(position).getId();
                break;
            case R.id.sp_days_in_year:
                interestCalculationDaysInYearTypeId = mSavingProductsTemplate
                        .getInterestCalculationDaysInYearTypeOptions().get(position).getId();
                break;
            case R.id.sp_interest_calc:
                interestCalculationTypeAdapterId = mSavingProductsTemplate
                        .getInterestCalculationTypeOptions().get(position).getId();
                break;
            case R.id.sp_interest_p_period:
                interestPostingPeriodTypeId = mSavingProductsTemplate
                        .getInterestPostingPeriodTypeOptions().get(position).getId();
                break;
            case R.id.sp_product:
                productId = mProductSavings.get(position).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
