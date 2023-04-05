/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.createnewcenter

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mifos.exceptions.InvalidTextInputException
import com.mifos.exceptions.RequiredFieldException
import com.mifos.exceptions.ShortOfLengthException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.FragmentCreateNewCenterBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse
import com.mifos.services.data.CenterPayload
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import com.mifos.utils.MifosResponseHandler
import com.mifos.utils.ValidationUtil
import java.util.*
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 */
class CreateNewCenterFragment : MifosBaseFragment(), OnDatePickListener, CreateNewCenterMvpView {

    private lateinit var binding: FragmentCreateNewCenterBinding

    var officeId = 0
    var result = true

    @JvmField
    @Inject
    var mCreateNewCenterPresenter: CreateNewCenterPresenter? = null
    private var activationdateString: String? = null
    private var newDatePicker: DialogFragment? = null
    private val officeNameIdHashMap = HashMap<String, Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCreateNewCenterBinding.inflate(inflater,container,false)
        mCreateNewCenterPresenter!!.attachView(this)
        inflateOfficeSpinner()
        inflateActivationDate()
        //client active checkbox onCheckedListener
        binding.cbCenterActiveStatus.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.layoutSubmission.visibility = View.VISIBLE
                activationdateString = binding.tvCenterActivationDate.text.toString()
                activationdateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationdateString).replace("-", " ")
            } else {
                binding.layoutSubmission.visibility = View.GONE
            }
        }
        binding.btnSubmit.setOnClickListener {
            val centerPayload = CenterPayload()
            centerPayload.name = binding.etCenterName.editableText.toString()
            centerPayload.isActive = binding.cbCenterActiveStatus.isChecked
            centerPayload.activationDate = activationdateString
            centerPayload.officeId = officeId
            centerPayload.dateFormat = "dd MMMM yyyy"
            centerPayload.locale = "en"
            initiateCenterCreation(centerPayload)
        }
        return binding.root
    }

    //inflating office list spinner
    private fun inflateOfficeSpinner() {
        mCreateNewCenterPresenter!!.loadOffices()
    }

    private fun initiateCenterCreation(centerPayload: CenterPayload) {
        if (isCenterNameValid) {
            mCreateNewCenterPresenter!!.createCenter(centerPayload)
        }
    }

    fun inflateActivationDate() {
        newDatePicker = MFDatePicker.newInsance(this)
        binding.tvCenterActivationDate.text = MFDatePicker.getDatePickedAsString()
        binding.tvCenterActivationDate.setOnClickListener { (newDatePicker as MFDatePicker?)!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER) }
    }

    override fun onDatePicked(date: String) {
        binding.tvCenterActivationDate.text = date
    }

    val isCenterNameValid: Boolean
        get() {
            result = true
            try {
                if (TextUtils.isEmpty(binding.etCenterName.editableText.toString())) {
                    throw RequiredFieldException(resources.getString(R.string.center_name),
                            resources.getString(R.string.error_cannot_be_empty))
                }
                if (binding.etCenterName.editableText.toString().trim { it <= ' ' }.length < 4 && binding.etCenterName
                                .getEditableText().toString().trim { it <= ' ' }.length > 0) {
                    throw ShortOfLengthException(resources.getString(R.string.center_name), 4)
                }
                if (!ValidationUtil.isNameValid(binding.etCenterName.editableText.toString())) {
                    throw InvalidTextInputException(
                            resources.getString(R.string.center_name),
                            resources.getString(R.string.error_should_contain_only),
                            InvalidTextInputException.TYPE_ALPHABETS)
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

    override fun showOffices(offices: List<Office?>?) {
        val officeList: MutableList<String> = ArrayList()
        for (office in offices!!) {
            officeList.add(office!!.name)
            officeNameIdHashMap[office.name] = office.id
        }
        Collections.sort(officeList)
        val officeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, officeList)
        officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCenterOffices.adapter = officeAdapter
        binding.spCenterOffices.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?,
                                        view: View, i: Int, l: Long) {
                officeId = officeNameIdHashMap[officeList[i]]!!
                Log.d("officeId " + officeList[i], officeId.toString())
                if (officeId != -1) {
                } else {
                    Toast.makeText(activity, getString(R.string.error_select_office)
                            , Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun centerCreatedSuccessfully(saveResponse: SaveResponse?) {
        Toast.makeText(activity, "Center " + MifosResponseHandler.getResponse(),
                Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showFetchingError(errorMessage: Int) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            binding.llCenter.visibility = View.GONE
            showMifosProgressBar()
        } else {
            binding.llCenter.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCreateNewCenterPresenter!!.detachView()
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        private const val TAG = "CreateNewCenter"
        @JvmStatic
        fun newInstance(): CreateNewCenterFragment {
            return CreateNewCenterFragment()
        }
    }
}