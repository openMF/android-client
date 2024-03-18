/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.documentlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.noncore.Document
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.DocumentListAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentDocumentListBinding
import com.mifos.mifosxdroid.dialogfragments.documentdialog.DocumentDialogFragment
import com.mifos.utils.CheckSelfPermissionAndRequest
import com.mifos.utils.FileUtils
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import java.io.File

@AndroidEntryPoint
class DocumentListFragment : MifosBaseFragment(), OnRefreshListener {

    private lateinit var binding: FragmentDocumentListBinding
    private val arg: DocumentListFragmentArgs by navArgs()

    private lateinit var viewModel: DocumentListViewModel

    private val mDocumentListAdapter by lazy {
        DocumentListAdapter(
            onDocumentClick = { documentSelected ->
                document = documentSelected
                showDocumentActions(documentSelected.id)
            }
        )
    }
    private var entityType: String? = null
    private var entityId = 0
    private var document: Document? = null
    private var documentBody: ResponseBody? = null
    private var mDocumentList: List<Document>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDocumentList = ArrayList()
        entityId = arg.entiyId
        entityType = arg.entityType
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDocumentListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DocumentListViewModel::class.java]
        setToolbarTitle(getString(R.string.documents))
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvDocuments.layoutManager = layoutManager
        binding.rvDocuments.setHasFixedSize(true)
        binding.rvDocuments.adapter = mDocumentListAdapter
        binding.swipeContainer.setColorSchemeColors(
            *activity
                ?.resources!!.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener(this)
        viewModel.loadDocumentList(entityType, entityId)
        viewModel.documentListUiState.observe(viewLifecycleOwner) {
            when (it) {
                is DocumentListUiState.ShowDocumentFetchSuccessfully -> {
                    showProgressbar(false)
                    showDocumentFetchSuccessfully(it.responseBody)
                }

                is DocumentListUiState.ShowDocumentList -> {
                    showProgressbar(false)
                    showDocumentList(it.documents)
                }

                is DocumentListUiState.ShowDocumentRemovedSuccessfully -> {
                    showProgressbar(false)
                    showDocumentRemovedSuccessfully()
                }

                is DocumentListUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is DocumentListUiState.ShowProgressbar -> showProgressbar(true)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noDocumentIcon.setOnClickListener {
            reloadOnError()
        }
    }

    override fun onRefresh() {
        viewModel.loadDocumentList(entityType, entityId)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDocumentList(entityType, entityId)
    }

    private fun reloadOnError() {
        binding.llError.visibility = View.GONE
        viewModel.loadDocumentList(entityType, entityId)
    }

    /**
     * This Method Checking the Permission WRITE_EXTERNAL_STORAGE is granted or not.
     * If not then prompt user a dialog to grant the WRITE_EXTERNAL_STORAGE permission.
     * and If Permission is granted already then Save the documentBody in external storage;
     */
    private fun checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            checkExternalStorageAndCreateDocument()
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
            Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the
                    checkExternalStorageAndCreateDocument()
                } else {

                    // permission denied, boo! Disable the
                    Toaster.show(
                        binding.root, resources
                            .getString(R.string.permission_denied_to_write_external_document)
                    )
                }
            }
        }
    }

    private fun showDocumentList(documents: List<Document>) {
        mDocumentList = documents
        mDocumentListAdapter.documents = documents
        if (documents.isEmpty()) {
            showEmptyDocuments()
        } else {
            if (binding.llError.visibility == View.VISIBLE) {
                binding.llError.visibility = View.GONE
            }
        }
    }

    private fun showDocumentFetchSuccessfully(responseBody: ResponseBody?) {
        documentBody = responseBody
        checkPermissionAndRequest()
    }

    private fun showDocumentActions(documentId: Int) {
        MaterialDialog.Builder().init(activity)
            .setItems(R.array.document_options) { dialog, which ->
                when (which) {
                    0 -> viewModel.downloadDocument(
                        entityType, entityId,
                        documentId
                    )

                    1 -> showDocumentDialog(getString(R.string.update_document))
                    2 -> viewModel.removeDocument(
                        entityType, entityId,
                        documentId
                    )

                    else -> {
                    }
                }
            }
            .createMaterialDialog()
            .show()
    }

    private fun checkExternalStorageAndCreateDocument() {
        // Create a path where we will place our documents in the user's
        // public directory and check if the file exists.
        val mifosDirectory = File(
            Environment.getExternalStorageDirectory(),
            resources.getString(R.string.document_directory)
        )
        if (!mifosDirectory.exists()) {
            mifosDirectory.mkdirs()
        }
        val documentFile = File(mifosDirectory.path, document!!.fileName)
        FileUtils.writeInputStreamDataToFile(documentBody!!.byteStream(), documentFile)

        //Opening the Saved Document
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            Uri.fromFile(documentFile),
            FileUtils.getMimeType(
                mifosDirectory.path +
                        resources.getString(R.string.slash) + document!!.fileName
            )
        )
        startActivity(intent)
    }

    private fun showDocumentRemovedSuccessfully() {
        Toaster.show(binding.root, resources.getString(R.string.document_remove_successfully))
        viewModel.loadDocumentList(entityType, entityId)
    }

    private fun showDocumentDialog(documentAction: String?) {
        val documentDialogFragment =
            DocumentDialogFragment.newInstance(entityType, entityId, documentAction, document)
        val fragmentTransaction = requireActivity().supportFragmentManager
            .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST)
        documentDialogFragment.show(fragmentTransaction, "Document Dialog Fragment")
    }

    private fun showEmptyDocuments() {
        binding.llError.visibility = View.VISIBLE
        binding.noDocumentText.text = resources.getString(R.string.no_document_to_show)
        binding.noDocumentIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
    }

    private fun showFetchingError(message: Int) {
        if (mDocumentListAdapter.itemCount == 0) {
            binding.llError.visibility = View.VISIBLE
            val errorMessage = getStringMessage(message) + getStringMessage(R.string.new_line) +
                    getStringMessage(R.string.click_to_refresh)
            binding.noDocumentText.text = errorMessage
        } else {
            Toaster.show(binding.root, getStringMessage(message))
        }
    }

    private fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = show
        if (show && mDocumentListAdapter.itemCount == 0) {
            showMifosProgressBar()
            binding.swipeContainer.isRefreshing = false
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val menuItemAddNewDocument =
            menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_DOCUMENT, Menu.NONE, getString(R.string.add_new))
        menuItemAddNewDocument.icon = ContextCompat
            .getDrawable(requireActivity(), R.drawable.ic_add_white_24dp)
        menuItemAddNewDocument.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == MENU_ITEM_ADD_NEW_DOCUMENT) {
            showDocumentDialog(getString(R.string.upload_document))
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val MENU_ITEM_ADD_NEW_DOCUMENT = 1000
        val LOG_TAG = DocumentListFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(entityType: String?, entiyId: Int): DocumentListFragment {
            val fragment = DocumentListFragment()
            val args = Bundle()
            args.putString(Constants.ENTITY_TYPE, entityType)
            args.putInt(Constants.ENTITY_ID, entiyId)
            fragment.arguments = args
            return fragment
        }
    }
}