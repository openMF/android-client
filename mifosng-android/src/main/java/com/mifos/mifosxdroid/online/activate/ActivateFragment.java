package com.mifos.mifosxdroid.online.activate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.databinding.FragmentActivateClientBinding;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.ActivatePayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import javax.inject.Inject;


/**
 * Created by Rajan Maurya on 09/02/17.
 */
public class ActivateFragment extends MifosBaseFragment implements ActivateMvpView,
        MFDatePicker.OnDatePickListener {

    public static final String LOG_TAG = ActivateFragment.class.getSimpleName();
    private FragmentActivateClientBinding binding;

    @Inject
    ActivatePresenter activatePresenter;


    private DialogFragment mfDatePicker;
    private String activationDate;
    private int id;
    private String activateType;

    public static ActivateFragment newInstance(int id, String activationType) {
        ActivateFragment activateFragment = new ActivateFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ID, id);
        args.putString(Constants.ACTIVATE_TYPE, activationType);
        activateFragment.setArguments(args);
        return activateFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            id = getArguments().getInt(Constants.ID);
            activateType = getArguments().getString(Constants.ACTIVATE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentActivateClientBinding
                .inflate(inflater, container, false);
        activatePresenter.attachView(this);
        showUserInterface();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnActivate.setOnClickListener(view1 -> onClickActivationButton());
        binding.tvActivationDate.setOnClickListener(view1 -> onClickTextViewActivationDate());
    }

    @Override
    public void showUserInterface() {
        setToolbarTitle(getString(R.string.activate));
        mfDatePicker = MFDatePicker.newInsance(this);
        binding.tvActivationDate.setText(MFDatePicker.getDatePickedAsString());
        activationDate = binding.tvActivationDate.getText().toString();
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationDate)
                .replace("-", " ");
    }

    void onClickActivationButton() {
        ActivatePayload clientActivate = new ActivatePayload(activationDate);
        activate(clientActivate);
    }

    void onClickTextViewActivationDate() {
        mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                .DFRAG_DATE_PICKER);
    }

    @Override
    public void onDatePicked(String date) {
        binding.tvActivationDate.setText(date);
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
                .replace("-", " ");
    }

    public void activate(ActivatePayload clientActivate) {
        switch (activateType) {
            case Constants.ACTIVATE_CLIENT:
                activatePresenter.activateClient(id, clientActivate);
                break;
            case Constants.ACTIVATE_CENTER:
                activatePresenter.activateCenter(id, clientActivate);
                break;
            case Constants.ACTIVATE_GROUP:
                activatePresenter.activateGroup(id, clientActivate);
                break;
            default:
                break;
        }
    }

    @Override
    public void showActivatedSuccessfully(int message) {
        Toast.makeText(getActivity(),
                getString(message), Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showError(String errorMessage) {
        Toaster.show(binding.getRoot(), errorMessage, Toaster.INDEFINITE);
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
        activatePresenter.detachView();
    }
}
