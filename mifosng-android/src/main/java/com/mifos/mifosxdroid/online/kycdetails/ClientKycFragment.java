package com.mifos.mifosxdroid.online.kycdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.fragment.app.FragmentTransaction;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;

import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersFragment;
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment;
import com.mifos.mifosxdroid.views.CircularImageView;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientKycFragment extends MifosBaseFragment implements CreateNewKycMvpView {

    private final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.iv_clientImage)
    CircularImageView ivClientImage;

    @BindView(R.id.sp_kyc_level)
    Spinner spKycLevel;

    @BindView(R.id.et_client_mobile_no)
    EditText etMobileNumber;

    @BindView(R.id.et_client_address)
    EditText etAddress;

    private View rootview;
    private int clientId;
    private List<String> kycList;
    private ArrayAdapter<CharSequence> kycOptionAdapter;

    public static ClientKycFragment newInstance(int clientId) {
        ClientKycFragment fragment = new ClientKycFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_kyc_details, container, false);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        ButterKnife.bind(this, rootview);
        showUserInterface();
        return rootview;
    }


    @Override
    public void showKycInformation(final Client client) {
        if (client != null) {
            setToolbarTitle(getString(R.string.client) + " - " + client.getDisplayName());
            getActivity().invalidateOptionsMenu();
            etMobileNumber.setText(client.getMobileNo());
            client.setMobileNo(etMobileNumber.toString());
        }
    }

    @Override
    public void showUserInterface() {
        kycOptionAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.kyc_level_values, android.R.layout.simple_spinner_item);
        kycOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKycLevel.setAdapter(kycOptionAdapter);
        spKycLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spKycLevel.setSelection(i);

                if (spKycLevel.getSelectedItem().toString().equals("Level 2")) {
                    LinearLayout kycLevel2 = rootview.findViewById(R.id.kyc_level_2);
                    kycLevel2.setVisibility(View.VISIBLE);
                }
                if (spKycLevel.getSelectedItem().toString().equals("Level 3")) {
                    LinearLayout kycLevel3 = rootview.findViewById(R.id.kyc_level_3);
                    kycLevel3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @OnClick(R.id.btn_upload_document)
    public void onClickUploadDocumentButton() {
        loadDocuments();
    }

    @OnClick(R.id.btn_upload_identifier)
    public void onClickUploadButton() {
        loadIdentifiers();
    }

    public void loadDocuments() {
        DocumentListFragment documentListFragment = DocumentListFragment.newInstance(Constants
                .ENTITY_TYPE_CLIENTS, clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, documentListFragment);
        fragmentTransaction.commit();
    }

    public void loadIdentifiers() {
        ClientIdentifiersFragment clientIdentifiersFragment = ClientIdentifiersFragment
                .newInstance(clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, clientIdentifiersFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showProgressbar(boolean show) {
    }
}
