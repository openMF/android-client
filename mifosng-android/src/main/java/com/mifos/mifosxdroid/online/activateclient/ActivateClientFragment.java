package com.mifos.mifosxdroid.online.activateclient;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.ClientActivate;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rajan Maurya on 09/02/17.
 */
public class ActivateClientFragment extends MifosBaseFragment implements ActivateClientMvpView,
        MFDatePicker.OnDatePickListener {

    public static final String LOG_TAG = ActivateClientFragment.class.getSimpleName();

    @BindView(R.id.tv_activation_client_date)
    TextView tvActivationClientDate;

    @Inject
    ActivateClientPresenter activateClientPresenter;

    View rootView;

    private DialogFragment mfDatePicker;
    private String activationDate;
    private int clientId;

    public static ActivateClientFragment newInstance(int clientId) {
        ActivateClientFragment activateClientFragment = new ActivateClientFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        activateClientFragment.setArguments(args);
        return activateClientFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_activate_client, container, false);
        ButterKnife.bind(this, rootView);
        activateClientPresenter.attachView(this);
        showUserInterface();
        return rootView;
    }

    @Override
    public void showUserInterface() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tvActivationClientDate.setText(MFDatePicker.getDatePickedAsString());
        activationDate = tvActivationClientDate.getText().toString();
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationDate)
                .replace("-", " ");
    }

    @OnClick(R.id.btn_activate_client)
    void onClickActivationButton() {
        ClientActivate clientActivate = new ClientActivate(activationDate);
        activateClientPresenter.activateClient(clientId, clientActivate);
    }

    @OnClick(R.id.tv_activation_client_date)
    void onClickTextViewActivationDate() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @Override
    public void onDatePicked(String date) {
        tvActivationClientDate.setText(date);
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
                .replace("-", " ");
    }

    @Override
    public void showClientActivatedSuccessfully() {
        Toast.makeText(getActivity(),
                R.string.client_activated_successfully, Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showError(String errorMessage) {
        Toaster.show(rootView, errorMessage, Toaster.INDEFINITE);
    }

    @Override
    public void showProgressbar(boolean show) {
        if (show) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activateClientPresenter.detachView();
    }
}
