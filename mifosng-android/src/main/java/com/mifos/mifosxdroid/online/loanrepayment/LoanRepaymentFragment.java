/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loanrepayment;

import android.R.layout;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.fliptables.FlipTable;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoanRepaymentFragment extends MifosBaseFragment
        implements MFDatePicker.OnDatePickListener, LoanRepaymentMvpView,
        DialogInterface.OnClickListener {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.rl_loan_repayment)
    RelativeLayout rl_loan_repayment;

    @BindView(R.id.tv_clientName)
    TextView tv_clientName;

    @BindView(R.id.tv_loan_product_short_name)
    TextView tv_loanProductShortName;

    @BindView(R.id.tv_loanAccountNumber)
    TextView tv_loanAccountNumber;

    @BindView(R.id.tv_in_arrears)
    TextView tv_inArrears;

    @BindView(R.id.tv_amount_due)
    TextView tv_amountDue;

    @BindView(R.id.tv_repayment_date)
    TextView tv_repaymentDate;

    @BindView(R.id.et_amount)
    EditText et_amount;

    @BindView(R.id.et_additional_payment)
    EditText et_additionalPayment;

    @BindView(R.id.et_fees)
    EditText et_fees;

    @BindView(R.id.tv_total)
    TextView tv_total;

    @BindView(R.id.sp_payment_type)
    Spinner sp_paymentType;

    @BindView(R.id.bt_paynow)
    Button bt_paynow;

    @Inject
    LoanRepaymentPresenter mLoanRepaymentPresenter;

    private View rootView;
    // Arguments Passed From the Loan Account Summary Fragment
    private String clientName;
    private String loanId;
    private String loanAccountNumber;
    private String loanProductName;
    private Double amountInArrears;
    private int paymentTypeOptionId;
    private DialogFragment mfDatePicker;

    public static LoanRepaymentFragment newInstance(LoanWithAssociations loanWithAssociations) {
        LoanRepaymentFragment fragment = new LoanRepaymentFragment();
        Bundle args = new Bundle();
        if (loanWithAssociations != null) {
            args.putParcelable(Constants.LOAN_SUMMARY, loanWithAssociations);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            LoanWithAssociations mLoanWithAssociations = getArguments().getParcelable(Constants
                    .LOAN_SUMMARY);
            if (mLoanWithAssociations != null) {
                clientName = mLoanWithAssociations.getClientName();
                loanAccountNumber = mLoanWithAssociations.getAccountNo();
                loanId = String.valueOf(mLoanWithAssociations.getId());
                loanProductName = mLoanWithAssociations.getLoanProductName();
                amountInArrears = mLoanWithAssociations.getSummary().getTotalOverdue();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_repayment, container, false);
        setToolbarTitle("Loan Repayment");

        ButterKnife.bind(this, rootView);
        mLoanRepaymentPresenter.attachView(this);

        //This Method Checking LoanRepayment made before in Offline mode or not.
        //If yes then User have to sync this first then he can able to make transaction.
        //If not then User able to make LoanRepayment in Online or Offline.
        checkLoanRepaymentStatusInDatabase();

        return rootView;
    }

    @Override
    public void checkLoanRepaymentStatusInDatabase() {
        // Checking LoanRepayment Already made in Offline mode or Not.
        mLoanRepaymentPresenter.checkDatabaseLoanRepaymentByLoanId(Integer
                .parseInt(loanId));
    }

    @Override
    public void showLoanRepaymentExistInDatabase() {
        //Visibility of ParentLayout GONE, If Repayment Already made in Offline Mode
        rl_loan_repayment.setVisibility(View.GONE);

        new MaterialDialog.Builder().init(getActivity())
                .setTitle(R.string.sync_previous_transaction)
                .setMessage(R.string.dialog_message_sync_transaction)
                .setPositiveButton(R.string.dialog_action_ok, this)
                .setCancelable(false)
                .createMaterialDialog()
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void showLoanRepaymentDoesNotExistInDatabase() {
        // This Method Inflating UI and Initializing the Loading LoadRepayment
        // Template for transaction
        inflateUI();

        // Loading PaymentOptions.
        mLoanRepaymentPresenter.loanLoanRepaymentTemplate(Integer.parseInt(loanId));
    }

    /**
     * This Method Setting UI and Initializing the Object, TextView or EditText.
     */
    public void inflateUI() {
        tv_clientName.setText(clientName);
        tv_loanProductShortName.setText(loanProductName);
        tv_loanAccountNumber.setText(loanId);
        tv_inArrears.setText(String.valueOf(amountInArrears));

        //Setup Form with Default Values
        et_amount.setText("0.0");

        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
                    tv_total.setText(String.valueOf(calculateTotal()));
                } catch (NumberFormatException nfe) {
                    et_amount.setText("0");
                } finally {
                    tv_total.setText(String.valueOf(calculateTotal()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_additionalPayment.setText("0.0");

        et_additionalPayment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
                    tv_total.setText(String.valueOf(calculateTotal()));
                } catch (NumberFormatException nfe) {
                    et_additionalPayment.setText("0");
                } finally {
                    tv_total.setText(String.valueOf(calculateTotal()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_fees.setText("0.0");

        et_fees.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
                    tv_total.setText(String.valueOf(calculateTotal()));
                } catch (NumberFormatException nfe) {
                    et_fees.setText("0");
                } finally {
                    tv_total.setText(String.valueOf(calculateTotal()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inflateRepaymentDate();
        tv_total.setText(String.valueOf(calculateTotal()));
    }


    /**
     * Calculating the Total of the  Amount, Additional Payment and Fee
     *
     * @return Total of the Amount + Additional Payment + Fee Amount
     */
    public Double calculateTotal() {
        return Double.parseDouble(et_amount.getText().toString())
                + Double.parseDouble(et_additionalPayment.getText().toString())
                + Double.parseDouble(et_fees.getText().toString());
    }

    /**
     * Setting the Repayment Date
     */
    public void inflateRepaymentDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tv_repaymentDate.setText(MFDatePicker.getDatePickedAsString());
        /*
            TODO Add Validation to make sure :
            1. Date Is in Correct Format
            2. Date Entered is not greater than Date Today i.e Date is not in future
         */
        tv_repaymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }

    /**
     * Whenever user click on Date Picker and in Result, setting Date in TextView.
     *
     * @param date Selected Date by Date picker
     */
    @Override
    public void onDatePicked(String date) {
        tv_repaymentDate.setText(date);
    }


    /**
     * Submitting the LoanRepayment after setting all arguments and Displaying the Dialog
     * First, So that user make sure. He/She wanna make LoanRepayment
     */
    @OnClick(R.id.bt_paynow)
    public void onPayNowButtonClicked() {
        try {
            String[] headers = {"Field", "Value"};
            final String[][] data = {
                    {"Account Number", loanAccountNumber},
                    {"Repayment Date", tv_repaymentDate.getText().toString()},
                    {"Payment Type", sp_paymentType.getSelectedItem().toString()},
                    {"Amount", et_amount.getText().toString()},
                    {"Addition Payment", et_additionalPayment.getText().toString()},
                    {"Fees", et_fees.getText().toString()},
                    {"Total", String.valueOf(calculateTotal())}
            };
            Log.d(LOG_TAG, FlipTable.of(headers, data));

            String formReviewString = new StringBuilder().append(data[0][0] + " : " + data[0][1])
                    .append("\n")
                    .append(data[1][0] + " : " + data[1][1])
                    .append("\n")
                    .append(data[2][0] + " : " + data[2][1])
                    .append("\n")
                    .append(data[3][0] + " : " + data[3][1])
                    .append("\n")
                    .append(data[4][0] + " : " + data[4][1])
                    .append("\n")
                    .append(data[5][0] + " : " + data[5][1])
                    .append("\n")
                    .append(data[6][0] + " : " + data[6][1]).toString();

            new MaterialDialog.Builder().init(getActivity())
                    .setTitle(R.string.review_payment)
                    .setMessage(formReviewString)
                    .setPositiveButton(R.string.dialog_action_pay_now,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    submitPayment();
                                }
                            })
                    .setNegativeButton(R.string.dialog_action_back,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .createMaterialDialog()
                    .show();

        } catch (NullPointerException npe) {
            Toaster.show(rootView, "Please make sure every field has a value, before submitting " +
                    "repayment!");
        }
    }

    /**
     * Cancel button on Home UI
     */
    @OnClick(R.id.bt_cancelPayment)
    public void onCancelPaymentButtonClicked() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    /**
     * Submit the Final LoanRepayment
     */
    public void submitPayment() {
        //TODO Implement a proper builder method here
        String dateString = tv_repaymentDate.getText().toString().replace("-", " ");
        final LoanRepaymentRequest request = new LoanRepaymentRequest();
        request.setAccountNumber(loanAccountNumber);
        request.setPaymentTypeId(String.valueOf(paymentTypeOptionId));
        request.setLocale("en");
        request.setTransactionAmount(String.valueOf(calculateTotal()));
        request.setDateFormat("dd MM yyyy");
        request.setTransactionDate(dateString);
        String builtRequest = new Gson().toJson(request);
        Log.i("LOG_TAG", builtRequest);

        mLoanRepaymentPresenter.submitPayment(Integer.parseInt(loanId), request);
    }

    @Override
    public void showLoanRepayTemplate(final LoanRepaymentTemplate loanRepaymentTemplate) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        if (loanRepaymentTemplate != null) {
            tv_amountDue.setText(String.valueOf(loanRepaymentTemplate.getAmount()));
            inflateRepaymentDate();
            List<String> listOfPaymentTypes =
                    Utils.getPaymentTypeOptions(loanRepaymentTemplate.getPaymentTypeOptions());

            ArrayAdapter<String> paymentTypeAdapter = new ArrayAdapter<>(getActivity(),
                    layout.simple_spinner_item, listOfPaymentTypes);

            paymentTypeAdapter.setDropDownViewResource(
                    layout.simple_spinner_dropdown_item);
            sp_paymentType.setAdapter(paymentTypeAdapter);
            sp_paymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long
                        id) {
                    paymentTypeOptionId = loanRepaymentTemplate
                            .getPaymentTypeOptions().get(position).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            et_amount.setText(String.valueOf(loanRepaymentTemplate
                    .getPrincipalPortion()
                    + loanRepaymentTemplate.getInterestPortion()));
            et_additionalPayment.setText("0.0");
            et_fees.setText(String.valueOf(loanRepaymentTemplate
                    .getFeeChargesPortion()));

        }
    }

    @Override
    public void showPaymentSubmittedSuccessfully(LoanRepaymentResponse loanRepaymentResponse) {
        if (loanRepaymentResponse != null) {
            Toaster.show(rootView, "Payment Successful, Transaction ID = " +
                    loanRepaymentResponse.getResourceId());
        }
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


    @Override
    public void showError(int errorMessage) {
        Toaster.show(rootView, errorMessage);
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            rl_loan_repayment.setVisibility(View.GONE);
            showMifosProgressBar();
        } else {
            rl_loan_repayment.setVisibility(View.VISIBLE);
            hideMifosProgressBar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanRepaymentPresenter.detachView();
    }

    public interface OnFragmentInteractionListener {

    }
}
