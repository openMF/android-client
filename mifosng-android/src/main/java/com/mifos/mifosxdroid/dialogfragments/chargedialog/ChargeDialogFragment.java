/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments.chargedialog;

import android.app.Dialog;
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
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableDialogFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Charges;
import com.mifos.objects.templates.clients.ChargeTemplate;
import com.mifos.services.data.ChargesPayload;
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
public class ChargeDialogFragment extends ProgressableDialogFragment implements
        MFDatePicker.OnDatePickListener, ChargeDialogMvpView,
        AdapterView.OnItemSelectedListener {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.sp_charge_name)
    Spinner spChargeName;

    @BindView(R.id.amount_due_charge)
    EditText etAmoutDue;

    @BindView(R.id.et_date)
    EditText etChargeDueDate;

    @BindView(R.id.et_charge_locale)
    EditText etChargeLocale;

    @BindView(R.id.bt_save_charge)
    Button btnSaveCharge;

    @Inject
    ChargeDialogPresenter mChargeDialogPresenter;

    private List<String> chargeNameList = new ArrayList<>();
    private ArrayAdapter<String> chargeNameAdapter;
    private ChargeTemplate mChargeTemplate;
    String duedateString;
    private View rootView;
    private DialogFragment mfDatePicker;
    private int chargeId;
    private int clientId;

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

        duedateString = etChargeDueDate.getText().toString();
        duedateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(duedateString)
                .replace("-", " ");
        return rootView;
    }

    @OnClick(R.id.bt_save_charge)
    public void saveNewCharge() {
        ChargesPayload chargesPayload = new ChargesPayload();
        chargesPayload.setAmount(etAmoutDue.getEditableText().toString());
        chargesPayload.setLocale(etChargeLocale.getEditableText().toString());
        chargesPayload.setDueDate(duedateString);
        chargesPayload.setDateFormat("dd MMMM yyyy");
        chargesPayload.setChargeId(chargeId);
        initiateChargesCreation(chargesPayload);
    }

    @Override
    public void onDatePicked(String date) {
        etChargeDueDate.setText(date);
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
        etChargeDueDate.setText(MFDatePicker.getDatePickedAsString());
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
        spChargeName.setOnItemSelectedListener(this);
    }

    @Override
    public void showAllChargesV2(ChargeTemplate chargeTemplate) {
        mChargeTemplate = chargeTemplate;
        chargeNameList.addAll(mChargeDialogPresenter.filterChargeName
                (chargeTemplate.getChargeOptions()));
        chargeNameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_charge_name:
                chargeId = mChargeTemplate.getChargeOptions().get(position).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void showChargesCreatedSuccessfully(Charges changes) {
        Toast.makeText(getActivity(), "Charge created successfully", Toast.LENGTH_LONG)
                .show();
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
}
