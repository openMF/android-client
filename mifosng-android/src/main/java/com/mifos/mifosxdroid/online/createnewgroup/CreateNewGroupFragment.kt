/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.createnewgroup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.exceptions.InvalidTextInputException
import com.mifos.exceptions.RequiredFieldException
import com.mifos.exceptions.ShortOfLengthException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.online.GroupsActivity
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.group.GroupPayload
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse
import com.mifos.utils.*
import java.util.*
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 */ //TODO Show Image and Text after successful or Failed during creation of Group and
//TODO A button to Continue or Finish the GroupCreation.
class CreateNewGroupFragment : ProgressableFragment(), OnDatePickListener, CreateNewGroupMvpView, OnItemSelectedListener {
    private val LOG_TAG = javaClass.simpleName

    @JvmField
    @BindView(R.id.et_group_name)
    var et_groupName: EditText? = null

    @JvmField
    @BindView(R.id.et_group_external_id)
    var et_groupexternalId: EditText? = null

    @JvmField
    @BindView(R.id.cb_group_active_status)
    var cb_groupActiveStatus: CheckBox? = null

    @JvmField
    @BindView(R.id.tv_group_submission_date)
    var tv_submissionDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_group_activationDate)
    var tv_activationDate: TextView? = null

    @JvmField
    @BindView(R.id.sp_group_offices)
    var sp_offices: Spinner? = null

    @JvmField
    @BindView(R.id.btn_submit)
    var bt_submit: Button? = null

    @JvmField
    @BindView(R.id.layout_submission)
    var layout_submission: LinearLayout? = null

    @JvmField
    @Inject
    var mCreateNewGroupPresenter: CreateNewGroupPresenter? = null
    var activationdateString: String? = null
    var officeId = 0
    var result = true
    lateinit var rootView: View
    var dateofsubmissionstring: String? = null
    private var mfDatePicker: DialogFragment? = null
    private var newDatePicker: DialogFragment? = null
    private val mListOffices: MutableList<String> = ArrayList()
    private var officeList: List<Office>? = null
    private var mOfficesAdapter: ArrayAdapter<String>? = null
    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (parent.id == R.id.sp_group_offices) {
            officeId = officeList!![position].id
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_create_new_group, null)
        ButterKnife.bind(this, rootView)
        mCreateNewGroupPresenter!!.attachView(this)
        inflateOfficesSpinner()
        inflateSubmissionDate()
        inflateActivationDate()
        mCreateNewGroupPresenter!!.loadOffices()

        //client active checkbox onCheckedListener
        cb_groupActiveStatus!!.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                layout_submission!!.visibility = View.VISIBLE
            } else {
                layout_submission!!.visibility = View.GONE
            }
        }
        activationdateString = tv_activationDate!!.text.toString()
        activationdateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationdateString).replace("-", " ")
        dateofsubmissionstring = tv_submissionDate!!.text.toString()
        dateofsubmissionstring = DateHelper.getDateAsStringUsedForDateofBirth(dateofsubmissionstring).replace("-", " ")
        bt_submit!!.setOnClickListener {
            if (Network.isOnline(context)) {
                val groupPayload = GroupPayload()
                groupPayload.name = et_groupName!!.editableText.toString()
                groupPayload.externalId = et_groupexternalId!!.editableText.toString()
                groupPayload.isActive = cb_groupActiveStatus!!.isChecked
                groupPayload.activationDate = activationdateString
                groupPayload.setSubmissionDate(dateofsubmissionstring)
                groupPayload.officeId = officeId
                groupPayload.dateFormat = "dd MMMM yyyy"
                groupPayload.locale = "en"
                initiateGroupCreation(groupPayload)
            } else {
                Toaster.show(rootView, R.string.error_network_not_available, Toaster.LONG)
            }
        }
        return rootView
    }

    private fun initiateGroupCreation(groupPayload: GroupPayload) {
        //TextField validations
        if (!isGroupNameValid) {
            return
        }
        mCreateNewGroupPresenter!!.createGroup(groupPayload)
    }

    private fun inflateOfficesSpinner() {
        mOfficesAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item,
                mListOffices)
        mOfficesAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_offices!!.adapter = mOfficesAdapter
        sp_offices!!.onItemSelectedListener = this
    }

    fun inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        tv_submissionDate!!.text = MFDatePicker.getDatePickedAsString()
        tv_submissionDate!!.setOnClickListener { (mfDatePicker as MFDatePicker?)?.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER) }
    }

    fun inflateActivationDate() {
        newDatePicker = MFDatePicker.newInsance(this)
        tv_activationDate!!.text = MFDatePicker.getDatePickedAsString()
        tv_activationDate!!.setOnClickListener { (newDatePicker as MFDatePicker?)?.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER) }
    }

    override fun onDatePicked(date: String) {
        tv_submissionDate!!.text = date
        tv_activationDate!!.text = date
    }

    val isGroupNameValid: Boolean
        get() {
            result = true
            try {
                if (TextUtils.isEmpty(et_groupName!!.editableText.toString())) {
                    throw RequiredFieldException(resources.getString(R.string.group_name),
                            resources.getString(R.string.error_cannot_be_empty))
                }
                if (et_groupName!!.editableText.toString().trim { it <= ' ' }.length < 4 && et_groupName!!
                                .getEditableText().toString().trim { it <= ' ' }.length > 0) {
                    throw ShortOfLengthException(resources.getString(R.string.group_name), 4)
                }
                if (!ValidationUtil.isNameValid(et_groupName!!.editableText.toString())) {
                    throw InvalidTextInputException(resources.getString(R.string.group_name)
                            , resources.getString(R.string.error_should_contain_only),
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
        officeList = offices as List<Office>?
        if (offices != null) {
            for (office in offices) {
                mListOffices.add(office.name)
            }
        }
        Collections.sort(mListOffices)
        mOfficesAdapter!!.notifyDataSetChanged()
    }

    override fun showGroupCreatedSuccessfully(group: SaveResponse?) {
        Toast.makeText(activity, "Group " + MifosResponseHandler.getResponse(),
                Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
        if (PrefManager.getUserStatus() == Constants.USER_ONLINE) {
            val groupActivityIntent = Intent(activity, GroupsActivity::class.java)
            groupActivityIntent.putExtra(Constants.GROUP_ID, group?.groupId)
            startActivity(groupActivityIntent)
        }
    }

    override fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCreateNewGroupPresenter!!.detachView()
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance(): CreateNewGroupFragment {
            return CreateNewGroupFragment()
        }
    }
}