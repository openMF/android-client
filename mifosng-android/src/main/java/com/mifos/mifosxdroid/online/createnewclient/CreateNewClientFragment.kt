/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.createnewclient

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import com.mifos.exceptions.InvalidTextInputException
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.mifosxdroid.views.CircularImageView
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.objects.templates.clients.ClientsTemplate
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import com.mifos.utils.ValidationUtil
import java.io.File
import java.util.*
import javax.inject.Inject

class CreateNewClientFragment : ProgressableFragment(), OnDatePickListener, CreateNewClientMvpView, OnItemSelectedListener {
    private val LOG_TAG = javaClass.simpleName
    @JvmField
    var datePickerSubmissionDate: DialogFragment? = null
    @JvmField
    var datePickerDateOfBirth: DialogFragment? = null

    @JvmField
    @BindView(R.id.iv_clientImage)
    var ivClientImage: CircularImageView? = null

    @JvmField
    @BindView(R.id.et_client_first_name)
    var etClientFirstName: EditText? = null

    @JvmField
    @BindView(R.id.et_client_last_name)
    var etClientLastName: EditText? = null

    @JvmField
    @BindView(R.id.et_client_middle_name)
    var etClientMiddleName: EditText? = null

    @JvmField
    @BindView(R.id.et_client_mobile_no)
    var etClientMobileNo: EditText? = null

    @JvmField
    @BindView(R.id.et_client_external_id)
    var etClientExternalId: EditText? = null

    @JvmField
    @BindView(R.id.cb_client_active_status)
    var cbClientActiveStatus: CheckBox? = null

