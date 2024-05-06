/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.createnewgroup

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import com.mifos.exceptions.InvalidTextInputException
import com.mifos.exceptions.RequiredFieldException
import com.mifos.exceptions.ShortOfLengthException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentCreateNewGroupBinding
import com.mifos.mifosxdroid.online.GroupsActivity
import com.mifos.utils.DatePickerConstrainType
import com.mifos.utils.FragmentConstants
import com.mifos.utils.MifosResponseHandler
import com.mifos.utils.Network
import com.mifos.utils.PrefManager
import com.mifos.utils.ValidationUtil
import com.mifos.utils.getDatePickerDialog
import com.mifos.utils.getTodayFormatted
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

/**
 * Created by nellyk on 1/22/2016.
 */ //TODO Show Image and Text after successful or Failed during creation of Group and
//TODO A button to Continue or Finish the GroupCreation.
@AndroidEntryPoint
class CreateNewGroupFragment : ProgressableFragment() {

    private lateinit var binding: FragmentCreateNewGroupBinding

    private lateinit var viewModel: CreateNewGroupViewModel

    private var activationDateString: String? = null
    var officeId: Int? = 0
    var result = true
    private var dateofsubmissionstring: String? = null
    private val mListOffices: MutableList<String> = ArrayList()
    private var officeList: List<Office> = ArrayList()

    private var submissionDate: Instant = Instant.now()
    private val submissionDatePickerDialog by lazy {
        getDatePickerDialog(submissionDate, DatePickerConstrainType.ONLY_FUTURE_DAYS) {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it)
            submissionDate = Instant.ofEpochMilli(it)
            binding.submittedDateFieldContainer.editText?.setText(formattedDate)
            dateofsubmissionstring = binding.submittedDateFieldContainer.editText.toString()
        }
    }
    private var activationDate: Instant = Instant.now()
    private val activationDatePickerDialog by lazy {
        getDatePickerDialog(activationDate, DatePickerConstrainType.ONLY_FUTURE_DAYS) {
            val formattedDate = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(it)
            activationDate = Instant.ofEpochMilli(it)
            binding.activateDateFieldContainer.editText?.setText(formattedDate)
            activationDateString = binding.activateDateFieldContainer.editText.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNewGroupBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CreateNewGroupViewModel::class.java]
        inflateSubmissionDate()
        inflateActivationDate()
        viewModel.loadOffices()

        //client active checkbox onCheckedListener
        dateofsubmissionstring = getTodayFormatted()
        binding.submittedDateFieldContainer.editText?.setText(getTodayFormatted())

        activationDateString = getTodayFormatted()
        binding.activateDateFieldContainer.editText?.setText(getTodayFormatted())

        viewModel.createNewGroupUiState.observe(viewLifecycleOwner) {
            when (it) {
                is CreateNewGroupUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is CreateNewGroupUiState.ShowGroupCreatedSuccessfully -> {
                    showProgressbar(false)
                    showGroupCreatedSuccessfully(it.saveResponse)
                }

                is CreateNewGroupUiState.ShowOffices -> {
                    showProgressbar(false)
                    showOffices(it.offices)
                }

                is CreateNewGroupUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            if (Network.isOnline(requireContext())) {
                val groupPayload = GroupPayload()
                groupPayload.name = binding.etGroupName.editableText.toString()
                groupPayload.externalId = binding.etGroupExternalId.editableText.toString()
                groupPayload.active = binding.cbGroupActiveStatus.isChecked
                groupPayload.activationDate = activationDateString
                groupPayload.submittedOnDate = dateofsubmissionstring
                groupPayload.officeId = officeId!!
                groupPayload.dateFormat = "dd MMMM yyyy"
                groupPayload.locale = "en"
                initiateGroupCreation(groupPayload)
            } else {
                Toaster.show(binding.root, R.string.error_network_not_available, Toaster.LONG)
            }
        }

        binding.cbGroupActiveStatus.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.activateDateFieldContainer.visibility = View.VISIBLE
            } else {
                binding.activateDateFieldContainer.visibility = View.GONE
            }
        }

        binding.officeListField.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val index = mListOffices.indexOf(adapterView.getItemAtPosition(relativePosition))
            officeId = officeList[index].id
        }
    }

    private fun initiateGroupCreation(groupPayload: GroupPayload) {
        //TextField validations
        if (!isGroupNameValid) {
            return
        }
        viewModel.createGroup(groupPayload)
    }

    private fun inflateSubmissionDate() {
        binding.submittedDateFieldContainer.setEndIconOnClickListener {
            submissionDatePickerDialog.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }
    }

    private fun inflateActivationDate() {
        binding.activateDateFieldContainer.setEndIconOnClickListener {
            activationDatePickerDialog.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }
    }

    private val isGroupNameValid: Boolean
        get() {
            result = true
            try {
                if (TextUtils.isEmpty(binding.etGroupName.editableText.toString())) {
                    throw RequiredFieldException(
                        resources.getString(R.string.group_name),
                        resources.getString(R.string.error_cannot_be_empty)
                    )
                }
                if (binding.etGroupName.editableText.toString()
                        .trim { it <= ' ' }.length < 4 && binding.etGroupName
                        .editableText.toString().trim { it <= ' ' }.isNotEmpty()
                ) {
                    throw ShortOfLengthException(resources.getString(R.string.group_name), 4)
                }
                if (!ValidationUtil.isNameValid(binding.etGroupName.editableText.toString())) {
                    throw InvalidTextInputException(
                        resources.getString(R.string.group_name),
                        resources.getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS
                    )
                }
            } catch (e: InvalidTextInputException) {
                e.notifyUserWithToast(activity)
                result = false
            } catch (e: ShortOfLengthException) {
                e.notifyUserWithToast(activity)
                result = false
            } catch (e: RequiredFieldException) {
                e.notifyUserWithToast(activity)
                result = false
            }
            return result
        }

    private fun showOffices(offices: List<Office>) {
        officeList = offices
        for (office in offices) {
            office.name?.let { mListOffices.add(it) }
        }
        mListOffices.sort()
        binding.officeListField.setSimpleItems(mListOffices.toTypedArray())
    }

    private fun showGroupCreatedSuccessfully(group: SaveResponse?) {
        Toast.makeText(
            activity, "Group " + MifosResponseHandler.response,
            Toast.LENGTH_LONG
        ).show()
        requireActivity().supportFragmentManager.popBackStack()
        if (PrefManager.userStatus == Constants.USER_ONLINE) {
            val groupActivityIntent = Intent(activity, GroupsActivity::class.java)
            groupActivityIntent.putExtra(
                Constants.GROUP_ID,
                group?.groupId
            )
            startActivity(groupActivityIntent)
        }
    }

    private fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressbar(b: Boolean) {
        showProgress(b)
    }
}