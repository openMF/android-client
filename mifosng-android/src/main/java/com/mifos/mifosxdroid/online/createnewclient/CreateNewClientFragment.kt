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
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.mifos.exceptions.InvalidTextInputException
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentCreateNewClientBinding
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.objects.templates.clients.ClientsTemplate
import com.mifos.utils.Constants
import com.mifos.utils.DatePickerConstrainType
import com.mifos.utils.FragmentConstants
import com.mifos.utils.ValidationUtil
import com.mifos.utils.getDatePickerDialog
import com.mifos.utils.getTodayFormatted
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import javax.inject.Inject

class CreateNewClientFragment : ProgressableFragment(), CreateNewClientMvpView {

    private lateinit var binding: FragmentCreateNewClientBinding

    @Inject
    lateinit var createNewClientPresenter: CreateNewClientPresenter

    // It checks whether the user wants to create the new client with or without picture
    private var createClientWithImage = false
    private var hasDataTables = false
    private var returnedClientId: Int? = null
    private var officeId: Int? = 0
    private var clientTypeId: Int? = 0
    private var staffId: Int? = 0
    private var genderId: Int? = 0
    private var clientClassificationId: Int? = 0
    private var result = true
    private var submissionDateString: String? = null
    private var dateOfBirthString: String? = null
    private var clientsTemplate: ClientsTemplate? = null
    private var clientOffices: List<Office>? = null
    private var clientStaff: List<Staff>? = null
    private var ClientImageFile: File? = null
    private var pickedImageUri: Uri? = null
    private var genderOptionsList: MutableList<String> = ArrayList()
    private var clientClassificationList: MutableList<String> = ArrayList()
    private var clientTypeList: MutableList<String> = ArrayList()
    private var officeList: MutableList<String> = ArrayList()
    private var staffList: MutableList<String> = ArrayList()
    private var progress: ProgressDialog? = null

    private var submissionDate: Instant = Instant.now()
    private val submissionDatePickerDialog by lazy {
        getDatePickerDialog(dayOfBirthDate, DatePickerConstrainType.ONLY_FUTURE_DAYS) {
            val formattedDate = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(it)
            submissionDate = Instant.ofEpochMilli(it)
            binding.submissionDateFieldContainer.editText?.setText(formattedDate)
        }
    }

    private var dayOfBirthDate: Instant = Instant.now()
    private val datePickerDialog by lazy {
        getDatePickerDialog(dayOfBirthDate, DatePickerConstrainType.ONLY_PAST_DAYS) {
            val formattedDate = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(it)
            dayOfBirthDate = Instant.ofEpochMilli(it)
            binding.dateOfBirthFieldContainer.editText?.setText(formattedDate)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNewClientBinding.inflate(inflater, container, false)
        (activity as MifosBaseActivity).activityComponent?.inject(this)
        createNewClientPresenter.attachView(this)
        showUserInterface()
        createNewClientPresenter.loadClientTemplate()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            onClickSubmitButton()
        }

        binding.cbClientActiveStatus.setOnCheckedChangeListener { compoundButton, b ->
            onClickActiveCheckBox()
        }

        binding.genderListField.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val index = genderOptionsList.indexOf(adapterView.getItemAtPosition(relativePosition))
            genderId = clientsTemplate?.genderOptions?.get(index)?.id
        }

        binding.dateOfBirthFieldContainer.setEndIconOnClickListener {
            datePickerDialog.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }

