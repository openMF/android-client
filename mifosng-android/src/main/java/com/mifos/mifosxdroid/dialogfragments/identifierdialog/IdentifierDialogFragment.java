package com.mifos.mifosxdroid.dialogfragments.identifierdialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
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
import com.mifos.objects.noncore.DocumentType;
import com.mifos.objects.noncore.Identifier;
import com.mifos.objects.noncore.IdentifierCreationResponse;
import com.mifos.objects.noncore.IdentifierPayload;
import com.mifos.objects.noncore.IdentifierTemplate;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rajan Maurya on 01/10/16.
 */
public class IdentifierDialogFragment extends ProgressableDialogFragment implements
        IdentifierDialogMvpView, AdapterView.OnItemSelectedListener {

    @BindView(R.id.sp_identifier_type)
    Spinner spIdentifierType;

    @BindView(R.id.sp_identifier_status)
    Spinner spIdentifierStatus;

    @BindView(R.id.et_description)
    EditText etDescription;

    @BindView(R.id.btn_create_identifier)
    Button btnIdentifier;

    @BindView(R.id.et_unique_id)
    EditText etUniqueId;

    @BindArray(R.array.status)
    String[] identifierStatus;

    @Inject
    IdentifierDialogPresenter mIdentifierDialogPresenter;

    @Nullable
    private ClientIdentifierCreationListener clientIdentifierCreationListener;

    private View rootView;
    private int clientId;
    private IdentifierTemplate identifierTemplate;
    private int identifierDocumentTypeId;
    private String status;
    private Identifier identifier;
    private HashMap<String, DocumentType> documentTypeHashMap;

    private List<String> mListIdentifierType = new ArrayList<>();

    private ArrayAdapter<String> mIdentifierTypeAdapter;
    private ArrayAdapter<String> mIdentifierStatusAdapter;

    public static IdentifierDialogFragment newInstance(int clientId) {
        IdentifierDialogFragment documentDialogFragment = new IdentifierDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        documentDialogFragment.setArguments(args);
        return documentDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_fragment_identifier, container, false);

        ButterKnife.bind(this, rootView);
        mIdentifierDialogPresenter.attachView(this);

        showIdentifierSpinners();

        mIdentifierDialogPresenter.loadClientIdentifierTemplate(clientId);

        return rootView;
    }


    @Override
    public void showIdentifierSpinners() {

        mIdentifierTypeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, mListIdentifierType);
        mIdentifierTypeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIdentifierType.setAdapter(mIdentifierTypeAdapter);
        spIdentifierType.setOnItemSelectedListener(this);

        mIdentifierStatusAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, identifierStatus);
        mIdentifierStatusAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIdentifierStatus.setAdapter(mIdentifierStatusAdapter);
        spIdentifierStatus.setOnItemSelectedListener(this);

    }

    @OnClick(R.id.btn_create_identifier)
    void onClickCreateIdentifier() {
        if (etUniqueId.getText().toString().trim().equals("")) {
            etUniqueId.setError(getResources().getString(R.string.unique_id_required));
        } else if (mListIdentifierType.size() == 0) {
            showError(R.string.empty_identifier_document_type);
        } else {
            hideKeyboard(btnIdentifier);
            IdentifierPayload identifierPayload = new IdentifierPayload();
            identifierPayload.setDocumentTypeId(identifierDocumentTypeId);
            identifierPayload.setStatus(status);
            identifierPayload.setDocumentKey(etUniqueId.getText().toString());
            identifierPayload.setDescription(etDescription.getText().toString());

            // Add the values in the identifier. It'll be sent to the calling Fragment
            // if the request is successful.
            identifier = new Identifier();
            identifier.setDescription(etDescription.getText().toString());
            identifier.setDocumentKey(etUniqueId.getText().toString());
            identifier.setDocumentType(documentTypeHashMap
                    .get(spIdentifierType.getSelectedItem().toString()));
            mIdentifierDialogPresenter.createClientIdentifier(clientId, identifierPayload);
        }
    }

    @Override
    public void showClientIdentifierTemplate(IdentifierTemplate identifierTemplate) {

        this.identifierTemplate = identifierTemplate;
        mListIdentifierType.addAll(mIdentifierDialogPresenter.getIdentifierDocumentTypeNames
                (identifierTemplate.getAllowedDocumentTypes()));
        documentTypeHashMap = mIdentifierDialogPresenter
                .mapDocumentTypesWithName(identifierTemplate.getAllowedDocumentTypes());
        mIdentifierTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showIdentifierCreatedSuccessfully(
            IdentifierCreationResponse identifierCreationResponse) {
        Toast.makeText(getActivity(), R.string.identifier_created_successfully,
                Toast.LENGTH_SHORT).show();
        identifier.setClientId(identifierCreationResponse.getClientId());
        identifier.setId(identifierCreationResponse.getResourceId());
        if (clientIdentifierCreationListener != null) {
            clientIdentifierCreationListener.onClientIdentifierCreationSuccess(identifier);
        }
        getDialog().dismiss();
    }

    @Override
    public void showErrorMessage(String message) {
        if (clientIdentifierCreationListener != null) {
            clientIdentifierCreationListener.onClientIdentifierCreationFailure(message);
        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showError(int errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean show) {
        showProgress(show);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIdentifierDialogPresenter.detachView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_identifier_type:
                identifierDocumentTypeId = identifierTemplate.getAllowedDocumentTypes()
                        .get(position).getId();
                break;
            case R.id.sp_identifier_status:
                status = identifierStatus[position];
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setOnClientIdentifierCreationListener(
            @Nullable ClientIdentifierCreationListener listener) {
        clientIdentifierCreationListener = listener;
    }
}
