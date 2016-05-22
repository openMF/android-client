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
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.App;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.accounts.loan.PaymentTypeOptions;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nellyk on 1/22/2016.
 */
public class LoanAccountDisbursement extends DialogFragment implements MFDatePicker
        .OnDatePickListener {

    public static final String TAG = "LoanAccountDisbursement";
    public static int loanAccountNumber;
    View rootView;
    SafeUIBlockingUtility safeUIBlockingUtility;
    @InjectView(R.id.tv_loan_disbursement_dates)
    TextView loan_disbursement_dates;
    @InjectView(R.id.bt_disburse_loan)
    Button bt_disburse_loan;
    @InjectView(R.id.sp_loan_payment_type)
    Spinner sp_payment_type;
    @InjectView(R.id.et_disbursed_amount)
    EditText et_disbursed_amount;
    @InjectView(R.id.et_disbursement_note)
    EditText et_disbursement_note;
    int paymentTypeId;
    String disbursement_dates;
    private OnDialogFragmentInteractionListener mListener;
    private DialogFragment mfDatePicker;
    private HashMap<String, Integer> paymentNameIdHashMap = new HashMap<String, Integer>();

    public static LoanAccountDisbursement newInstance(int loanAccountNumber) {
        LoanAccountDisbursement loanAccountDisbursement = new LoanAccountDisbursement();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        loanAccountDisbursement.setArguments(args);
        return loanAccountDisbursement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        }

        setHasOptionsMenu(true);
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
        rootView = inflater.inflate(R.layout.dialog_fragment_disburse_loan, null);
        ButterKnife.inject(this, rootView);
        inflateDisbursementDate();
        inflatePaymentTypeSpinner();
        disbursement_dates = loan_disbursement_dates.getText().toString();
        disbursement_dates = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (disbursement_dates).replace("-", " ");

        bt_disburse_loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoanDisbursement loanDisbursement = new LoanDisbursement();
                loanDisbursement.setNote(et_disbursement_note.getEditableText().toString());
                loanDisbursement.setActualDisbursementDate(disbursement_dates);
                loanDisbursement.setTransactionAmount(et_disbursed_amount.getEditableText()
                        .toString());
                loanDisbursement.setPaymentId(paymentTypeId);
                initiateLoanDisbursement(loanDisbursement);

            }
        });

        return rootView;
    }

    @Override
    public void onDatePicked(String date) {
        loan_disbursement_dates.setText(date);

    }

    private void inflatePaymentTypeSpinner() {
        App.apiManager.getLoanTemplate(loanAccountNumber, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                final ArrayList<PaymentTypeOptions> paymentOption = new
                        ArrayList<PaymentTypeOptions>();
                final ArrayList<String> paymentNames = new ArrayList<String>();
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    JSONObject obj = new JSONObject(sb.toString());
                    if (obj.has("paymentTypeOptions")) {
                        JSONArray paymentOptions = obj.getJSONArray("paymentTypeOptions");
                        for (int i = 0; i < paymentOptions.length(); i++) {
                            JSONObject paymentObject = paymentOptions.getJSONObject(i);
                            PaymentTypeOptions payment = new PaymentTypeOptions();
                            payment.setId(paymentObject.optInt("id"));
                            payment.setName(paymentObject.optString("name"));
                            paymentOption.add(payment);
                            paymentNames.add(paymentObject.optString("name"));
                            paymentNameIdHashMap.put(payment.getName(), payment.getId());
                        }
                    }
                    String stringResult = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
                ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, paymentNames);
                paymentAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                sp_payment_type.setAdapter(paymentAdapter);
                sp_payment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                            l) {
                        paymentTypeId = paymentNameIdHashMap.get(paymentNames.get(i));
                        Log.d("paymentId " + paymentNames.get(i), String.valueOf(paymentTypeId));
                        if (paymentTypeId != -1) {


                        } else {

                            Toast.makeText(getActivity(), getString(R.string
                                    .error_select_payment), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());
            }
        });

    }

    private void initiateLoanDisbursement(final LoanDisbursement loanDisbursement) {

        App.apiManager.dispurseLoan(loanAccountNumber,
                loanDisbursement,
                new Callback<GenericResponse>() {
                    @Override
                    public void success(GenericResponse genericResponse, Response response) {
                        Toast.makeText(getActivity(), "The Loan has been Disbursed", Toast
                                .LENGTH_LONG).show();

                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void inflateDisbursementDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        loan_disbursement_dates.setText(MFDatePicker.getDatePickedAsString());

        loan_disbursement_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }


    public interface OnDialogFragmentInteractionListener {


    }


}
