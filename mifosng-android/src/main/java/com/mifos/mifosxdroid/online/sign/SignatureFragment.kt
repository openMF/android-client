package com.mifos.mifosxdroid.online.sign

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mifos.core.common.utils.Constants
import com.mifos.core.network.GenericResponse
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentSignBinding
import com.mifos.mifosxdroid.views.SignatureView.OnSignatureSaveListener
import com.mifos.utils.AndroidVersionUtil
import com.mifos.utils.CheckSelfPermissionAndRequest
import com.mifos.utils.FileUtils
import com.mifos.utils.SafeUIBlockingUtility
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * Created by Tarun on 28-06-2017.
 */
@AndroidEntryPoint
class SignatureFragment : MifosBaseFragment(),
    BottomNavigationView.OnNavigationItemSelectedListener, OnSignatureSaveListener {

    private lateinit var binding: FragmentSignBinding
    private val arg: SignatureFragmentArgs by navArgs()

    private lateinit var viewModel: SignatureViewModel

    private var mClientId: Int? = null
    private var signatureFile: File? = null
    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        safeUIBlockingUtility = SafeUIBlockingUtility(
            requireContext(),
            getString(R.string.signature_fragment_loading_message)
        )
        mClientId = arg.clientId
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SignatureViewModel::class.java]
        showInterface()

        viewModel.signatureUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SignatureUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SignatureUiState.ShowProgressbar -> showProgressbar(true)
                is SignatureUiState.ShowSignatureUploadedSuccessfully -> {
                    showProgressbar(false)
                    showSignatureUploadedSuccessfully(it.genericResponse)
                }
            }
        }

        return binding.root
    }

    private fun showInterface() {
        setToolbarTitle(getString(R.string.upload_sign))
        binding.signView.setOnSignatureSaveListener(this)
        binding.navigation.setOnNavigationItemSelectedListener(this)
        binding.navigation.itemIconTintList = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.signature_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_reset_sign -> binding.signView.clear()
            R.id.btn_from_gallery -> documentFromGallery
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_upload_sign -> if (AndroidVersionUtil.isApiVersionGreaterOrEqual(Build.VERSION_CODES.M)) {
                checkPermissionAndRequest()
            } else {
                saveAndUploadSignature()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * This method checks the permission WRITE_EXTERNAL_STORAGE is granted or not.
     * If not, it prompts the user a dialog to grant the WRITE_EXTERNAL_STORAGE permission.
     * If the permission is granted already then save the signature in external storage;
     */
    private fun checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            saveAndUploadSignature()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
            activity as MifosBaseActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
            resources.getString(
                R.string.dialog_message_write_external_storage_permission_denied
            ),
            resources.getString(R.string.dialog_message_permission_never_ask_again_write),
            Constants.WRITE_EXTERNAL_STORAGE_STATUS
        )
    }

    /**
     * This method handles the response after user grants or denies the permission.
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
            Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    saveAndUploadSignature()
                } else {
                    Toaster.show(
                        binding.root, resources
                            .getString(R.string.permission_denied_to_write_external_document)
                    )
                }
            }
        }
    }

    private val documentFromGallery: Unit
        get() {
            val intentDocument: Intent =
                if (AndroidVersionUtil.isApiVersionGreaterOrEqual(Build.VERSION_CODES.KITKAT)) {
                    Intent(Intent.ACTION_OPEN_DOCUMENT)
                } else {
                    Intent(Intent.ACTION_GET_CONTENT)
                }
            intentDocument.addCategory(Intent.CATEGORY_OPENABLE)
            intentDocument.type = "image/*"
            startActivityForResult(intentDocument, FILE_SELECT_CODE)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                val filePath = FileUtils.getPathReal(requireContext(), uri!!)
                if (filePath != null) {
                    signatureFile = File(filePath)
                }
                if (signatureFile != null) {
                    uploadSignImage()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadSignImage() {
        showProgressbar(true)
        viewModel.createDocument(
            Constants.ENTITY_TYPE_CLIENTS,
            mClientId!!, signatureFile?.name, "Signature", signatureFile
        )
    }

    override fun onSignSavedError(errorMsg: String) {
        Toaster.show(binding.root, errorMsg)
    }

    override fun onSignSavedSuccess(absoluteFilePath: String) {
        val signImageUri = Uri.parse("file://$absoluteFilePath")
        signatureFile = File(signImageUri.path)
        Toaster.show(
            binding.root, getString(R.string.sign_saved_success_msg)
                    + signImageUri.path
        )
        uploadSignImage()
    }

    private fun showSignatureUploadedSuccessfully(response: GenericResponse?) {
        showProgressbar(false)
        Toaster.show(binding.root, R.string.sign_uploaded_success_msg)
    }

    private fun showError(errorId: Int) {
        showProgressbar(false)
        Toaster.show(binding.root, getStringMessage(errorId))
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility?.safelyBlockUI()
        } else {
            safeUIBlockingUtility?.safelyUnBlockUI()
        }
    }

    private fun saveAndUploadSignature() {
        if (binding.signView.xCoordinateSize > 0 && binding.signView.yCoordinateSize > 0) {
            binding.signView.saveSignature(mClientId!!)
        } else {
            Toaster.show(binding.root, R.string.empty_signature)
        }
    }

    companion object {
        val LOG_TAG = SignatureFragment::class.java.simpleName
        private const val FILE_SELECT_CODE = 0
    }
}