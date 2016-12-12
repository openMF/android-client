/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments.loanchargedialog;

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
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableDialogFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Charges;
import com.mifos.services.data.ChargesPayload;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

/**
 * Created by nellyk on 1/22/2016.
 * <p/>
 * Use this Dialog Fragment to Create and/or Update charges
 */
public class LoanChargeDialogFragment extends ProgressableDialogFragment implements
        MFDatePicker.OnDatePickListener, LoanChargeDialogMvpView {

    public final String LOG_TAG = getClass().getSimpleName();

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    @BindView(R.id.sp_charge_name)
    Spinner sp_charge_name;

    @BindView(R.id.amount_due_charge)
    EditText et_amout_due;

    @BindView(R.id.et_date)
    EditText charge_due_date;

    @BindView(R.id.et_charge_locale)
    EditText charge_locale;

    @BindView(R.id.bt_save_charge)
    Button bt_save_charge;

    @Inject
    LoanChargeDialogPresenter mLoanChargeDialogPresenter;

    String duedateString;

    private OnDialogFragmentInteractionListener mListener;
    private DialogFragment mfDatePicker;
    private int Id;
    private int loanAccountNumber;
    private HashMap<String, Integer> chargeNameIdHashMap = new HashMap<String, Integer>();
    private String chargeName;

    public static LoanChargeDialogFragment newInstance(int loanAccountNumber) {
        LoanChargeDialogFragment loanChargeDialogFragment = new LoanChargeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        loanChargeDialogFragment.setArguments(args);
        return loanChargeDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
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
        mLoanChargeDialogPresenter.attachView(this);

        inflatedueDate();
        inflateChargesSpinner();

        duedateString = charge_due_date.getText().toString();
        duedateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(duedateString)
                .replace("-", " ");

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

    private void inflateChargesSpinner() {
        mLoanChargeDialogPresenter.loanAllChargesV3(loanAccountNumber);
    }

    private void initiateChargesCreation(ChargesPayload chargesPayload) {
        mLoanChargeDialogPresenter.createLoanCharges(loanAccountNumber, chargesPayload);
    }

    public void inflatedueDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        charge_due_date.setText(MFDatePicker.getDatePickedAsString());

        charge_due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }

    @Override
    public void showAllChargesV3(ResponseBody result) {

        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        Log.d(LOG_TAG, "");

        final List<Charges> charges = new ArrayList<>();
        // you can use this array to populate your spinner
        final ArrayList<String> chargesNames = new ArrayList<String>();
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(result.byteStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            if (obj.has("chargeOptions")) {
                JSONArray chargesTypes = obj.getJSONArray("chargeOptions");
                for (int i = 0; i < chargesTypes.length(); i++) {
                    JSONObject chargesObject = chargesTypes.getJSONObject(i);
                    Charges charge = new Charges();
                    charge.setId(chargesObject.optInt("id"));
                    charge.setName(chargesObject.optString("name"));
                    charges.add(charge);
                    chargesNames.add(chargesObject.optString("name"));
                    chargeNameIdHashMap.put(charge.getName(), charge.getId());
                }

            }
            String stringResult = sb.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        final ArrayAdapter<String> chargesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, chargesNames);
        chargesAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_charge_name.setAdapter(chargesAdapter);
        sp_charge_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                    l) {
                Id = chargeNameIdHashMap.get(chargesNames.get(i));
                Log.d("chargesoptionss" + chargesNames.get(i), String.valueOf(Id));
                if (Id != -1) {


                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_charge)
                            , Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showLoanChargesCreatedSuccessfully(Charges charges) {
        Toast.makeText(getActivity(), R.string.charge_created, Toast
                .LENGTH_LONG)
                .show();
    }

    @Override
    public void showError(String s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanChargeDialogPresenter.detachView();
    }

    public interface OnDialogFragmentInteractionListener {


    }
}
