/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.savingsaccountactivate;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tarun on 01/06/17.
 * Fragment to allow user to select a date for account approval.
 * It uses the same layout as Savings Account Approve Fragment.
 */
public class SavingsAccountActivateFragment extends MifosBaseFragment implements
        MFDatePicker.OnDatePickListener, SavingsAccountActivateMvpView {

    public final String LOG_TAG = getClass().getSimpleName();
    @BindView(R.id.tv_approval_date_on)
    TextView tvActivateDateHeading;

    @BindView(R.id.tv_approval_date)
    TextView tvActivationDate;

    @BindView(R.id.btn_approve_savings)
    Button btnActivateSavings;

    @BindView(R.id.et_savings_approval_reason)
    EditText etSavingsActivateReason;

    @Inject
    SavingsAccountActivatePresenter mSavingsAccountActivatePresenter;

    View rootView;

    String activationDate;
    public int savingsAccountNumber;
    public DepositType savingsAccountType;
    private DialogFragment mfDatePicker;
    private SafeUIBlockingUtility safeUIBlockingUtility;

    public static SavingsAccountActivateFragment newInstance(int savingsAccountNumber,
                                                             DepositType type) {
        SavingsAccountActivateFragment savingsAccountApproval =
                new SavingsAccountActivateFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountNumber);
        args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, type);
        savingsAccountApproval.setArguments(args);
        return savingsAccountApproval;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            savingsAccountNumber = getArguments().getInt(Constants.SAVINGS_ACCOUNT_NUMBER);
            savingsAccountType = getArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.dialog_fragment_approve_savings, null);

        ButterKnife.bind(this, rootView);
        mSavingsAccountActivatePresenter.attachView(this);
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity(),
                getString(R.string.savings_account_loading_message));

        showUserInterface();

        return rootView;
    }

    @Override
    public void showUserInterface() {
        etSavingsActivateReason.setVisibility(View.GONE);
        tvActivateDateHeading.setText(getResources().getString(R.string.activated_on));
        mfDatePicker = MFDatePicker.newInsance(this);
        tvActivationDate.setText(MFDatePicker.getDatePickedAsString());
        activationDate = tvActivationDate.getText().toString();
        showActivationDate();
    }

    @OnClick(R.id.btn_approve_savings)
    void onClickActivateSavings() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("dateFormat", "dd MMMM yyyy");
        hashMap.put("activatedOnDate", activationDate);
        hashMap.put("locale", "en");
        mSavingsAccountActivatePresenter.activateSavings(savingsAccountNumber, hashMap);
    }

    @OnClick(R.id.tv_approval_date)
    void onClickApprovalDate() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @Override
    public void onDatePicked(String date) {
        tvActivationDate.setText(date);
        activationDate = date;
        showActivationDate();
    }

    public void showActivationDate() {
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationDate)
                .replace("-", " ");
    }

    @Override
    public void showSavingAccountActivatedSuccessfully(GenericResponse genericResponse) {
        Toaster.show(tvActivateDateHeading,
                getResources().getString(R.string.savings_account_activated));
        Toast.makeText(getActivity(), "Savings Activated", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            safeUIBlockingUtility.safelyBlockUI();
        } else {
            safeUIBlockingUtility.safelyUnBlockUI();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSavingsAccountActivatePresenter.detachView();
    }
}
