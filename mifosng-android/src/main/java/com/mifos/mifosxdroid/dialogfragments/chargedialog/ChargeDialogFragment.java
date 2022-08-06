/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableDialogFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.ChargeCreationResponse;
import com.mifos.objects.client.Charges;
import com.mifos.objects.templates.clients.ChargeTemplate;
import com.mifos.services.data.ChargesPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nellyk on 1/22/2016.
 * <p/>
 * Use this Dialog Fragment to Create and/or Update charges
 */
public class ChargeDialogFragment extends ProgressableDialogFragment implements
        MFDatePicker.OnDatePickListener, ChargeDialogMvpView  {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.chargeNameField)
    MaterialAutoCompleteTextView spChargeName;

    @BindView(R.id.amount_due_charge)
    EditText etAmountDue;

    @BindView(R.id.et_date)
    EditText etChargeDueDate;

    @BindView(R.id.et_charge_locale)
    EditText etChargeLocale;

    @BindView(R.id.bt_save_charge)
    Button btnSaveCharge;

    @Inject
    ChargeDialogPresenter mChargeDialogPresenter;

    @Nullable
    private OnChargeCreateListener chargeCreateListener;

    private List<String> chargeNameList = new ArrayList<>();
    private ArrayAdapter<String> chargeNameAdapter;
    private ChargeTemplate mChargeTemplate;
    private String dueDateString;
    private List<Integer> dueDateAsIntegerList;
    private View rootView;
    private DialogFragment mfDatePicker;
    private int chargeId;
    private String chargeName;
    private int clientId;
    private Charges createdCharge;

    public static ChargeDialogFragment newInstance(int clientId) {
        ChargeDialogFragment chargeDialogFragment = new ChargeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        chargeDialogFragment.setArguments(args);
        return chargeDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.dialog_fragment_charge, null);

        ButterKnife.bind(this, rootView);
        mChargeDialogPresenter.attachView(this);
        inflatedueDate();
        inflateChargesSpinner();
        inflateChargeNameSpinner();

        return rootView;
    }

    @OnClick(R.id.bt_save_charge)
    public void saveNewCharge() {
        //Insert values for the new Charge.
        if (etAmountDue.getText().toString().isEmpty()) {
            Toaster.show(rootView, getString(R.string.amount)
                    + " " + getString(R.string.error_cannot_be_empty));
            return;
        }
        createdCharge = new Charges();
        createdCharge.setChargeId(chargeId);
        createdCharge.setAmount(Double.parseDouble(etAmountDue.getEditableText().toString()));

        dueDateAsIntegerList = DateHelper.convertDateAsReverseInteger(dueDateString);
        createdCharge.setDueDate(dueDateAsIntegerList);
        createdCharge.setName(chargeName);

        ChargesPayload chargesPayload = new ChargesPayload();
        chargesPayload.setAmount(etAmountDue.getEditableText().toString());
        chargesPayload.setLocale(etChargeLocale.getEditableText().toString());
        chargesPayload.setDueDate(dueDateString);
        chargesPayload.setDateFormat("dd MMMM yyyy");
        chargesPayload.setChargeId(chargeId);
        initiateChargesCreation(chargesPayload);
    }

    @Override
    public void onDatePicked(String date) {
        dueDateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
                .replace("-", " ");
        etChargeDueDate.setText(dueDateString);
    }

    //Charges Fetching API
    private void inflateChargesSpinner() {
        mChargeDialogPresenter.loadAllChargesV2(clientId);
    }

    //Charges Creation APi
    private void initiateChargesCreation(ChargesPayload chargesPayload) {
        mChargeDialogPresenter.createCharges(clientId, chargesPayload);
    }

    public void inflatedueDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        String receivedDate = MFDatePicker.getDatePickedAsString();
        dueDateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(receivedDate)
                .replace("-", " ");
        dueDateAsIntegerList = DateHelper.convertDateAsListOfInteger(dueDateString);
        etChargeDueDate.setText(dueDateString);
    }

    @OnClick(R.id.et_date)
    public void inflateDatePicker() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }


    public void inflateChargeNameSpinner() {
        chargeNameAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, chargeNameList);
        chargeNameAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChargeName.setAdapter(chargeNameAdapter);
        spChargeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                chargeId = mChargeTemplate.getChargeOptions().get(position).getId();
                chargeName = mChargeTemplate.getChargeOptions().get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void showAllChargesV2(ChargeTemplate chargeTemplate) {
        mChargeTemplate = chargeTemplate;
        chargeNameList.addAll(mChargeDialogPresenter.filterChargeName
                (chargeTemplate.getChargeOptions()));
        chargeNameAdapter.notifyDataSetChanged();
    }

    @Override
    public void showChargesCreatedSuccessfully(ChargeCreationResponse chargeCreationResponse) {
        if (chargeCreateListener != null) {
            createdCharge.setClientId(chargeCreationResponse.getClientId());
            createdCharge.setId(chargeCreationResponse.getResourceId());
            chargeCreateListener.onChargeCreatedSuccess(createdCharge);
        } else {
            Toaster.show(rootView, getString(R.string.message_charge_created_success));
        }
        getDialog().dismiss();
    }

    @Override
    public void showChargeCreatedFailure(String errorMessage) {
        if (chargeCreateListener != null) {
            chargeCreateListener.onChargeCreatedFailure(errorMessage);
        } else {
            Toaster.show(rootView, errorMessage);
        }
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
        mChargeDialogPresenter.detachView();
    }

    public void setOnChargeCreatedListener(@Nullable OnChargeCreateListener chargeCreatedListener) {
        this.chargeCreateListener = chargeCreatedListener;
    }
}
