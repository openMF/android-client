/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.services.API;
import com.mifos.services.GenericResponse;
import com.mifos.utils.Constants;
import com.mifos.utils.FileUtils;
import com.mifos.utils.SafeUIBlockingUtility;

import java.io.File;
import java.net.URISyntaxException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by ishankhanna on 04/07/14.
 *
 * Use this Dialog Fragment to Create and/or Update Documents
 */
public class DocumentDialogFragment extends DialogFragment {

    public static final String TAG = "DocumentDialogFragment";
    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    private OnDialogFragmentInteractionListener mListener;

    @InjectView(R.id.et_document_name) EditText et_document_name;
    @InjectView(R.id.et_document_description) EditText et_document_description;
    @InjectView(R.id.tv_choose_file) TextView tv_choose_file;
    @InjectView(R.id.bt_upload) Button bt_upload;

    private static final int FILE_SELECT_CODE = 0;

    private String entityType;

    private int entityId;

    private String documentName;
    private String documentDescription;

    private String filePath;

    private File fileChoosen;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle(R.string.upload_document);
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        rootView = inflater.inflate(R.layout.document_dialog_fragment, container, false);

        ButterKnife.inject(this,rootView);

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

    public void validateInput() throws RequiredFieldException{

        documentName = et_document_name.getEditableText().toString();

        if (documentName == null || documentName.equals(""))
            throw new RequiredFieldException(getResources().getString(R.string.name), getString(R.string.message_field_required));

        documentDescription = et_document_description.getEditableText().toString();

        if (documentDescription == null)
            documentDescription = "";

        uploadFile();

    }

    public interface OnDialogFragmentInteractionListener {

        public void initiateFileUpload(String name, String description);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();

                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    try {

                        String scheme = uri.getScheme();

                        if (scheme.equals("file")) {
                            filePath = FileUtils.getPath(getActivity(), uri);
                            fileChoosen = new File(filePath);
                            Log.d(TAG, "File Path: " + filePath);
                        } else if (scheme.equals("content")) {

                            Toast.makeText(getActivity(), "The application currently does not support file picking from apps other than File Managers.",
                                    Toast.LENGTH_SHORT).show();
                            resultCode = Activity.RESULT_CANCELED;
                        }

                        if(fileChoosen!=null) {
                            tv_choose_file.setText(fileChoosen.getName());
                        } else {
                            break;
                        }
                        bt_upload.setEnabled(true);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.tv_choose_file)
    public void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getActivity(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadFile() {


        String[] parts = fileChoosen.getName().split("\\.");
        System.out.println("Extension :"+parts[1]);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(parts[1]);
        System.out.println("Mime Type = "+mimeType);

        TypedFile typedFile = new TypedFile(mimeType, fileChoosen);

        safeUIBlockingUtility.safelyBlockUI();
        API.changeRestAdapterLogLevel(RestAdapter.LogLevel.FULL);
        API.documentService.createDocument(entityType, entityId, documentName, documentDescription,
                typedFile, new Callback<GenericResponse>() {
                    @Override
                    public void success(GenericResponse genericResponse, Response response) {

                        if (genericResponse != null) {

                            Toast.makeText(getActivity(), String.format(getString(R.string.uploaded_successfully), fileChoosen.getName()), Toast.LENGTH_SHORT).show();

                            System.out.println(genericResponse.toString());
                        }
                        safeUIBlockingUtility.safelyUnBlockUI();
                        getDialog().dismiss();

                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                        Toast.makeText(getActivity(), getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                        safeUIBlockingUtility.safelyUnBlockUI();
                        getDialog().dismiss();


                    }
                }
        );

    }



}
