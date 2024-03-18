/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.dialogfragments.documentdialog

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.Document
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.DialogFragmentDocumentBinding
import com.mifos.utils.AndroidVersionUtil
import com.mifos.utils.CheckSelfPermissionAndRequest
import com.mifos.utils.Constants
import com.mifos.utils.FileUtils
import com.mifos.utils.SafeUIBlockingUtility
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * Created by ishankhanna on 04/07/14.
 *
 *
 * Use this Dialog Fragment to Create and/or Update Documents
 */
@AndroidEntryPoint
class DocumentDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentDocumentBinding

    private lateinit var viewModel: DocumentDialogViewModel

    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    private var documentName: String? = null
    private var documentDescription: String? = null
    private var entityType: String? = null
    private var filePath: String? = null
    private var documentAction: String? = null
    private var document: Document? = null
    private var entityId = 0
    private var fileChoosen: File? = null
    private var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        safeUIBlockingUtility = SafeUIBlockingUtility(
            requireContext(),
            getString(R.string.document_dialog_fragment_loading_message)
        )
        if (arguments != null) {
            entityType = requireArguments().getString(Constants.ENTITY_TYPE)
            entityId = requireArguments().getInt(Constants.ENTITY_ID)
            documentAction = requireArguments().getString(Constants.DOCUMENT_ACTIONS)
            document = requireArguments().getParcelable(Constants.DOCUMENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentDocumentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DocumentDialogViewModel::class.java]
        if (resources.getString(R.string.update_document) == documentAction) {
            binding.tvDocumentAction.setText(R.string.update_document)
            binding.etDocumentName.setText(document?.name)
            binding.etDocumentDescription.setText(document?.description)
        } else if (resources.getString(R.string.upload_document) == documentAction) {
            binding.tvDocumentAction.setText(R.string.upload_document)
        }
        binding.btUpload.isEnabled = false

        viewModel.documentDialogUiState.observe(viewLifecycleOwner) {
            when (it) {
                is DocumentDialogUiState.ShowDocumentUpdatedSuccessfully -> {
                    showProgressbar(false)
                    showDocumentUpdatedSuccessfully()
                }

                is DocumentDialogUiState.ShowDocumentedCreatedSuccessfully -> {
                    showProgressbar(false)
                    showDocumentedCreatedSuccessfully(it.genericResponse)
                }

                is DocumentDialogUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is DocumentDialogUiState.ShowProgressbar -> showProgressbar(true)

                is DocumentDialogUiState.ShowUploadError -> {
                    showProgressbar(false)
                    showUploadError(it.message)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btUpload.setOnClickListener {
            beginUpload()
        }

        binding.btnBrowseDocument.setOnClickListener {
            openFilePicker()
        }
    }


    private fun beginUpload() {
        try {
            validateInput()
        } catch (e: RequiredFieldException) {
            e.notifyUserWithToast(activity)
        }
    }

    private fun openFilePicker() {
        checkPermissionAndRequest()
    }

    /**
     * This Method Validating the Name and Description String if any of them is null them throw
     * an RequiredFieldException, Otherwise start uploading Document.
     *
     * @throws RequiredFieldException
     */
    @Throws(RequiredFieldException::class)
    fun validateInput() {
        documentName = binding.etDocumentName.editableText.toString()
        if (documentName?.isEmpty() == true || documentName == "") throw RequiredFieldException(
            resources.getString(R.string.name),
            getString(R.string.message_field_required)
        )
        documentDescription = binding.etDocumentDescription.editableText.toString()
        if (documentDescription?.isEmpty() == true || documentDescription == "") throw RequiredFieldException(
            resources.getString(R.string.description),
            getString(R.string.message_field_required)
        )

        //Start Uploading Document
        if (documentAction == resources.getString(R.string.update_document)) {
            viewModel.updateDocument(
                entityType, entityId, document!!.id,
                documentName, documentDescription, fileChoosen!!
            )
        } else if (documentAction == resources.getString(R.string.upload_document)) {
            viewModel.createDocument(
                entityType, entityId,
                documentName, documentDescription, fileChoosen!!
            )
        }
    }

    /**
     * This Method Checking the Permission READ_EXTERNAL_STORAGE is granted or not.
     * If not then prompt user a dialog to grant the READ_EXTERNAL_STORAGE permission.
     * and If Permission is granted already then start Intent to get the Document from the External
     * Storage.
     */
    private fun checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            getExternalStorageDocument()
        } else {
            requestPermission()
        }
    }

    /**
     * This Method is Requesting the Permission
     */
    private fun requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
            activity as MifosBaseActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Constants.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE,
            resources.getString(
                R.string.dialog_message_read_external_storage_permission_denied
            ),
            resources.getString(R.string.dialog_message_permission_never_ask_again_read),
            Constants.READ_EXTERNAL_STORAGE_STATUS
        )
    }

    /**
     * This Method getting the Response after User Grant or denied the Permission
     *
     * @param requestCode  Request Code
     * @param permissions  Permission
     * @param grantResults GrantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the
                    getExternalStorageDocument()
                } else {

                    // permission denied, boo! Disable the
                    show(
                        binding.root, resources
                            .getString(R.string.permission_denied_to_read_external_document)
                    )
                }
            }
        }
    }

    /**
     * This method is to start an intent(getExternal Storage Document).
     * If Android Version is Kitkat or greater then start intent with ACTION_OPEN_DOCUMENT,
     * otherwise with ACTION_GET_CONTENT
     */
    private fun getExternalStorageDocument() {
        val intentDocument: Intent =
            if (AndroidVersionUtil.isApiVersionGreaterOrEqual(Build.VERSION_CODES.KITKAT)) {
                Intent(Intent.ACTION_OPEN_DOCUMENT)
            } else {
                Intent(Intent.ACTION_GET_CONTENT)
            }
        intentDocument.addCategory(Intent.CATEGORY_OPENABLE)
        intentDocument.type = "*/*"
        startActivityForResult(intentDocument, FILE_SELECT_CODE)
    }

    /**
     * This Method will be called when any document will be selected from the External Storage.
     *
     * @param requestCode Request Code
     * @param resultCode  Result Code ok or Cancel
     * @param data        Intent Data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> if (resultCode == Activity.RESULT_OK) {
                // Get the Uri of the selected file
                uri = data?.data
                filePath = FileUtils.getPathReal(requireContext(), uri!!)
                if (filePath != null) {
                    fileChoosen = File(filePath)
                }
                if (fileChoosen != null) {
                    binding.tvChooseFile.setText(fileChoosen?.name)
                } else {
                    return
                }
                binding.btUpload.isEnabled = true
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDocumentedCreatedSuccessfully(genericResponse: GenericResponse) {
        Toast.makeText(
            activity, String.format(getString(R.string.uploaded_successfully), fileChoosen?.name),
            Toast.LENGTH_SHORT
        ).show()
        dialog?.dismiss()
    }

    private fun showDocumentUpdatedSuccessfully() {
        Toast.makeText(
            activity,
            String.format(getString(R.string.document_updated_successfully), fileChoosen?.name),
            Toast.LENGTH_SHORT
        ).show()
        dialog?.dismiss()
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }

    private fun showUploadError(errorMessage: String) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility?.safelyBlockUI()
        } else {
            safeUIBlockingUtility?.safelyUnBlockUI()
        }
    }

    interface OnDialogFragmentInteractionListener {
        fun initiateFileUpload(name: String?, description: String?)
    }

    companion object {
        private const val FILE_SELECT_CODE = 0
        fun newInstance(
            entityType: String?, entityId: Int,
            documentAction: String?,
            document: Document?
        ): DocumentDialogFragment {
            val documentDialogFragment = DocumentDialogFragment()
            val args = Bundle()
            args.putString(Constants.ENTITY_TYPE, entityType)
            args.putInt(Constants.ENTITY_ID, entityId)
            args.putString(Constants.DOCUMENT_ACTIONS, documentAction)
            args.putParcelable(Constants.DOCUMENT, document)
            documentDialogFragment.arguments = args
            return documentDialogFragment
        }
    }
}