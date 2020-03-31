/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.savingsaccount;

import android.R.layout;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
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
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.services.data.SavingsPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.Network;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
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
    Spinner spProduct;

    @BindView(R.id.sp_field_officer)
    Spinner spFieldOfficer;

    @BindView(R.id.et_client_external_id)
    EditText etClientExternalId;

    @BindView(R.id.tv_submittedon_date)
    TextView tvSubmissionDate;

    @BindView(R.id.et_nominal_annual)
    EditText etNominalAnnual;

    @BindView(R.id.sp_interest_calc)
    TextView tvInterestCalc;

    @BindView(R.id.sp_interest_comp)
    TextView tvInterestComp;

    @BindView(R.id.sp_interest_p_period)
    TextView tvInterestPeriod;

    @BindView(R.id.sp_days_in_year)
    TextView tvDaysInYear;

    @BindView(R.id.cb_enforce_required_balance)
    CheckBox cbEnforceRequiredBalance;

    @BindView(R.id.et_min_required_balance)
    EditText etMinRequiredBalance;

    @BindView(R.id.cb_overdraft_allowed)
    CheckBox cbOverdraftAllowed;

    @BindView(R.id.et_max_overdraft_amount)
    EditText etMaxOverdraftAmount;

    @BindView(R.id.et_nominal_annual_overdraft)
    EditText etNominalAnnualOverdraft;

    @BindView(R.id.et_min_overdraft_required)
    EditText etMinOverdraftRequired;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @Inject
    SavingsAccountPresenter mSavingsAccountPresenter;

    private View rootView;

    private DialogFragment mfDatePicker;
    private int productId;
    private int clientId;
    private int fieldOfficerId;
    private int groupId;
    private String submission_date;

    List<String> mFieldOfficerNames = new ArrayList<>();
    List<String> mListSavingProductsNames = new ArrayList<>();

    ArrayAdapter<String> mFieldOfficerAdapter;
    ArrayAdapter<String> mSavingProductsAdapter;

    private SavingProductsTemplate mSavingProductsTemplateByProductId;
    private List<ProductSavings> mProductSavings;

    private boolean isGroupAccount = false;

    public static SavingsAccountFragment newInstance(int id, boolean isGroupAccount) {
        SavingsAccountFragment savingsAccountFragment = new SavingsAccountFragment();
        Bundle args = new Bundle();
        args.putInt(isGroupAccount ? Constants.GROUP_ID : Constants.CLIENT_ID, id);
        args.putBoolean(Constants.GROUP_ACCOUNT, isGroupAccount);
        savingsAccountFragment.setArguments(args);
        return savingsAccountFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            isGroupAccount = arguments.getBoolean(Constants.GROUP_ACCOUNT);
            clientId = arguments.getInt(Constants.CLIENT_ID);
            groupId = arguments.getInt(Constants.GROUP_ID);
        }
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

    @OnCheckedChanged(R.id.cb_enforce_required_balance)
    public void onClickOverdraftAllowedCheckBox() {
        etMinRequiredBalance.setVisibility(cbEnforceRequiredBalance.isChecked()
                ? View.VISIBLE : View.GONE);
    }

    @OnCheckedChanged(R.id.cb_overdraft_allowed)
    public void onClickMinRequiredCheckBox() {
        etMaxOverdraftAmount.setVisibility(cbOverdraftAllowed.isChecked()
                ? View.VISIBLE : View.GONE);
        etNominalAnnualOverdraft.setVisibility(cbOverdraftAllowed.isChecked()
                ? View.VISIBLE : View.GONE);
        etMinOverdraftRequired.setVisibility(cbOverdraftAllowed.isChecked()
                ? View.VISIBLE : View.GONE);
    }

    public void inflateSavingsSpinners() {

        mFieldOfficerAdapter = new ArrayAdapter<>(getActivity(),
                layout.simple_spinner_item, mFieldOfficerNames);
        mFieldOfficerAdapter
                .setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spFieldOfficer.setAdapter(mFieldOfficerAdapter);
        spFieldOfficer.setOnItemSelectedListener(this);

        mSavingProductsAdapter = new ArrayAdapter<>(getActivity(), layout
                .simple_spinner_item, mListSavingProductsNames);
        mSavingProductsAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spProduct.setAdapter(mSavingProductsAdapter);
        spProduct.setOnItemSelectedListener(this);

    }

    @OnClick(R.id.btn_submit)
    void submitSavingsAccount() {
        if (Network.isOnline(getContext())) {
            SavingsPayload savingsPayload = new SavingsPayload();
            savingsPayload.setExternalId(etClientExternalId.getEditableText().toString());
            savingsPayload.setLocale("en");
            savingsPayload.setSubmittedOnDate(submission_date);
            savingsPayload.setDateFormat("dd MMMM yyyy");
            if (isGroupAccount) {
                savingsPayload.setGroupId(groupId);
            } else {
                savingsPayload.setClientId(clientId);
            }
            savingsPayload.setProductId(productId);
            savingsPayload.setFieldOfficerId(fieldOfficerId);
            savingsPayload.setNominalAnnualInterestRate(etNominalAnnual.getEditableText()
                    .toString());
            savingsPayload.setAllowOverdraft(cbOverdraftAllowed.isChecked());
            savingsPayload.setNominalAnnualInterestRateOverdraft(etNominalAnnualOverdraft.
                    getEditableText().toString());
            savingsPayload.setOverdraftLimit(etMaxOverdraftAmount.getEditableText()
                    .toString());
            savingsPayload.setMinOverdraftForInterestCalculation(etMinOverdraftRequired.
                    getEditableText().toString());
            savingsPayload.setEnforceMinRequiredBalance(cbEnforceRequiredBalance.isChecked());
            savingsPayload.setMinRequiredOpeningBalance(etMinRequiredBalance.getEditableText()
                    .toString());

            mSavingsAccountPresenter.createSavingsAccount(savingsPayload);
        } else {
            Toaster.show(rootView, R.string.error_network_not_available);
        }
    }

    @Override
    public void onDatePicked(String date) {
        tvSubmissionDate.setText(date);
        submission_date  = date;
        setSubmissionDate();
    }

    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tvSubmissionDate.setText(MFDatePicker.getDatePickedAsString());
        setSubmissionDate();
    }

    @OnClick(R.id.tv_submittedon_date)
    void onClickTextViewSubmissionDate() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    public void setSubmissionDate() {
        submission_date = tvSubmissionDate.getText().toString();
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
    public void showSavingsAccountTemplateByProduct(SavingProductsTemplate savingProductsTemplate) {
        mSavingProductsTemplateByProductId = savingProductsTemplate;

        mFieldOfficerNames.addAll(mSavingsAccountPresenter.
                filterFieldOfficerNames(savingProductsTemplate.getFieldOfficerOptions()));
        mFieldOfficerAdapter.notifyDataSetChanged();

        tvInterestCalc.setText(savingProductsTemplate.getInterestCalculationType().getValue());
        tvInterestComp.setText(savingProductsTemplate.
                getInterestCompoundingPeriodType().getValue());
        tvInterestPeriod.setText(savingProductsTemplate.getInterestPostingPeriodType().getValue());
        tvDaysInYear.setText(savingProductsTemplate.
                getInterestCalculationDaysInYearType().getValue());
    }

    @Override
    public void showSavingsAccountCreatedSuccessfully(Savings savings) {
        Toast.makeText(getActivity(),
                getResources().getString(R.string.savings_account_submitted_for_approval),
                Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


    @Override
    public void showFetchingError(int errorMessage) {
        Toaster.show(rootView, getResources().getString(errorMessage));
    }

    @Override
    public void showFetchingError(String errorMessage) {
        Toaster.show(rootView, errorMessage);
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
            case R.id.sp_field_officer:
                fieldOfficerId = mSavingProductsTemplateByProductId
                        .getFieldOfficerOptions().get(position).getId();
                break;
            case R.id.sp_product:
                productId = mProductSavings.get(position).getId();
                if (isGroupAccount) {
                    mSavingsAccountPresenter.
                            loadGroupSavingAccountTemplateByProduct(groupId, productId);
                } else {
                    mSavingsAccountPresenter.
                            loadClientSavingAccountTemplateByProduct(clientId, productId);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
