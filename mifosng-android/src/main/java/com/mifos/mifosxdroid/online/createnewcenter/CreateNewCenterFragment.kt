/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.createnewcenter

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import com.mifos.exceptions.InvalidTextInputException
import com.mifos.exceptions.RequiredFieldException
import com.mifos.exceptions.ShortOfLengthException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.FragmentCreateNewCenterBinding
import com.mifos.utils.DatePickerConstrainType
import com.mifos.utils.FragmentConstants
import com.mifos.utils.MifosResponseHandler
import com.mifos.utils.ValidationUtil
import com.mifos.utils.getDatePickerDialog
import com.mifos.utils.getTodayFormatted
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

/**
 * Created by nellyk on 1/22/2016.
 */
@AndroidEntryPoint
class CreateNewCenterFragment : MifosBaseFragment() {

    private lateinit var binding: FragmentCreateNewCenterBinding

    var officeId: Int? = 0
    var result = true

    private lateinit var viewModel: CreateNewCenterViewModel

    private var activationDateString: String? = null
    private val officeNameIdHashMap = HashMap<String, Int>()

    private var activationDate: Instant = Instant.now()
    private val submissionDatePickerDialog by lazy {
        getDatePickerDialog(activationDate, DatePickerConstrainType.ONLY_FUTURE_DAYS) {
            val formattedDate = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(it)
            activationDate = Instant.ofEpochMilli(it)
            binding.activateDateFieldContainer.editText?.setText(formattedDate)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNewCenterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CreateNewCenterViewModel::class.java]
        inflateOfficeSpinner()
        inflateActivationDate()
        //client active checkbox onCheckedListener

        viewModel.createNewCenterUiState.observe(viewLifecycleOwner) {
            when (it) {
                is CreateNewCenterUiState.CenterCreatedSuccessfully -> {
                    showProgressbar(false)
                    centerCreatedSuccessfully(it.saveResponse)
                }

                is CreateNewCenterUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is CreateNewCenterUiState.ShowFetchingErrorString -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is CreateNewCenterUiState.ShowOffices -> {
                    showProgressbar(false)
                    showOffices(it.offices)
                }

                is CreateNewCenterUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cbCenterActiveStatus.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.activateDateFieldContainer.visibility = View.VISIBLE
                activationDateString = binding.activateDateFieldContainer.editText?.text.toString()
            } else {
                binding.activateDateFieldContainer.visibility = View.GONE
            }
        }
        binding.btnSubmit.setOnClickListener {
            val centerPayload = CenterPayload()
            centerPayload.name = binding.etCenterName.editableText.toString()
            centerPayload.active = binding.cbCenterActiveStatus.isChecked
            centerPayload.activationDate = activationDateString
            centerPayload.officeId = officeId
            centerPayload.dateFormat = "dd MMMM yyyy"
            centerPayload.locale = "en"
            initiateCenterCreation(centerPayload)
        }

        binding.activateDateFieldContainer.setEndIconOnClickListener {
            submissionDatePickerDialog.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }
    }

    //inflating office list spinner
    private fun inflateOfficeSpinner() {
        viewModel.loadOffices()
    }

    private fun initiateCenterCreation(centerPayload: CenterPayload) {
        if (isCenterNameValid) {
            viewModel.createCenter(centerPayload)
        }
    }

    private fun inflateActivationDate() {
        binding.activateDateFieldContainer.editText?.setText(getTodayFormatted())
    }

    private val isCenterNameValid: Boolean
        get() {
            result = true
            try {
                if (TextUtils.isEmpty(binding.etCenterName.editableText.toString())) {
                    throw RequiredFieldException(
                        resources.getString(R.string.center_name),
                        resources.getString(R.string.error_cannot_be_empty)
                    )
                }
                if (binding.etCenterName.editableText.toString()
                        .trim { it <= ' ' }.length < 4 && binding.etCenterName
                        .editableText.toString().trim { it <= ' ' }.isNotEmpty()
                ) {
                    throw ShortOfLengthException(resources.getString(R.string.center_name), 4)
                }
                if (!ValidationUtil.isNameValid(binding.etCenterName.editableText.toString())) {
                    throw InvalidTextInputException(
                        resources.getString(R.string.center_name),
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
        val officeList: MutableList<String> = ArrayList()
        if (offices != null) {
            for (office in offices) {
                if (office != null) {
                    office.name?.let { officeList.add(it) }
                }
                if (office != null) {
                    officeNameIdHashMap[office.name!!] = office.id!!
                }
            }
        }
        officeList.sort()

        binding.officeListField.setSimpleItems(officeList.toTypedArray())

        binding.officeListField.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val index = officeList.indexOf(adapterView.getItemAtPosition(relativePosition))
            officeId = officeNameIdHashMap[officeList[index]]
            if (officeId != -1) {
            } else {
                Toast.makeText(
                    activity, getString(R.string.error_select_office), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun centerCreatedSuccessfully(saveResponse: SaveResponse?) {
        Toast.makeText(
            activity, "Center " + MifosResponseHandler.response,
            Toast.LENGTH_LONG
        ).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun showFetchingError(errorMessage: Int) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressbar(show: Boolean) {
        if (show) {
            binding.llCenter.visibility = View.GONE
            showMifosProgressBar()
        } else {
            binding.llCenter.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }
}