/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments.documentdialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.api.GenericResponse;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.utils.CheckSelfPermissionAndRequest;
import com.mifos.utils.Constants;
import com.mifos.utils.FileUtils;
import com.mifos.utils.SafeUIBlockingUtility;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ishankhanna on 04/07/14.
 * <p/>
 * Use this Dialog Fragment to Create and/or Update Documents
 */
public class DocumentDialogFragment extends DialogFragment implements DocumentDialogMvpView {

    private static final int FILE_SELECT_CODE = 0;

    private final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.et_document_name)
    EditText et_document_name;

    @BindView(R.id.et_document_description)
    EditText et_document_description;

    @BindView(R.id.tv_choose_file)
    TextView tv_choose_file;

    @BindView(R.id.bt_upload)
    Button bt_upload;

    @Inject
    DocumentDialogPresenter mDocumentDialogPresenter;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    private OnDialogFragmentInteractionListener mListener;

    private String documentName;
    private String documentDescription;
    private String entityType;
    private String filePath;
    private int entityId;
    private File fileChoosen;
    private Uri uri;

    public static DocumentDialogFragment newInstance(String entityType, int entiyId) {
        DocumentDialogFragment documentDialogFragment = new DocumentDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ENTITY_TYPE, entityType);
        args.putInt(Constants.ENTITY_ID, entiyId);
        documentDialogFragment.setArguments(args);
        return documentDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            entityType = getArguments().getString(Constants.ENTITY_TYPE);
            entityId = getArguments().getInt(Constants.ENTITY_ID);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        getDialog().setTitle(R.string.upload_document);
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        rootView = inflater.inflate(R.layout.dialog_fragment_document, container, false);

        ButterKnife.bind(this, rootView);
        mDocumentDialogPresenter.attachView(this);

        return rootView;
    }


    @OnClick(R.id.bt_upload)
    public void beginUpload() {
        try {
            validateInput();
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
        }
    }

    @OnClick(R.id.tv_choose_file)
    public void openFilePicker() {
        checkPermissionAndRequest();
    }

    public void validateInput() throws RequiredFieldException {

        documentName = et_document_name.getEditableText().toString();

        if (documentName.length() == 0 || documentName.equals(""))
            throw new RequiredFieldException(getResources().getString(R.string.name), getString(R
                    .string.message_field_required));

        documentDescription = et_document_description.getEditableText().toString();

        if (documentDescription.length() == 0)
            documentDescription = "";

        //Start Uploading Document
        mDocumentDialogPresenter.createDocument(entityType, entityId,
                documentName, documentDescription, fileChoosen);

    }

    @Override
    public void checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            getExternalStorageDocument();
        } else {
            CheckSelfPermissionAndRequest.requestPermission(
                    (MifosBaseActivity) getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Constants.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE,
                    getResources().getString(
                            R.string.dialog_message_read_external_storage_permission_denied),
                    Constants.READ_EXTERNAL_STORAGE_STATUS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    getExternalStorageDocument();

                } else {

                    // permission denied, boo! Disable the
                    Toaster.show(rootView, getResources()
                            .getString(R.string.permission_denied_to_read_external_document));
                }
                return;
            }
        }
    }

    @Override
    public void getExternalStorageDocument() {
        Intent intentDocument;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            intentDocument = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        }else{
            intentDocument = new Intent(Intent.ACTION_GET_CONTENT);
        }
        intentDocument.addCategory(Intent.CATEGORY_OPENABLE);
        intentDocument.setType("*/*");
        startActivityForResult(intentDocument, FILE_SELECT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    uri = data.getData();

                    filePath = FileUtils.getPathReal(getActivity(), uri);
                    if (filePath != null) {
                        fileChoosen = new File(filePath);
                    }

                    if (fileChoosen != null) {
                        tv_choose_file.setText(fileChoosen.getName());
                    } else {
                        break;
                    }
                    bt_upload.setEnabled(true);

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void showDocumentedCreatedSuccessfully(GenericResponse genericResponse) {
        if (genericResponse != null) {

            Toast.makeText(getActivity(), String.format(getString(R.string
                    .uploaded_successfully), fileChoosen.getName()), Toast
                    .LENGTH_SHORT).show();

            Log.d(LOG_TAG, genericResponse.toString());
        }
        getDialog().dismiss();
    }

    @Override
    public void showError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        getDialog().dismiss();
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
        mDocumentDialogPresenter.detachView();
    }

    public interface OnDialogFragmentInteractionListener {
        void initiateFileUpload(String name, String description);
    }


}
