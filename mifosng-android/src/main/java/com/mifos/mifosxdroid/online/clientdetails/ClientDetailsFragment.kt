/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientdetails

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.joanzapata.iconify.fonts.MaterialIcons
import com.joanzapata.iconify.widget.IconTextView
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.LoanAccountsListAdapter
import com.mifos.mifosxdroid.adapters.SavingsAccountsListAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentClientDetailsBinding
import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Client
import com.mifos.utils.Constants
import com.mifos.utils.ImageLoaderUtils
import com.mifos.utils.Utils
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class ClientDetailsFragment : MifosBaseFragment(), ClientDetailsMvpView {

    private lateinit var binding: FragmentClientDetailsBinding

    private var imgDecodableString: String? = null
    var clientId = 0
    var chargesList: MutableList<Charges> = ArrayList()

    @Inject
    lateinit var mClientDetailsPresenter: ClientDetailsPresenter

    private val clientImageFile = File(
        Environment.getExternalStorageDirectory().toString() +
                "/client_image.png"
    )
    private var accountAccordion: AccountAccordion? = null
    private var isClientActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent?.inject(this)
        if (arguments != null) {
            clientId = requireArguments().getInt(Constants.CLIENT_ID)
        }
        setHasOptionsMenu(true)
        checkPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientDetailsBinding.inflate(inflater, container, false)
        mClientDetailsPresenter.attachView(this)
        inflateClientInformation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnActivateClient.setOnClickListener {
            onClickActivateClient()
        }
    }

    private fun onClickActivateClient() {
        activateClient()
    }

    private fun inflateClientInformation() {
        mClientDetailsPresenter.loadClientDetailsAndClientAccounts(clientId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            // When an Image is picked
            if (requestCode == UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data && data.data != null) {
                // Get the Image from data
                val selectedImage = data.data!!
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                // Get the cursor
                val cursor = requireActivity().applicationContext.contentResolver.query(
                    selectedImage,
                    filePathColumn, null, null, null
                )!!
                // Move to first row
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imgDecodableString = cursor.getString(columnIndex)
                cursor.close()
                val pickedImage = BitmapFactory.decodeFile(imgDecodableString)
                saveBitmap(clientImageFile, pickedImage)
                uploadImage(clientImageFile)
            } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK
            ) {
                uploadImage(clientImageFile)
            } else {
                Toaster.show(
                    binding.root, R.string.havent_picked_image,
                    Toast.LENGTH_LONG
                )
            }
        } catch (e: Exception) {
            Toaster.show(binding.root, e.toString(), Toast.LENGTH_LONG)
        }
    }

    private fun saveBitmap(file: File, mBitmap: Bitmap) {
        try {
            file.createNewFile()
            var fOut: FileOutputStream? = null
            fOut = FileOutputStream(file)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
        } catch (exception: Exception) {
            //Empty catch block to prevent crashing
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        if (isClientActive) {
            menu.add(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, getString(R.string.more_info))
            menu.add(Menu.NONE, MENU_ITEM_PIN_POINT, Menu.NONE, getString(R.string.pinpoint))
            menu.add(Menu.NONE, MENU_ITEM_CLIENT_CHARGES, Menu.NONE, getString(R.string.charges))
            menu.add(
                Menu.NONE,
                MENU_ITEM_ADD_SAVINGS_ACCOUNT,
                Menu.NONE,
                getString(R.string.savings_account)
            )
            menu.add(
                Menu.NONE, MENU_ITEM_ADD_LOAN_ACCOUNT, Menu.NONE,
                getString(R.string.add_loan)
            )
            menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE, getString(R.string.documents))
            menu.add(Menu.NONE, MENU_ITEM_UPLOAD_SIGN, Menu.NONE, R.string.upload_sign)
            menu.add(Menu.NONE, MENU_ITEM_IDENTIFIERS, Menu.NONE, getString(R.string.identifiers))
            menu.add(Menu.NONE, MENU_ITEM_SURVEYS, Menu.NONE, getString(R.string.survey))
            menu.add(Menu.NONE, MENU_ITEM_NOTE, Menu.NONE, getString(R.string.note))
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_ITEM_DATA_TABLES -> loadClientDataTables()
            MENU_ITEM_DOCUMENTS -> loadDocuments()
            MENU_ITEM_UPLOAD_SIGN -> loadSignUpload()
            MENU_ITEM_CLIENT_CHARGES -> loadClientCharges()
            MENU_ITEM_ADD_SAVINGS_ACCOUNT -> addSavingsAccount()
            MENU_ITEM_ADD_LOAN_ACCOUNT -> addLoanAccount()
            MENU_ITEM_IDENTIFIERS -> loadIdentifiers()
            MENU_ITEM_PIN_POINT -> {
                val action =
                    ClientDetailsFragmentDirections.actionClientDetailsFragmentToPinpointClientActivity(
                        clientId
                    )
                findNavController().navigate(action)
            }

            MENU_ITEM_SURVEYS -> loadSurveys()
            MENU_ITEM_NOTE -> loadNotes()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun captureClientImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(clientImageFile))
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                CHECK_PERMISSIONS
            )
        }
    }

    private fun uploadClientImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Start the Intent
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(clientImageFile))
        startActivityForResult(galleryIntent, UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    /**
     * A service to upload the image of the client.
     *
     * @param pngFile - PNG images supported at the moment
     */
    private fun uploadImage(pngFile: File) {
        mClientDetailsPresenter.uploadImage(clientId, pngFile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mClientDetailsPresenter.detachView()
    }

    private fun loadDocuments() {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToDocumentListFragment(
                clientId,
                Constants.ENTITY_TYPE_CLIENTS
            )
        findNavController().navigate(action)
    }

    private fun loadNotes() {
        val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToNoteFragment(
            clientId,
            Constants.ENTITY_TYPE_CLIENTS
        )
        findNavController().navigate(action)
    }

    private fun loadClientCharges() {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToClientChargeFragment(
                clientId,
                chargesList.toTypedArray()
            )
        findNavController().navigate(action)
    }

    private fun loadIdentifiers() {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToClientIdentifiersFragment(
                clientId
            )
        findNavController().navigate(action)
    }

    private fun loadSurveys() {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToSurveyListFragment(clientId)
        findNavController().navigate(action)
    }

    private fun addSavingsAccount() {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToSavingsAccountFragment(
                clientId,
                false
            )
        findNavController().navigate(action)
    }

    private fun addLoanAccount() {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToLoanAccountFragment(
                clientId
            )
        findNavController().navigate(action)
    }

    private fun activateClient() {
        val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToActivateFragment(
            clientId,
            Constants.ACTIVATE_CLIENT
        )
        findNavController().navigate(action)
    }

    private fun loadClientDataTables() {
        val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToDataTableFragment(
            Constants.DATA_TABLE_NAME_CLIENT,
            clientId
        )
        findNavController().navigate(action)
    }

    private fun loadSignUpload() {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToSignatureFragment(clientId)
        findNavController().navigate(action)
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            binding.rlClient.visibility = GONE
            showMifosProgressBar()
        } else {
            binding.rlClient.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }

    override fun showClientInformation(client: Client?) {
        if (client != null) {
            setToolbarTitle(getString(R.string.client) + " - " + client.displayName)
            isClientActive = client.active
            requireActivity().invalidateOptionsMenu()
            if (!client.active) {
                binding.llBottomPanel.visibility = View.VISIBLE
            }
            binding.tvFullName.text = client.displayName
            binding.tvAccountNumber.text = client.accountNo
            binding.tvGroup.text = client.groupNames
            binding.tvExternalId.text = client.externalId
            binding.tvMobileNo.text = client.mobileNo
            if (TextUtils.isEmpty(client.accountNo)) binding.rowAccount.visibility = GONE
            if (TextUtils.isEmpty(client.externalId)) binding.rowExternal.visibility = GONE
            if (TextUtils.isEmpty(client.mobileNo)) binding.tableRowMobileNo.visibility =
                GONE
            if (TextUtils.isEmpty(client.groupNames)) binding.rowGroup.visibility = GONE
            try {
                val dateString = Utils.getStringOfDate(
                    client.activationDate as List<Int>
                )
                binding.tvActivationDate.text = dateString
                if (TextUtils.isEmpty(dateString)) binding.rowActivation.visibility = GONE
            } catch (e: IndexOutOfBoundsException) {
                Toast.makeText(
                    activity, getString(R.string.error_client_inactive),
                    Toast.LENGTH_SHORT
                ).show()
                binding.tvActivationDate.text = ""
            }
            binding.tvOffice.text = client.officeName
            if (TextUtils.isEmpty(client.officeName)) binding.rowOffice.visibility = GONE
            if (client.imagePresent) {
                loadClientProfileImage()
            } else {
                binding.ivClientImage.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher, null)
                )
                binding.pbImageProgressBar.visibility = GONE
            }
            binding.ivClientImage.setOnClickListener { view ->
                val menu = PopupMenu(requireActivity(), view)
                menu.menuInflater.inflate(
                    R.menu.client_image_popup, menu
                        .menu
                )
                if (!client.imagePresent) {
                    menu.menu.findItem(R.id.client_image_remove).isVisible = false
                }
                menu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.client_image_upload -> uploadClientImage()
                        R.id.client_image_capture -> captureClientImage()
                        R.id.client_image_remove -> mClientDetailsPresenter.deleteClientImage(
                            clientId
                        )

                        else -> Log.e(
                            "ClientDetailsFragment", "Unrecognized " +
                                    "client " +
                                    "image menu item"
                        )
                    }
                    true
                }
                menu.show()
            }
        }
    }

    override fun showUploadImageSuccessfully(response: ResponseBody?, imagePath: String?) {
        Toaster.show(binding.root, R.string.client_image_updated)
        binding.ivClientImage.setImageBitmap(BitmapFactory.decodeFile(imagePath))
    }

    override fun showUploadImageFailed(s: String?) {
        Toaster.show(binding.root, s)
        loadClientProfileImage()
    }

    override fun showUploadImageProgressbar(b: Boolean) {
        if (b) {
            binding.pbImageProgressBar.visibility = View.VISIBLE
        } else {
            binding.pbImageProgressBar.visibility = GONE
        }
    }

    private fun loadClientProfileImage() {
        binding.pbImageProgressBar.visibility = View.VISIBLE
        binding.ivClientImage.let { ImageLoaderUtils.loadImage(activity, clientId, it) }
        binding.pbImageProgressBar.visibility = GONE
    }

    override fun showClientImageDeletedSuccessfully() {
        Toaster.show(binding.root, "Image deleted")
        binding.ivClientImage.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_launcher
            )
        )
    }

    override fun showClientAccount(clientAccounts: ClientAccounts) {
        // Proceed only when the fragment is added to the activity.
        if (!isAdded) {
            return
        }
        accountAccordion = AccountAccordion(activity)
        if (clientAccounts.loanAccounts.isNotEmpty()) {
            val section = AccountAccordion.Section.LOANS
            val adapter = LoanAccountsListAdapter(
                requireActivity().applicationContext,
                clientAccounts.loanAccounts
            )
            section.connect(
                activity,
                adapter
            ) { adapterView, view, i, l ->
                adapter.getItem(i).id?.let { loadLoanAccountSummary(it) }
            }
        } else {
            binding.accountAccordionSectionLoans.root.visibility = GONE
        }
        if (clientAccounts.getNonRecurringSavingsAccounts().isNotEmpty()) {
            val section = AccountAccordion.Section.SAVINGS
            val adapter = SavingsAccountsListAdapter(
                requireActivity().applicationContext,
                clientAccounts.getNonRecurringSavingsAccounts()
            )
            section.connect(
                activity,
                adapter
            ) { adapterView, view, i, l ->
                adapter.getItem(i).id?.let {
                    adapter.getItem(i).depositType?.let { it1 ->
                        loadSavingsAccountSummary(
                            it,
                            it1
                        )
                    }
                }
            }
        } else {
            binding.accountAccordionSectionSavings.root.visibility = GONE
        }
        if (clientAccounts.getRecurringSavingsAccounts().isNotEmpty()) {
            val section = AccountAccordion.Section.RECURRING
            val adapter = SavingsAccountsListAdapter(
                requireActivity().applicationContext,
                clientAccounts.getRecurringSavingsAccounts()
            )
            section.connect(
                activity,
                adapter
            ) { adapterView, view, i, l ->
                adapter.getItem(i).id?.let {
                    adapter.getItem(i).depositType?.let { it1 ->
                        loadSavingsAccountSummary(
                            it,
                            it1
                        )
                    }
                }
            }
        } else {
            binding.accountAccordionSectionRecurring.root.visibility = GONE
        }
    }

    override fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    private class AccountAccordion(private val context: Activity?) {
        private var currentSection: Section? = null
        fun setCurrentSection(currentSection: Section?) {
            // close previous section
            if (this.currentSection != null) {
                this.currentSection?.close(context)
            }
            this.currentSection = currentSection

            // open new section
            if (this.currentSection != null) {
                this.currentSection?.open(context)
            }
        }

        enum class Section(private val sectionId: Int, private val textViewStringId: Int) {
            LOANS(
                R.id.account_accordion_section_loans,
                R.string.loanAccounts
            ),
            SAVINGS(
                R.id.account_accordion_section_savings,
                R.string.savingAccounts
            ),
            RECURRING(R.id.account_accordion_section_recurring, R.string.recurringAccount);

            private var mListViewCount = 0.0
            fun getTextView(context: Activity?): TextView {
                return getSectionView(context).findViewById<View>(R.id.tv_toggle_accounts) as TextView
            }

            fun getIconView(context: Activity?): IconTextView {
                return getSectionView(context).findViewById<View>(R.id.tv_toggle_accounts_icon) as IconTextView
            }

            fun getListView(context: Activity?): ListView {
                return getSectionView(context).findViewById<View>(R.id.lv_accounts) as ListView
            }

            fun getCountView(context: Activity?): TextView {
                return getSectionView(context).findViewById<View>(R.id.tv_count_accounts) as TextView
            }

            fun getSectionView(context: Activity?): View {
                return context!!.findViewById(sectionId)
            }

            fun connect(
                context: Activity?,
                adapter: ListAdapter,
                onItemClickListener: AdapterView.OnItemClickListener?
            ) {
                getCountView(context).text = adapter.count.toString()
                val listView = getListView(context)
                listView.adapter = adapter
                listView.onItemClickListener = onItemClickListener
            }

            fun open(context: Activity?) {
                val iconView = getIconView(context)
                iconView.text = "{" + LIST_CLOSED_ICON.key() + "}"
                mListViewCount = java.lang.Double.valueOf(
                    getCountView(context)
                        .text
                        .toString()
                )
                val listView = getListView(context)
                resizeListView(context, listView)
                listView.visibility = View.VISIBLE
            }

            fun close(context: Activity?) {
                val iconView = getIconView(context)
                iconView.text = "{" + LIST_OPEN_ICON.key() + "}"
                getListView(context).visibility = GONE
            }

            private fun configureSection(context: Activity?, accordion: AccountAccordion) {
                val listView = getListView(context)
                val textView = getTextView(context)
                val iconView = getIconView(context)
                val onClickListener = View.OnClickListener {
                    if (this@Section == accordion.currentSection) {
                        accordion.setCurrentSection(null)
                    } else if (listView.count > 0) {
                        accordion.setCurrentSection(this@Section)
                    }
                }
                textView.setOnClickListener(onClickListener)
                textView.text = context?.getString(textViewStringId)
                iconView.setOnClickListener(onClickListener)
                listView.setOnTouchListener { view, motionEvent ->
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    false
                }
                // initialize section in closed state
                close(context)
            }

            private fun resizeListView(context: Activity?, listView: ListView) {
                if (mListViewCount < 4) {
                    //default listview height is 200dp,which displays 4 listview items.
                    // This calculates the required listview height
                    // if listview items are less than 4
                    val heightInDp = mListViewCount / 4 * 200
                    val heightInPx = heightInDp * context!!.resources
                        .displayMetrics.density
                    val params = listView.layoutParams
                    params.height = heightInPx.toInt()
                    listView.layoutParams = params
                    listView.requestLayout()
                }
            }

            companion object {
                private val LIST_OPEN_ICON = MaterialIcons.md_add_circle_outline
                private val LIST_CLOSED_ICON = MaterialIcons.md_remove_circle_outline
                fun configure(context: Activity?, accordion: AccountAccordion) {
                    for (section in values()) {
                        section.configureSection(context, accordion)
                    }
                }
            }

        }

        init {
            Section.configure(context, this)
        }
    }

    private fun loadLoanAccountSummary(loanAccountNumber: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToLoanAccountSummaryFragment(
                loanAccountNumber,
                true
            )
        findNavController().navigate(action)
    }

    private fun loadSavingsAccountSummary(savingsAccountNumber: Int, accountType: DepositType) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToSavingsAccountSummaryFragment(
                savingsAccountNumber,
                accountType,
                true
            )
        findNavController().navigate(action)
    }

    companion object {
        // Intent response codes. Each response code must be a unique integer.
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2
        private const val UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1
        private const val CHECK_PERMISSIONS = 1010
        const val MENU_ITEM_DATA_TABLES = 1000
        const val MENU_ITEM_PIN_POINT = 1001
        const val MENU_ITEM_CLIENT_CHARGES = 1003
        const val MENU_ITEM_ADD_SAVINGS_ACCOUNT = 1004
        const val MENU_ITEM_ADD_LOAN_ACCOUNT = 1005
        const val MENU_ITEM_DOCUMENTS = 1006
        const val MENU_ITEM_UPLOAD_SIGN = 1010
        const val MENU_ITEM_IDENTIFIERS = 1007
        const val MENU_ITEM_SURVEYS = 1008
        const val MENU_ITEM_NOTE = 1009
    }
}