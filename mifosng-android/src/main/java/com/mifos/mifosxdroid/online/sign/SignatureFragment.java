package com.mifos.mifosxdroid.online.sign;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.views.SignatureView;
import com.mifos.utils.AndroidVersionUtil;
import com.mifos.utils.CheckSelfPermissionAndRequest;
import com.mifos.utils.Constants;
import com.mifos.utils.FileUtils;
import com.mifos.utils.SafeUIBlockingUtility;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tarun on 28-06-2017.
 */

public class SignatureFragment extends MifosBaseFragment implements
        SignatureMvpView, BottomNavigationView.OnNavigationItemSelectedListener,
        SignatureView.OnSignatureSaveListener  {

    public static final String LOG_TAG = SignatureFragment.class.getSimpleName();
    private static final int FILE_SELECT_CODE = 0;

    @BindView(R.id.sign_view)
    SignatureView signView;

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    @Inject
    SignaturePresenter mSignaturePresenter;

    private Integer mClientId;
    private View rootView;
    private File signatureFile;
    private SafeUIBlockingUtility safeUIBlockingUtility;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity(),
                getString(R.string.signature_fragment_loading_message));
        if (getArguments() != null) {
            mClientId = getArguments().getInt(Constants.CLIENT_ID);
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sign, container, false);
        ButterKnife.bind(this, rootView);
        mSignaturePresenter.attachView(this);
        showInterface();

        return rootView;
    }

    private void showInterface() {
        setToolbarTitle(getString(R.string.upload_sign));
        signView.setOnSignatureSaveListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemIconTintList(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.signature_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_reset_sign:
                signView.clear();
                break;

            case R.id.btn_from_gallery:
                getDocumentFromGallery();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_upload_sign:
                if (AndroidVersionUtil.isApiVersionGreaterOrEqual(Build.VERSION_CODES.M)) {
                    checkPermissionAndRequest();
                } else {
                    saveAndUploadSignature();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method checks the permission WRITE_EXTERNAL_STORAGE is granted or not.
     * If not, it prompts the user a dialog to grant the WRITE_EXTERNAL_STORAGE permission.
     * If the permission is granted already then save the signature in external storage;
     */
    @Override
    public void checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            saveAndUploadSignature();
        } else {
            requestPermission();
        }
    }

    @Override
    public void requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
                (MifosBaseActivity) getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                getResources().getString(
                        R.string.dialog_message_write_external_storage_permission_denied),
                getResources().getString(R.string.dialog_message_permission_never_ask_again_write),
                Constants.WRITE_EXTERNAL_STORAGE_STATUS);
    }

    /**
     * This method handles the response after user grants or denies the permission.
     *
     * @param requestCode  Request Code
     * @param permissions  Permission
     * @param grantResults GrantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveAndUploadSignature();

                } else {
                    Toaster.show(rootView, getResources()
                            .getString(R.string.permission_denied_to_write_external_document));
                }
            }
        }
    }

    @Override
    public void getDocumentFromGallery() {
        Intent intentDocument;
        if (AndroidVersionUtil.isApiVersionGreaterOrEqual(Build.VERSION_CODES.KITKAT)) {
            intentDocument = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intentDocument = new Intent(Intent.ACTION_GET_CONTENT);
        }
        intentDocument.addCategory(Intent.CATEGORY_OPENABLE);
        intentDocument.setType("image/*");
        startActivityForResult(intentDocument, FILE_SELECT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();

                    String filePath = FileUtils.getPathReal(getActivity(), uri);
                    if (filePath != null) {
                        signatureFile = new File(filePath);
                    }

                    if (signatureFile != null) {
                        uploadSignImage();
                    } else {
                        break;
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadSignImage() {
        showProgressbar(true);
        mSignaturePresenter.createDocument(Constants.ENTITY_TYPE_CLIENTS,
                mClientId, signatureFile.getName(), "Signature", signatureFile);
    }

    @Override
    public void onSignSavedError(String errorMsg) {
        Toaster.show(rootView, errorMsg);
    }

    @Override
    public void onSignSavedSuccess(String absoluteFilePath) {
        Uri signImageUri = Uri.parse("file://" + absoluteFilePath);
        signatureFile = new File(signImageUri.getPath());
        Toaster.show(rootView, getString(R.string.sign_saved_success_msg)
                + signImageUri.getPath());
        uploadSignImage();
    }

    @Override
    public void showSignatureUploadedSuccessfully(GenericResponse response) {
        showProgressbar(false);
        Toaster.show(rootView, R.string.sign_uploaded_success_msg);
    }

    @Override
    public void showError(int errorId) {
        showProgressbar(false);
        Toaster.show(rootView, getStringMessage(errorId));
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
    public void saveAndUploadSignature() {
        if (signView.getXCoordinateSize() > 0 && signView.getYCoordinateSize() > 0) {
            signView.saveSignature(mClientId);
        } else {
            Toaster.show(rootView, R.string.empty_signature);
        }
    }
}