        binding.clientTypeListField.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val index = clientTypeList.indexOf(adapterView.getItemAtPosition(relativePosition))
            clientTypeId = clientsTemplate?.clientTypeOptions?.get(index)?.id
        }

        binding.clientClassificationListField.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val index =
                clientClassificationList.indexOf(adapterView.getItemAtPosition(relativePosition))
            clientClassificationId = clientsTemplate?.clientClassificationOptions?.get(index)?.id
        }

        binding.officeListField.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val index = officeList.indexOf(adapterView.getItemAtPosition(relativePosition))
            officeId = clientOffices?.get(index)?.id
            officeId?.let { createNewClientPresenter.loadStaffInOffices(it) }
        }

        binding.staffListField.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val index = staffList.indexOf(adapterView.getItemAtPosition(relativePosition))
            staffId = clientStaff?.get(index)?.id
        }

        binding.submissionDateFieldContainer.setEndIconOnClickListener {
            submissionDatePickerDialog.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }

    }

    override fun showUserInterface() {

        binding.submissionDateFieldContainer.editText?.setText(getTodayFormatted())

        binding.dateOfBirthFieldContainer.editText?.setText(getTodayFormatted())

        binding.ivClientImage.setOnClickListener { view ->
            val menu = PopupMenu(requireActivity(), view)
            menu.menuInflater.inflate(
                R.menu.menu_create_client_image, menu
                    .menu
            )
            menu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.client_image_capture -> captureClientImage()
                    R.id.client_image_upload -> uploadClientImageFromDevice()
                    R.id.client_image_remove -> removeExistingImage()
                    else -> Log.e(
                        "CreateNewClientFragment", "Unrecognized " +
                                "client " +
                                "image menu item"
                    )
                }
                true
            }
            menu.show()
        }
    }

    private fun captureClientImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        ClientImageFile = File(
            requireActivity().externalCacheDir, "client_image.png"
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ClientImageFile))
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    private fun uploadClientImageFromDevice() {
        if (isStoragePermissionGranted) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, PICK_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    private fun removeExistingImage() {
        binding.ivClientImage.setImageDrawable(resources.getDrawable(R.drawable.ic_dp_placeholder))
        createClientWithImage = false
    }


    private fun onClickSubmitButton() {
        submissionDateString = binding.dateOfBirthFieldContainer.editText?.text.toString()

        dateOfBirthString = binding.dateOfBirthFieldContainer.editText?.text.toString()

        val clientPayload = ClientPayload()

        //Mandatory Fields
        clientPayload.firstname = binding.etClientFirstName.editableText.toString()
        clientPayload.lastname = binding.etClientLastName.editableText.toString()
        clientPayload.officeId = officeId

        //Optional Fields, we do not need to add any check because these fields carry some
        // default values
        clientPayload.active = binding.cbClientActiveStatus.isChecked
        clientPayload.activationDate = submissionDateString
        clientPayload.dateOfBirth = dateOfBirthString

        //Optional Fields
        if (!TextUtils.isEmpty(binding.etClientMiddleName.editableText.toString())) {
            clientPayload.middlename = binding.etClientMiddleName.editableText.toString()
        }
        if (PhoneNumberUtils.isGlobalPhoneNumber(binding.etClientMobileNo.editableText.toString())) {
            clientPayload.mobileNo = binding.etClientMobileNo.editableText.toString()
        }
        if (!TextUtils.isEmpty(binding.etClientExternalId.editableText.toString())) {
            clientPayload.externalId = binding.etClientExternalId.editableText.toString()
        }
        if (clientStaff?.isNotEmpty() == true) {
            clientPayload.staffId = staffId
        }
        if (genderOptionsList.isNotEmpty()) {
            clientPayload.genderId = genderId
        }
        if (clientTypeList.isNotEmpty()) {
            clientPayload.clientTypeId = clientTypeId
        }
        if (clientClassificationList.isNotEmpty()) {
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
                    clientsTemplate?.dataTables,
                    clientPayload, Constants.CREATE_CLIENT
                )
                val fragmentTransaction = requireActivity().supportFragmentManager
                    .beginTransaction()
                requireActivity().supportFragmentManager.popBackStackImmediate()
                fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST)
                fragmentTransaction.replace(R.id.container, fragment).commit()
            } else {
                clientPayload.datatables = null
                createNewClientPresenter.createClient(clientPayload)
            }
        }
    }

    private fun onClickActiveCheckBox() {
        binding.submissionDateFieldContainer.visibility =
            if (binding.cbClientActiveStatus.isChecked) View.VISIBLE else View.GONE
    }

    override fun showClientTemplate(clientsTemplate: ClientsTemplate?) {
        this.clientsTemplate = clientsTemplate
        if (clientsTemplate != null) {
            if (clientsTemplate.dataTables.isNotEmpty()) {
                hasDataTables = true
            }
        }
        genderOptionsList.addAll(
            createNewClientPresenter.filterOptions(clientsTemplate?.genderOptions)
        )
        binding.genderListField.setSimpleItems(genderOptionsList.toTypedArray())

        clientTypeList.addAll(
            createNewClientPresenter.filterOptions(clientsTemplate?.clientTypeOptions)
        )
        binding.clientTypeListField.setSimpleItems(clientTypeList.toTypedArray())

        clientClassificationList.addAll(
            createNewClientPresenter
                .filterOptions(clientsTemplate?.clientClassificationOptions)
        )
        binding.clientTypeListField.setSimpleItems(clientTypeList.toTypedArray())
    }

    override fun showOffices(offices: List<Office?>?) {
        clientOffices = offices as List<Office>?
        officeList.addAll(createNewClientPresenter.filterOffices(offices))
        officeList.sort()
        binding.officeListField.setSimpleItems(officeList.toTypedArray())
    }

    override fun showStaffInOffices(staffs: List<Staff?>?) {
        if (staffs != null) {
            if (staffs.isEmpty()) {
                showMessage(R.string.no_staff_associated_with_office)
            }
        }
        clientStaff = staffs as List<Staff>?
        staffList.clear()
        staffList.addAll(createNewClientPresenter.filterStaff(staffs))
        binding.staffListField.setSimpleItems(staffList.toTypedArray())
    }

    override fun showClientCreatedSuccessfully(message: Int) {
        Toaster.show(binding.root, message)
    }

    override fun showWaitingForCheckerApproval(message: Int) {
        Toaster.show(binding.root, message)
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showMessage(message: Int) {
        Toaster.show(binding.root, message)
    }

    override fun showMessage(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(show: Boolean) {
        showProgress(show)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createNewClientPresenter.detachView()
    }

    private val isFirstNameValid: Boolean
        get() {
            result = true
            try {
                if (TextUtils.isEmpty(binding.etClientFirstName.editableText.toString())) {
                    throw RequiredFieldException(
                        resources.getString(R.string.first_name),
                        resources.getString(R.string.error_cannot_be_empty)
                    )
                }
                if (!ValidationUtil.isNameValid(binding.etClientFirstName.editableText.toString())) {
                    throw InvalidTextInputException(
                        resources.getString(R.string.first_name),
                        resources.getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS
                    )
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

    private val isMiddleNameValid: Boolean
        get() {
            result = true
            try {
                if (!TextUtils.isEmpty(binding.etClientMiddleName.editableText.toString())
                    && !ValidationUtil.isNameValid(
                        binding.etClientMiddleName.editableText
                            .toString()
                    )
                ) {
                    throw InvalidTextInputException(
                        resources.getString(R.string.middle_name),
                        resources.getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS
                    )
                }
            } catch (e: InvalidTextInputException) {
                e.notifyUserWithToast(activity)
                result = false
            }
            return result
        }

    private val isLastNameValid: Boolean
        get() {
            result = true
            try {
                if (TextUtils.isEmpty(binding.etClientLastName.editableText.toString())) {
                    throw RequiredFieldException(
                        resources.getString(R.string.last_name),
                        resources.getString(R.string.error_cannot_be_empty)
                    )
                }
                if (!ValidationUtil.isNameValid(binding.etClientLastName.editableText.toString())) {
                    throw InvalidTextInputException(
                        resources.getString(R.string.last_name),
                        resources.getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS
                    )
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
            ClientImageFile?.let { createNewClientPresenter.uploadImage(returnedClientId!!, it) }
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
        ) {
            createClientWithImage = true
            binding.ivClientImage.setImageBitmap(BitmapFactory.decodeFile(ClientImageFile!!.absolutePath))
        } else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data?.data != null
        ) {
            createClientWithImage = true
            pickedImageUri = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val c = requireActivity().contentResolver.query(
                pickedImageUri!!, filePath,
                null, null, null
            )!!
            c.moveToFirst()
            val columnIndex = c.getColumnIndex(filePath[0])
            val picturePath = c.getString(columnIndex)
            binding.ivClientImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))
            ClientImageFile = File(picturePath)
        }
    }

    override fun showProgress(message: String?) {
        if (progress == null) {
            progress = ProgressDialog(activity, ProgressDialog.STYLE_SPINNER)
            progress?.setCancelable(false)
        }
        progress?.setMessage(message)
        progress?.show()
    }

    override fun hideProgress() {
        if (progress != null && progress!!.isShowing) progress?.dismiss()
    }

    //permission is automatically granted on sdk<23 upon installation
    val isStoragePermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            uploadClientImageFromDevice()
        }
    }

    companion object {
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1
        private const val PICK_IMAGE_ACTIVITY_REQUEST_CODE = 2
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 3
    }
}