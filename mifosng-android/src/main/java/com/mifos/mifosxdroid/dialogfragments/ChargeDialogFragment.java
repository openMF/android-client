/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments;

import android.app.Dialog;
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
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Charges;
import com.mifos.services.data.ChargesPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

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
 *
 * Use this Dialog Fragment to Create and/or Update charges
 */
public class ChargeDialogFragment extends DialogFragment implements MFDatePicker.OnDatePickListener {

    public static final String TAG = "ChargeDialogFragment";
    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    private OnDialogFragmentInteractionListener mListener;

    @InjectView(R.id.sp_charge_name)
    Spinner sp_charge_name;
    @InjectView(R.id.amount_due_charge)
    EditText et_amout_due;
    @InjectView(R.id.et_date)
    EditText charge_due_date;
    @InjectView(R.id.et_charge_locale)
    EditText charge_locale;
    @InjectView(R.id.bt_save_charge)
    Button bt_save_charge;
    private DialogFragment mfDatePicker;
    private int Id;
    private int clientId;
    String duedateString;
    private HashMap<String, Integer> chargeNameIdHashMap = new HashMap<String, Integer>();
    private String chargeName;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.dialog_fragment_charge, null);
        ButterKnife.inject(this, rootView);
        inflatedueDate();
        inflateChargesSpinner();

        duedateString = charge_due_date.getText().toString();
        duedateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(duedateString).replace("-", " ");

        bt_save_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChargesPayload chargesPayload = new ChargesPayload();
                chargesPayload.setAmount(et_amout_due.getEditableText().toString());
                chargesPayload.setLocale(charge_locale.getEditableText().toString());
                chargesPayload.setDueDate(duedateString);
                chargesPayload.setDateFormat("dd MMMM yyyy");
                chargesPayload.setChargeId(Id);

                initiateChargesCreation(chargesPayload);

            }
        });

        return rootView;
    }

    @Override
    public void onDatePicked(String date) {
        charge_due_date.setText(date);

    }


    public interface OnDialogFragmentInteractionListener {


    }

    private void inflateChargesSpinner() {

        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication) getActivity().getApplicationContext()).api.chargeService.getAllChargesS(new Callback<List<Charges>>() {

            @Override
            public void success(List<Charges> charges, Response response) {
                final List<String> chargesList = new ArrayList<String>();

                for (Charges chargesname : charges) {
                    chargesList.add(chargesname.getName());
                    chargeNameIdHashMap.put(chargesname.getName(), chargesname.getId());

                }
                ArrayAdapter<String> chargeAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, chargesList);
                chargeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_charge_name.setAdapter(chargeAdapter);
                sp_charge_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Id = chargeNameIdHashMap.get(chargesList.get(i));
                        Log.d("Id " + chargesList.get(i), String.valueOf(Id));
                        if (Id != -1) {


                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_charge), Toast.LENGTH_SHORT).show();

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

    private void initiateChargesCreation(ChargesPayload chargesPayload) {

        safeUIBlockingUtility.safelyBlockUI();

                MifosApplication.getApi().chargeService.createCharges(clientId, chargesPayload, new Callback<Charges>() {
                    @Override
                    public void success(Charges charges, Response response) {
                        safeUIBlockingUtility.safelyUnBlockUI();
                        Toast.makeText(getActivity(), "Charge created successfully", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        safeUIBlockingUtility.safelyUnBlockUI();
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
                    }
                });


        }


    public void inflatedueDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        charge_due_date.setText(MFDatePicker.getDatePickedAsString());

        charge_due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }
        });

    }
}
