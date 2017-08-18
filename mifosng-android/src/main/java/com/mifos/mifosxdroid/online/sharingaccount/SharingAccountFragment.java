package com.mifos.mifosxdroid.online.sharingaccount;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ShareChargesAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableDialogFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.organisation.ProductSharing;
import com.mifos.objects.response.ShareResponse;
import com.mifos.objects.templates.clients.ChargeOptions;
import com.mifos.objects.templates.sharing.SharingAccountsTemplate;
import com.mifos.services.data.ShareChargePayload;
import com.mifos.services.data.SharingPayload;
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
 * Created by mayankjindal on 18/08/17.
 */

public class SharingAccountFragment extends ProgressableDialogFragment implements
        MFDatePicker.OnDatePickListener, SharingAccountMvpView, AdapterView.OnItemSelectedListener,
        ShareChargeAdpaterListener {

    public static final String LOG_TAG = SharingAccountFragment.class.getSimpleName();

    @NonNull
    @BindView(R.id.sp_product)
    Spinner spProduct;

    @NonNull
    @BindView(R.id.sp_charges)
    Spinner spCharges;

    @NonNull
    @BindView(R.id.sp_lockin_period)
    Spinner spLockinPeriod;

    @NonNull
    @BindView(R.id.sp_min_active_period)
    Spinner spMinActivePeriod;

    @NonNull
    @BindView(R.id.sp_savings_account)
    Spinner spSavingsAccount;

    @NonNull
    @BindView(R.id.et_client_external_id)
    EditText etClientExternalId;

    @NonNull
    @BindView(R.id.tv_submittedon_date)
    TextView tvSubmissionDate;

    @NonNull
    @BindView(R.id.tv_application_date)
    TextView tvApplicationDate;

    @NonNull
    @BindView(R.id.et_requested_shares)
    EditText etRequestedShares;

    @NonNull
    @BindView(R.id.et_lockin_period)
    EditText etLockinPeriod;

    @NonNull
    @BindView(R.id.et_min_active_period)
    EditText etMinActivePeriod;

    @NonNull
    @BindView(R.id.cb_allow_dividend_calculation)
    CheckBox cbDividendAllowed;

    @NonNull
    @BindView(R.id.recycler_charges)
    RecyclerView recyclerSheets;

    @NonNull
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @NonNull
    @BindView(R.id.img_add)
    ImageView btnAdd;

    @NonNull
    @Inject
    SharingAccountPresenter mSharingAccountPresenter;

    @NonNull
    @Inject
    ShareChargesAdapter shareChargesAdapter;

    private View rootView;

    private DialogFragment mfDatePicker;
    private int productId;
    private int clientId;
    private int savingId;
    private int lockinPeriodId;
    private int minActivePeriodId;
    private int chargeId;
    private int defaultShares;
    private String submissionDate;
    private String applicationDate;
    private boolean isAllowDividend = false;
    private boolean isApplicationDate = false;
    private boolean isSubmissionDate = false;
    private ShareChargeAdpaterListener clickAddListener;

    List<String> mChargesNames = new ArrayList<>();
    List<String> mListSharingProductsNames = new ArrayList<>();
    List<String> mListLockinPeriodOptions = new ArrayList<>();
    List<String> mListMinActivePeriodOptions = new ArrayList<>();
    List<String> mListSavingsAccountOptions = new ArrayList<>();

    ArrayAdapter<String> mChargesAdapter;
    ArrayAdapter<String> mSharingProductsAdapter;
    ArrayAdapter<String> mLockinPeriodAdapter;
    ArrayAdapter<String> mMinActivePeriodAdapter;
    ArrayAdapter<String> mSavingsAccountAdapter;

    private SharingAccountsTemplate mSharingProductsTemplateByProductId;
    private List<ProductSharing> mProductSharings;
    private List<ChargeOptions> mChargeOptions;
    private List<ChargeOptions> chargeOptionAdapterList = new ArrayList<ChargeOptions>();
    private ChargeOptions chargeOptionSelected;

    public static SharingAccountFragment newInstance(int clientId) {
        SharingAccountFragment sharingAccountFragment = new SharingAccountFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        sharingAccountFragment.setArguments(args);
        return sharingAccountFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_sharing_account, null);

        ButterKnife.bind(this, rootView);
        mSharingAccountPresenter.attachView(this);

        inflateSubmissionDate();
        inflateApplicationDate();
        inflateSharingSpinners();


        applicationDate = tvApplicationDate.getText().toString();
        submissionDate = tvSubmissionDate.getText().toString();
        submissionDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (submissionDate).replace("-", " ");
        applicationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (applicationDate).replace("-", " ");

        mSharingAccountPresenter.loadSharingAccountsAndTemplate(clientId);

        return rootView;
    }

    @OnCheckedChanged(R.id.cb_allow_dividend_calculation)
    public void onClickDividendAllowedCheckBox() {
        isAllowDividend = cbDividendAllowed.isChecked();
    }

    @OnClick(R.id.img_add)
    public void addCharges() {
        showChargesLayout();
    }

    public void showChargesLayout() {
        chargeOptionAdapterList.add(chargeOptionSelected);
        shareChargesAdapter.notifyItemInserted(chargeOptionAdapterList.size() - 1);
    }

    @Override
    public void removeChargeItem(int position) {
        chargeOptionAdapterList.remove(position);
        shareChargesAdapter.notifyItemRemoved(position);
    }

    @Override
    public void editAmount(int position, Double value) {
        chargeOptionAdapterList.get(position).setAmount(value);
    }

    public void inflateSharingSpinners() {

        mSharingProductsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, mListSharingProductsNames);
        mSharingProductsAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProduct.setAdapter(mSharingProductsAdapter);
        spProduct.setOnItemSelectedListener(this);

        mChargesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_spinner_item, mChargesNames);
        mChargesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCharges.setAdapter(mChargesAdapter);
        spCharges.setOnItemSelectedListener(this);

        mLockinPeriodAdapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_spinner_item, mListLockinPeriodOptions);
        mLockinPeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLockinPeriod.setAdapter(mLockinPeriodAdapter);
        spLockinPeriod.setOnItemSelectedListener(this);

        mMinActivePeriodAdapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_spinner_item, mListMinActivePeriodOptions);
        mMinActivePeriodAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spMinActivePeriod.setAdapter(mMinActivePeriodAdapter);
        spMinActivePeriod.setOnItemSelectedListener(this);

        mSavingsAccountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_spinner_item, mListSavingsAccountOptions);
        mSavingsAccountAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spSavingsAccount.setAdapter(mSavingsAccountAdapter);
        spSavingsAccount.setOnItemSelectedListener(this);
    }

    @OnClick(R.id.btn_submit)
    void submitSharingAccount() {
        if (Network.isOnline(getActivity())) {
            SharingPayload sharingPayload = new SharingPayload();
            sharingPayload.setExternalId(etClientExternalId.getEditableText().toString());
            sharingPayload.setLocale("en");
            sharingPayload.setSubmittedDate(submissionDate);
            sharingPayload.setDateFormat("dd MMMM yyyy");
            sharingPayload.setClientId(clientId);
            sharingPayload.setProductId(productId);
            sharingPayload.setRequestedShares(Integer.parseInt(etRequestedShares.
                    getEditableText().toString()));
            sharingPayload.setMinimumActivePeriod(etMinActivePeriod.getEditableText()
                    .toString());
            sharingPayload.setMinimumActivePeriodFrequencyType(minActivePeriodId);
            sharingPayload.setLockinPeriodFrequency(etLockinPeriod.
                    getEditableText().toString());
            sharingPayload.setLockinPeriodFrequencyType(lockinPeriodId);
            sharingPayload.setApplicationDate(applicationDate);
            sharingPayload.setAllowDividendCalculationForInactiveClients(isAllowDividend);
            sharingPayload.setSavingsAccountId(savingId);
            List<ShareChargePayload> shareChargePayloadList = new ArrayList<ShareChargePayload>();
            for (ChargeOptions chargeOptions : chargeOptionAdapterList) {
                shareChargePayloadList.add(new ShareChargePayload(chargeOptions.getId(),
                        chargeOptions.getAmount()));
            }
            sharingPayload.setChargePayload(shareChargePayloadList);
            mSharingAccountPresenter.createSharingAccount(sharingPayload);
        } else {
            Toaster.show(rootView, R.string.error_network_not_available);
        }
    }

    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tvSubmissionDate.setText(MFDatePicker.getDatePickedAsString());
    }

    @OnClick(R.id.tv_submittedon_date)
    void onClickTextViewSubmissionDate() {
        isSubmissionDate = true;
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    public void inflateApplicationDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tvApplicationDate.setText(MFDatePicker.getDatePickedAsString());
    }

    @OnClick(R.id.tv_application_date)
    public void setTvApplicationDate() {
        isApplicationDate = true;
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @Override
    public void onDatePicked(String date) {

        if (isSubmissionDate) {
            tvSubmissionDate.setText(date);
            submissionDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                    (date).replace("-", " ");
            isSubmissionDate = false;
        }

        if (isApplicationDate) {
            tvApplicationDate.setText(date);
            applicationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                    (date).replace("-", " ");
            isApplicationDate = false;
        }
    }

    @Override
    public void showSharingAccountTemplate(@NonNull List<ProductSharing> productSharing,
                                           @NonNull List<ChargeOptions> chargeOptions) {
        mProductSharings = productSharing;
        mChargeOptions = chargeOptions;

        mListSharingProductsNames.addAll(mSharingAccountPresenter
                .filterSharingProductsNames(productSharing));
        mSharingProductsAdapter.notifyDataSetChanged();

        mChargesNames.addAll(mSharingAccountPresenter
                .filterChargeOptionNames(chargeOptions));
        mChargesAdapter.notifyDataSetChanged();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerSheets.getContext(), layoutManager.getOrientation());
        recyclerSheets.setLayoutManager(layoutManager);
        recyclerSheets.addItemDecoration(dividerItemDecoration);
        recyclerSheets.setAdapter(shareChargesAdapter);
        shareChargesAdapter.setChargeOptionList(chargeOptionAdapterList);
        shareChargesAdapter.setAddItemClickListener(this);
    }

    @Override
    public void showSavingsAccountTemplateByProduct(@NonNull SharingAccountsTemplate
                                                                sharingAccountsTemplate) {
        mSharingProductsTemplateByProductId = sharingAccountsTemplate;


        mListLockinPeriodOptions.clear();
        mListLockinPeriodOptions.addAll(mSharingAccountPresenter
                .filterLockinPeriodFrequencyTypesName(sharingAccountsTemplate.
                        getLockinPeriodFrequencyTypeOptions()));
        mLockinPeriodAdapter.notifyDataSetChanged();

        mListMinActivePeriodOptions.clear();
        mListMinActivePeriodOptions.addAll(mSharingAccountPresenter
                .filterMinimumActivePeriodFrequencyTypesNames(sharingAccountsTemplate.
                        getMinimumActivePeriodFrequencyTypeOptions()));
        mMinActivePeriodAdapter.notifyDataSetChanged();

        mListSavingsAccountOptions.clear();
        mListSavingsAccountOptions.addAll(mSharingAccountPresenter
                .filterSavingProductsNames(sharingAccountsTemplate.getClientSavingsAccounts()));
        mSavingsAccountAdapter.notifyDataSetChanged();
        showDefaultValues();
    }

    @Override
    public void showSharingAccountCreatedSuccessfully(@NonNull ShareResponse shareResponse) {
        Toast.makeText(getActivity(), getString(R.string.sharing_account_submitted_for_approval),
                Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void showFetchingError(int errorMessage) {
        Toaster.show(rootView, getString(errorMessage));
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
        mSharingAccountPresenter.detachView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_lockin_period:
                lockinPeriodId = mSharingProductsTemplateByProductId
                        .getLockinPeriodFrequencyTypeOptions().get(position).getId();
                break;
            case R.id.sp_min_active_period:
                minActivePeriodId = mSharingProductsTemplateByProductId
                        .getMinimumActivePeriodFrequencyTypeOptions().get(position).getId();
                break;
            case R.id.sp_charges:
                chargeOptionSelected = mChargeOptions.get(position);
                chargeId = mChargeOptions.get(position).getId();
                break;
            case R.id.sp_product:
                productId = mProductSharings.get(position).getId();
                mSharingAccountPresenter.
                        loadSharingAccountTemplateByProduct(clientId, productId);
                break;
            case R.id.sp_savings_account:
                savingId = mSharingProductsTemplateByProductId.
                        getClientSavingsAccounts().get(position).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showDefaultValues() {
        defaultShares = mSharingProductsTemplateByProductId.getDefaultShares();
        etRequestedShares.setText(String.valueOf(defaultShares));
    }
}