    @JvmField
    @BindView(R.id.tv_submission_date)
    var tvSubmissionDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_dateofbirth)
    var tvDateOfBirth: TextView? = null

    @JvmField
    @BindView(R.id.sp_offices)
    var spOffices: Spinner? = null

    @JvmField
    @BindView(R.id.sp_gender)
    var spGender: Spinner? = null

    @JvmField
    @BindView(R.id.sp_client_type)
    var spClientType: Spinner? = null

    @JvmField
    @BindView(R.id.sp_staff)
    var spStaff: Spinner? = null

    @JvmField
    @BindView(R.id.sp_client_classification)
    var spClientClassification: Spinner? = null

    @JvmField
    @BindView(R.id.layout_submission)
    var layout_submission: LinearLayout? = null

    @JvmField
    @Inject
    var createNewClientPresenter: CreateNewClientPresenter? = null
    lateinit var rootView: View

    // It checks whether the user wants to create the new client with or without picture
    private var createClientWithImage = false
    private var hasDataTables = false
    private var returnedClientId: Int? = null
    private var officeId = 0
    private var clientTypeId = 0
    private var staffId = 0
    private var genderId = 0
    private var clientClassificationId = 0
    private var result = true
    private var submissionDateString: String? = null
    private var dateOfBirthString: String? = null
    private var clientsTemplate: ClientsTemplate? = null
    private var clientOffices: List<Office>? = null
    private var clientStaff: List<Staff>? = null
    private var mCurrentDateView // the view whose click opened the date picker
            : View? = null
    private var ClientImageFile: File? = null
    private var pickedImageUri: Uri? = null
    private var genderOptionsList: MutableList<String>? = null
    private var clientClassificationList: MutableList<String>? = null
    private var clientTypeList: MutableList<String>? = null
    private var officeList: MutableList<String>? = null
    private var staffList: MutableList<String>? = null
    private var genderOptionsAdapter: ArrayAdapter<String>? = null
    private var clientClassificationAdapter: ArrayAdapter<String>? = null
    private var clientTypeAdapter: ArrayAdapter<String>? = null
    private var officeAdapter: ArrayAdapter<String>? = null
    private var staffAdapter: ArrayAdapter<String>? = null
    private var progress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genderOptionsList = ArrayList()
        clientClassificationList = ArrayList()
        clientTypeList = ArrayList()
        officeList = ArrayList()
        staffList = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_create_new_client, null)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        ButterKnife.bind(this, rootView)
        createNewClientPresenter!!.attachView(this)
        showUserInterface()
        createNewClientPresenter!!.loadClientTemplate()
        return rootView
    }

    override fun showUserInterface() {
        genderOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, genderOptionsList ?: emptyList())
        genderOptionsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGender!!.adapter = genderOptionsAdapter
        spGender!!.onItemSelectedListener = this
        clientClassificationAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, clientClassificationList ?: emptyList())
        clientClassificationAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spClientClassification!!.adapter = clientClassificationAdapter
        spClientClassification!!.onItemSelectedListener = this
        clientTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, clientTypeList ?: emptyList())
        clientTypeAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spClientType!!.adapter = clientTypeAdapter
        spClientType!!.onItemSelectedListener = this
        officeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, officeList ?: emptyList())
        officeAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spOffices!!.adapter = officeAdapter
        spOffices!!.onItemSelectedListener = this
        staffAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, staffList ?: emptyList())
        staffAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spStaff!!.adapter = staffAdapter
        spStaff!!.onItemSelectedListener = this
        datePickerSubmissionDate = MFDatePicker.newInsance(this)
        datePickerDateOfBirth = MFDatePicker.newInsance(this)
        tvSubmissionDate!!.text = MFDatePicker.getDatePickedAsString()
        tvDateOfBirth!!.text = MFDatePicker.getDatePickedAsString()
        ivClientImage!!.setOnClickListener { view ->
            val menu = PopupMenu(requireActivity(), view)
            menu.menuInflater.inflate(R.menu.menu_create_client_image, menu
                    .menu)
            menu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.client_image_capture -> captureClientImage()
                    R.id.client_image_upload -> uploadClientImageFromDevice()
                    R.id.client_image_remove -> removeExistingImage()
                    else -> Log.e("CreateNewClientFragment", "Unrecognized " +
                            "client " +
                            "image menu item")
                }
                true
            }
            menu.show()
        }
    }

    private fun captureClientImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        ClientImageFile = File(
                requireActivity().externalCacheDir, "client_image.png")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ClientImageFile))
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    private fun uploadClientImageFromDevice() {
        if (isStoragePermissionGranted) {
            val galleryIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    private fun removeExistingImage() {
        ivClientImage!!.setImageDrawable(resources.getDrawable(R.drawable.ic_dp_placeholder))
        createClientWithImage = false
    }

    @OnClick(R.id.tv_submission_date)
    fun onClickTextViewSubmissionDate() {
        datePickerSubmissionDate!!.show(requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER)
        mCurrentDateView = tvSubmissionDate
    }

    @OnClick(R.id.tv_dateofbirth)
    fun onClickTextViewDateOfBirth() {
        datePickerDateOfBirth!!.show(requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER)
        mCurrentDateView = tvDateOfBirth
    }

    @OnClick(R.id.btn_submit)
    fun onClickSubmitButton() {
        submissionDateString = tvSubmissionDate!!.text.toString()
        submissionDateString = DateHelper
                .getDateAsStringUsedForCollectionSheetPayload(submissionDateString)
                .replace("-", " ")
        dateOfBirthString = tvDateOfBirth!!.text.toString()
        dateOfBirthString = DateHelper.getDateAsStringUsedForDateofBirth(dateOfBirthString)
                .replace("-", " ")
        val clientPayload = ClientPayload()

        //Mandatory Fields
        clientPayload.firstname = etClientFirstName!!.editableText.toString()
        clientPayload.lastname = etClientLastName!!.editableText.toString()
        clientPayload.officeId = officeId

        //Optional Fields, we do not need to add any check because these fields carry some
        // default values
        clientPayload.isActive = cbClientActiveStatus!!.isChecked
        clientPayload.activationDate = submissionDateString
        clientPayload.dateOfBirth = dateOfBirthString

        //Optional Fields
        if (!TextUtils.isEmpty(etClientMiddleName!!.editableText.toString())) {
            clientPayload.middlename = etClientMiddleName!!.editableText.toString()
        }
        if (PhoneNumberUtils.isGlobalPhoneNumber(etClientMobileNo!!.editableText.toString())) {
            clientPayload.mobileNo = etClientMobileNo!!.editableText.toString()
        }
        if (!TextUtils.isEmpty(etClientExternalId!!.editableText.toString())) {
            clientPayload.externalId = etClientExternalId!!.editableText.toString()
        }
        if (!clientStaff!!.isEmpty()) {
            clientPayload.staffId = staffId
        }
        if (!genderOptionsList!!.isEmpty()) {
            clientPayload.genderId = genderId
        }
        if (!clientTypeList!!.isEmpty()) {
            clientPayload.clientTypeId = clientTypeId
        }
        if (!clientClassificationList!!.isEmpty()) {
            clientPayload.clientClassificationId = clientClassificationId
        }
        if (!isFirstNameValid) {
            return
        }
        if (!isMiddleNameValid) {
            return
        }
        if (isLastNameValid) {
            if (hasDataTables) {
                val fragment = DataTableListFragment.newInstance(
                        clientsTemplate!!.dataTables,
                        clientPayload, Constants.CREATE_CLIENT)
                val fragmentTransaction = requireActivity().supportFragmentManager
                        .beginTransaction()
                requireActivity().supportFragmentManager.popBackStackImmediate()
                fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST)
                fragmentTransaction.replace(R.id.container, fragment).commit()
            } else {
                clientPayload.datatables = null
                createNewClientPresenter!!.createClient(clientPayload)
            }
        }
    }

    @OnCheckedChanged(R.id.cb_client_active_status)
    fun onClickActiveCheckBox() {
        layout_submission!!.visibility = if (cbClientActiveStatus!!.isChecked) View.VISIBLE else View.GONE
    }

    override fun onDatePicked(date: String) {
        if (mCurrentDateView != null && mCurrentDateView === tvSubmissionDate) {
            tvSubmissionDate!!.text = date
        } else if (mCurrentDateView != null && mCurrentDateView === tvDateOfBirth) {
            tvDateOfBirth!!.text = date
        }
    }

    override fun showClientTemplate(clientsTemplate: ClientsTemplate?) {
        this.clientsTemplate = clientsTemplate
        if (clientsTemplate != null) {
            if (!clientsTemplate.dataTables.isEmpty()) {
                hasDataTables = true
            }
        }
        genderOptionsList!!.addAll(
                createNewClientPresenter!!.filterOptions(clientsTemplate?.genderOptions))
        genderOptionsAdapter!!.notifyDataSetChanged()
        clientTypeList!!.addAll(
                createNewClientPresenter!!.filterOptions(clientsTemplate?.clientTypeOptions))
        clientTypeAdapter!!.notifyDataSetChanged()
        clientClassificationList!!.addAll(createNewClientPresenter!!
                .filterOptions(clientsTemplate?.clientClassificationOptions))
        clientClassificationAdapter!!.notifyDataSetChanged()
    }

    override fun showOffices(offices: List<Office?>?) {
        clientOffices = offices as List<Office>?
        officeList!!.addAll(createNewClientPresenter!!.filterOffices(offices))
        Collections.sort(officeList)
        officeAdapter!!.notifyDataSetChanged()
    }

    override fun showStaffInOffices(staffs: List<Staff?>?) {
        if (staffs != null) {
            if (staffs.isEmpty()) {
                showMessage(R.string.no_staff_associated_with_office)
            }
        }
        clientStaff = staffs as List<Staff>?
        staffList!!.clear()
        staffList!!.addAll(createNewClientPresenter!!.filterStaff(staffs))
        staffAdapter!!.notifyDataSetChanged()
    }

    override fun showClientCreatedSuccessfully(message: Int) {
        Toaster.show(rootView, message)
    }

    override fun showWaitingForCheckerApproval(message: Int) {
        Toaster.show(rootView, message)
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showMessage(message: Int) {
        Toaster.show(rootView, message)
    }

    override fun showMessage(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(show: Boolean) {
        showProgress(show)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createNewClientPresenter!!.detachView()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.sp_offices -> {
                officeId = clientOffices!![position].id
                createNewClientPresenter!!.loadStaffInOffices(officeId)
            }
            R.id.sp_gender -> genderId = clientsTemplate!!.genderOptions[position].id
            R.id.sp_client_type -> clientTypeId = clientsTemplate!!.clientTypeOptions[position].id
            R.id.sp_staff -> staffId = clientStaff!![position].id
            R.id.sp_client_classification -> clientClassificationId = clientsTemplate!!.clientClassificationOptions[position].id
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    val isFirstNameValid: Boolean
        get() {
            result = true
            try {
                if (TextUtils.isEmpty(etClientFirstName!!.editableText.toString())) {
                    throw RequiredFieldException(resources.getString(R.string.first_name),
                            resources.getString(R.string.error_cannot_be_empty))
                }
                if (!ValidationUtil.isNameValid(etClientFirstName!!.editableText.toString())) {
                    throw InvalidTextInputException(resources.getString(R.string.first_name),
                            resources.getString(R.string.error_should_contain_only),
                            InvalidTextInputException.TYPE_ALPHABETS)
                }
            } catch (e: InvalidTextInputException) {
                e.notifyUserWithToast(activity)
                result = false
            } catch (e: RequiredFieldException) {
                e.notifyUserWithToast(activity)
                result = false
            }
            return result
        }

    val isMiddleNameValid: Boolean
        get() {
            result = true
            try {
                if (!TextUtils.isEmpty(etClientMiddleName!!.editableText.toString())
                        && !ValidationUtil.isNameValid(etClientMiddleName!!.editableText
                                .toString())) {
                    throw InvalidTextInputException(
                            resources.getString(R.string.middle_name),
                            resources.getString(R.string.error_should_contain_only),
                            InvalidTextInputException.TYPE_ALPHABETS)
                }
            } catch (e: InvalidTextInputException) {
                e.notifyUserWithToast(activity)
                result = false
            }
            return result
        }

    val isLastNameValid: Boolean
        get() {
            result = true
            try {
                if (TextUtils.isEmpty(etClientLastName!!.editableText.toString())) {
                    throw RequiredFieldException(resources.getString(R.string.last_name),
                            resources.getString(R.string.error_cannot_be_empty))
                }
                if (!ValidationUtil.isNameValid(etClientLastName!!.editableText.toString())) {
                    throw InvalidTextInputException(resources.getString(R.string.last_name),
                            resources.getString(R.string.error_should_contain_only),
                            InvalidTextInputException.TYPE_ALPHABETS)
                }
            } catch (e: InvalidTextInputException) {
                e.notifyUserWithToast(activity)
                result = false
            } catch (e: RequiredFieldException) {
                e.notifyUserWithToast(activity)
                result = false
            }
            return result
        }

    override fun setClientId(id: Int?) {
        returnedClientId = id
        if (createClientWithImage) {
            ClientImageFile?.let { createNewClientPresenter!!.uploadImage(returnedClientId!!, it) }
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            createClientWithImage = true
            ivClientImage!!.setImageBitmap(BitmapFactory.decodeFile(ClientImageFile!!.absolutePath))
        } else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data?.data != null) {
            createClientWithImage = true
            pickedImageUri = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val c = requireActivity().contentResolver.query(pickedImageUri!!, filePath,
                    null, null, null)!!
            c.moveToFirst()
            val columnIndex = c.getColumnIndex(filePath[0])
            val picturePath = c.getString(columnIndex)
            ivClientImage!!.setImageBitmap(BitmapFactory.decodeFile(picturePath))
            ClientImageFile = File(picturePath)
        }
    }

    override fun showProgress(message: String?) {
        if (progress == null) {
            progress = ProgressDialog(activity, ProgressDialog.STYLE_SPINNER)
            progress!!.setCancelable(false)
        }
        progress!!.setMessage(message)
        progress!!.show()
    }

    override fun hideProgress() {
        if (progress != null && progress!!.isShowing) progress!!.dismiss()
    }

    //permission is automatically granted on sdk<23 upon installation
    val isStoragePermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            uploadClientImageFromDevice()
        }
    }

    companion object {
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1
        private const val PICK_IMAGE_ACTIVITY_REQUEST_CODE = 2
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 3
        @JvmStatic
        fun newInstance(): CreateNewClientFragment {
            val createNewClientFragment = CreateNewClientFragment()
            val args = Bundle()
            createNewClientFragment.arguments = args
            return createNewClientFragment
        }
    }
}