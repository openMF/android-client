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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private final String LOG_TAG = getClass().getSimpleName();

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

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

    public void validateInput() throws RequiredFieldException {

        documentName = et_document_name.getEditableText().toString();

        if (documentName == null || documentName.equals(""))
            throw new RequiredFieldException(getResources().getString(R.string.name), getString(R
                    .string.message_field_required));

        documentDescription = et_document_description.getEditableText().toString();

        if (documentDescription == null)
            documentDescription = "";

        uploadFile();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    uri = data.getData();


                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    }




                    filePath = FileUtils.getPathReal(getActivity(), uri);
                    fileChoosen = new File(filePath);


                    Log.d(LOG_TAG, "File Uri: " + uri.toString());


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

    @OnClick(R.id.tv_choose_file)
    public void openFilePicker() {
        //TODO check android version and fire accordingly intent,
        //TODO use ACTION_PICK_CONTENT for below 4.3 version
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    public void uploadFile() {

        mDocumentDialogPresenter.createDocument(entityType, entityId,
                documentName, documentDescription, fileChoosen);
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

        public void initiateFileUpload(String name, String description);

    }


}
