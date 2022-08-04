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
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.DocumentListAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.dialogfragments.documentdialog.DocumentDialogFragment
import com.mifos.objects.noncore.Document
import com.mifos.utils.CheckSelfPermissionAndRequest
import com.mifos.utils.Constants
import com.mifos.utils.FileUtils
import com.mifos.utils.FragmentConstants
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

class DocumentListFragment : MifosBaseFragment(), DocumentListMvpView, OnRefreshListener {
    @JvmField
    @BindView(R.id.rv_documents)
    var rv_documents: RecyclerView? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.noDocumentText)
    var mNoChargesText: TextView? = null

    @JvmField
    @BindView(R.id.noDocumentIcon)
    var mNoChargesIcon: ImageView? = null

    @JvmField
    @BindView(R.id.ll_error)
    var ll_error: LinearLayout? = null

    @JvmField
    @Inject
    var mDocumentListPresenter: DocumentListPresenter? = null

    private val mDocumentListAdapter by lazy {
        DocumentListAdapter(
            onDocumentClick = { documentSelected ->
                document = documentSelected
                showDocumentActions(documentSelected.id)
            }
        )
    }
    private lateinit var rootView: View
    private var entityType: String? = null
    private var entityId = 0
    private var document: Document? = null
    private var documentBody: ResponseBody? = null
    private var mDocumentList: List<Document>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        mDocumentList = ArrayList()
        if (arguments != null) {
            entityType = requireArguments().getString(Constants.ENTITY_TYPE)
            entityId = requireArguments().getInt(Constants.ENTITY_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_document_list, container, false)
        ButterKnife.bind(this, rootView)
        mDocumentListPresenter!!.attachView(this)
        setToolbarTitle(getString(R.string.documents))
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_documents!!.layoutManager = layoutManager
        rv_documents!!.setHasFixedSize(true)
        rv_documents!!.adapter = mDocumentListAdapter
        swipeRefreshLayout!!.setColorSchemeColors(*activity
                ?.getResources()!!.getIntArray(R.array.swipeRefreshColors))
        swipeRefreshLayout!!.setOnRefreshListener(this)
        mDocumentListPresenter!!.loadDocumentList(entityType, entityId)
        return rootView
    }

    override fun onRefresh() {
        mDocumentListPresenter!!.loadDocumentList(entityType, entityId)
    }

    override fun onResume() {
        super.onResume()
        mDocumentListPresenter!!.loadDocumentList(entityType, entityId)
    }

    @OnClick(R.id.noDocumentIcon)
    fun reloadOnError() {
        ll_error!!.visibility = View.GONE
        mDocumentListPresenter!!.loadDocumentList(entityType, entityId)
    }

    /**
     * This Method Checking the Permission WRITE_EXTERNAL_STORAGE is granted or not.
     * If not then prompt user a dialog to grant the WRITE_EXTERNAL_STORAGE permission.
     * and If Permission is granted already then Save the documentBody in external storage;
     */
    override fun checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            checkExternalStorageAndCreateDocument()
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
     * This Method getting the Response after User Grant or denied the Permission
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

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    checkExternalStorageAndCreateDocument()
                } else {

                    // permission denied, boo! Disable the
                    Toaster.show(rootView, resources
                            .getString(R.string.permission_denied_to_write_external_document))
                }
            }
        }
    }

    override fun showDocumentList(documents: List<Document>) {
        mDocumentList = documents
        mDocumentListAdapter!!.documents = documents
        if (documents.isEmpty()) {
            showEmptyDocuments()
        } else {
            if (ll_error!!.visibility == View.VISIBLE) {
                ll_error!!.visibility = View.GONE
            }
        }
    }

    override fun showDocumentFetchSuccessfully(responseBody: ResponseBody?) {
        documentBody = responseBody
        checkPermissionAndRequest()
    }

    override fun showDocumentActions(documentId: Int) {
        MaterialDialog.Builder().init(activity)
                .setItems(R.array.document_options) { dialog, which ->
                    when (which) {
                        0 -> mDocumentListPresenter!!.downloadDocument(entityType, entityId,
                                documentId)
                        1 -> showDocumentDialog(getString(R.string.update_document))
                        2 -> mDocumentListPresenter!!.removeDocument(entityType, entityId,
                                documentId)
                        else -> {
                        }
                    }
                }
                .createMaterialDialog()
                .show()
    }

    override fun checkExternalStorageAndCreateDocument() {
        // Create a path where we will place our documents in the user's
        // public directory and check if the file exists.
        val mifosDirectory = File(Environment.getExternalStorageDirectory(),
                resources.getString(R.string.document_directory))
        if (!mifosDirectory.exists()) {
            mifosDirectory.mkdirs()
        }
        val documentFile = File(mifosDirectory.path, document!!.fileName)
        FileUtils.writeInputStreamDataToFile(documentBody!!.byteStream(), documentFile)

        //Opening the Saved Document
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(documentFile),
                FileUtils.getMimeType(mifosDirectory.path +
                        resources.getString(R.string.slash) + document!!.fileName))
        startActivity(intent)
    }

    override fun showDocumentRemovedSuccessfully() {
        Toaster.show(rootView, resources.getString(R.string.document_remove_successfully))
        mDocumentListPresenter!!.loadDocumentList(entityType, entityId)
    }

    override fun showDocumentDialog(documentAction: String?) {
        val documentDialogFragment = DocumentDialogFragment.newInstance(entityType, entityId, documentAction, document)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST)
        documentDialogFragment.show(fragmentTransaction, "Document Dialog Fragment")
    }

    override fun showEmptyDocuments() {
        ll_error!!.visibility = View.VISIBLE
        mNoChargesText!!.text = resources.getString(R.string.no_document_to_show)
        mNoChargesIcon!!.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
    }

    override fun showFetchingError(message: Int) {
        if (mDocumentListAdapter!!.itemCount == 0) {
            ll_error!!.visibility = View.VISIBLE
            val errorMessage = getStringMessage(message) + getStringMessage(R.string.new_line) +
                    getStringMessage(R.string.click_to_refresh)
            mNoChargesText!!.text = errorMessage
        } else {
            Toaster.show(rootView, getStringMessage(message))
        }
    }

    override fun showProgressbar(show: Boolean) {
        swipeRefreshLayout!!.isRefreshing = show
        if (show && mDocumentListAdapter!!.itemCount == 0) {
            showMifosProgressBar()
            swipeRefreshLayout!!.isRefreshing = false
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDocumentListPresenter!!.detachView()
        hideMifosProgressBar()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val menuItemAddNewDocument = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_DOCUMENT, Menu.NONE, getString(R.string.add_new))
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