package com.mifos.mifosxdroid.online.sign

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentSignBinding
import com.mifos.mifosxdroid.views.SignatureView.OnSignatureSaveListener
import com.mifos.utils.*
import java.io.File
import javax.inject.Inject

/**
 * Created by Tarun on 28-06-2017.
 */
class SignatureFragment : MifosBaseFragment(), SignatureMvpView,
    BottomNavigationView.OnNavigationItemSelectedListener, OnSignatureSaveListener {

    private lateinit var binding: FragmentSignBinding

    @JvmField
    @Inject
    var mSignaturePresenter: SignaturePresenter? = null
    private var mClientId: Int? = null
    private var signatureFile: File? = null
    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        safeUIBlockingUtility = SafeUIBlockingUtility(activity,
                getString(R.string.signature_fragment_loading_message))
        if (arguments != null) {
            mClientId = requireArguments().getInt(Constants.CLIENT_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSignBinding.inflate(inflater,container,false)

        mSignaturePresenter!!.attachView(this)
        showInterface()
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
    override fun checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            saveAndUploadSignature()
        } else {
            requestPermission()
        }
    }

    override fun requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
                activity as MifosBaseActivity?,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                resources.getString(
                        R.string.dialog_message_write_external_storage_permission_denied),
                resources.getString(R.string.dialog_message_permission_never_ask_again_write),
                Constants.WRITE_EXTERNAL_STORAGE_STATUS)
    }

    /**
     * This method handles the response after user grants or denies the permission.
     *
     * @param requestCode  Request Code
     * @param permissions  Permission
     * @param grantResults GrantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveAndUploadSignature()
                } else {
                    Toaster.show(binding.root, resources
                            .getString(R.string.permission_denied_to_write_external_document))
                }
            }
        }
    }

    override val documentFromGallery: Unit
        get() {
            val intentDocument: Intent
            intentDocument = if (AndroidVersionUtil.isApiVersionGreaterOrEqual(Build.VERSION_CODES.KITKAT)) {
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
                val filePath = FileUtils.getPathReal(activity, uri)
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
        mSignaturePresenter!!.createDocument(Constants.ENTITY_TYPE_CLIENTS,
                mClientId!!, signatureFile!!.name, "Signature", signatureFile)
    }

    override fun onSignSavedError(errorMsg: String) {
        Toaster.show(binding.root, errorMsg)
    }

    override fun onSignSavedSuccess(absoluteFilePath: String) {
        val signImageUri = Uri.parse("file://$absoluteFilePath")
        signatureFile = File(signImageUri.path)
        Toaster.show(binding.root, getString(R.string.sign_saved_success_msg)
                + signImageUri.path)
        uploadSignImage()
    }

    override fun showSignatureUploadedSuccessfully(response: GenericResponse?) {
        showProgressbar(false)
        Toaster.show(binding.root, R.string.sign_uploaded_success_msg)
    }

    override fun showError(errorId: Int) {
        showProgressbar(false)
        Toaster.show(binding.root, getStringMessage(errorId))
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility!!.safelyBlockUI()
        } else {
            safeUIBlockingUtility!!.safelyUnBlockUI()
        }
    }

    override fun saveAndUploadSignature() {
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