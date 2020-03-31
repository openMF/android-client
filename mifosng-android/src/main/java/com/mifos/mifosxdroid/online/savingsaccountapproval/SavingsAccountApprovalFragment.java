/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.savingsaccountapproval;

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
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.loan.SavingsApproval;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.Network;
import com.mifos.utils.SafeUIBlockingUtility;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author nellyk
 */
public class SavingsAccountApprovalFragment extends MifosBaseFragment implements
        MFDatePicker.OnDatePickListener, SavingsAccountApprovalMvpView {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.tv_approval_date)
    TextView tvApprovalDate;

    @BindView(R.id.btn_approve_savings)
    Button btnApproveSavings;

    @BindView(R.id.et_savings_approval_reason)
    EditText etSavingsApprovalReason;

    @Inject
    SavingsAccountApprovalPresenter mSavingsAccountApprovalPresenter;

    View rootView;

    String approvaldate;
    public int savingsAccountNumber;
    public DepositType savingsAccountType;
    private DialogFragment mfDatePicker;
    private SafeUIBlockingUtility safeUIBlockingUtility;

    public static SavingsAccountApprovalFragment newInstance(int savingsAccountNumber,
                                                             DepositType type) {
        SavingsAccountApprovalFragment savingsAccountApproval =
                new SavingsAccountApprovalFragment();
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
        mSavingsAccountApprovalPresenter.attachView(this);
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity(),
                getString(R.string.savings_account_approval_fragment_loading_message));

        showUserInterface();

        return rootView;
    }

    @Override
    public void showUserInterface() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tvApprovalDate.setText(MFDatePicker.getDatePickedAsString());
        approvaldate = tvApprovalDate.getText().toString();
        showApprovalDate();
    }

    @OnClick(R.id.btn_approve_savings)
    void onClickApproveSavings() {
        if (Network.isOnline(getContext())) {
            SavingsApproval savingsApproval = new SavingsApproval();
            savingsApproval.setNote(etSavingsApprovalReason.getEditableText().toString());
            savingsApproval.setApprovedOnDate(approvaldate);
            initiateSavingsApproval(savingsApproval);
        } else {
            Toast.makeText(getContext(),
                    getResources().getString(R.string.error_network_not_available),
                    Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.tv_approval_date)
    void onClickApprovalDate() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @Override
    public void onDatePicked(String date) {
        tvApprovalDate.setText(date);
        approvaldate = date;
        showApprovalDate();
    }

    public void showApprovalDate() {
        approvaldate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(approvaldate)
                .replace("-", " ");
    }

    private void initiateSavingsApproval(final SavingsApproval savingsApproval) {
        mSavingsAccountApprovalPresenter.approveSavingsApplication(
                savingsAccountNumber, savingsApproval);
    }

    @Override
    public void showSavingAccountApprovedSuccessfully(GenericResponse genericResponse) {
        Toast.makeText(getActivity(), "Savings Approved", Toast.LENGTH_LONG).show();
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
        mSavingsAccountApprovalPresenter.detachView();
    }
}
