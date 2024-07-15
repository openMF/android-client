/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.documentlist

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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.noncore.Document
import com.mifos.exceptions.RequiredFieldException
import com.mifos.feature.document.document_dialog.DocumentDialogViewModel
import com.mifos.feature.document.document_list.DocumentListScreen
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.utils.AndroidVersionUtil
import com.mifos.utils.CheckSelfPermissionAndRequest
import com.mifos.utils.Constants
import com.mifos.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DocumentListFragment : MifosBaseFragment() {

    private lateinit var viewModel: DocumentDialogViewModel
    private val arg: DocumentListFragmentArgs by navArgs()
    private lateinit var entityType: String
    private var entityId = 0
    private var filePath: String? = null
    private var documentAction: String? = null
    private var document: Document? = null
    private var fileChoosen: File? = null
    private var uri: Uri? = null
    private val FILE_SELECT_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DocumentDialogViewModel::class.java]
        entityId = arg.entiyId
        entityType = arg.entityType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                DocumentListScreen(
                    entityType = entityType,
                    entityId = entityId,
                    onBackPressed = { requireActivity().onBackPressed() },
                    documentAction = documentAction,
                    document = document,
                    openFilePicker = { openFilePicker() },
                    uploadDocument = { documentName, documentDescription, docAction ->
                        uploadDocument(documentName, documentDescription, docAction)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    private fun openFilePicker() {
        checkPermissionAndRequest()
    }

    @Throws(RequiredFieldException::class)
    fun uploadDocument(documentName: String, documentDescription: String, docAction: String) {

        if (docAction == resources.getString(R.string.update_document)) {
            viewModel.updateDocument(
                entityType, entityId, document!!.id,
                documentName, documentDescription, fileChoosen!!
            )
        } else if (docAction == resources.getString(R.string.upload_document)) {
            viewModel.createDocument(
                entityType, entityId,
                documentName, documentDescription, fileChoosen!!
            )
        }
    }

    /**
     * This Method Validating the Name and Description String if any of them is null them throw
     * an RequiredFieldException, Otherwise start uploading Document.
     *
     * @throws RequiredFieldException
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
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    getExternalStorageDocument()
                } else {
                    //Toaster.show( , resources.getString(R.string.permission_denied_to_read_external_document))
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
                uri = data?.data
                filePath = FileUtils.getPathReal(requireContext(), uri!!)
                if (filePath != null) {
                    fileChoosen = File(filePath)
                }
                if (fileChoosen != null) {
                    viewModel.setFilePath(fileChoosen!!.name)
                } else {
                    return
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    private fun showDocumentDialog(documentAction: String) {
//        val documentDialogFragment =
//            DocumentDialogComposeFragment.newInstance(entityType, entityId, documentAction, Document())
//        val fragmentTransaction = requireActivity().supportFragmentManager
//            .beginTransaction()
//        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST)
//        documentDialogFragment.show(fragmentTransaction, "Document Dialog Fragment")
//    }
}