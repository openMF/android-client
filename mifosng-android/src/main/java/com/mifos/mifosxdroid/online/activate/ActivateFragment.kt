package com.mifos.mifosxdroid.online.activate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.client.ActivatePayload
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 09/02/17.
 */
class ActivateFragment : MifosBaseFragment(), ActivateMvpView, OnDatePickListener {
    @JvmField
    @BindView(R.id.tv_activation_date)
    var tvActivationDate: TextView? = null

    @JvmField
    @Inject
    var activatePresenter: ActivatePresenter? = null
    private lateinit var rootView: View
    private var mfDatePicker: DialogFragment? = null
    private var activationDate: String? = null
    private var id = 0
    private var activateType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent?.inject(this)
        if (arguments != null) {
            id = requireArguments().getInt(Constants.ID)
            activateType = requireArguments().getString(Constants.ACTIVATE_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_activate_client, container, false)
        ButterKnife.bind(this, rootView)
        activatePresenter?.attachView(this)
        showUserInterface()
        return rootView
    }

    override fun showUserInterface() {
        setToolbarTitle(getString(R.string.activate))
        mfDatePicker = MFDatePicker.newInsance(this)
        tvActivationDate?.text = MFDatePicker.datePickedAsString
        activationDate = tvActivationDate?.text.toString()
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationDate)
            .replace("-", " ")
    }

    @OnClick(R.id.btn_activate)
    fun onClickActivationButton() {
        val clientActivate = ActivatePayload(activationDate)
        activate(clientActivate)
    }

    @OnClick(R.id.tv_activation_date)
    fun onClickTextViewActivationDate() {
        mfDatePicker?.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun onDatePicked(date: String?) {
        tvActivationDate?.text = date
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
            .replace("-", " ")
    }

    fun activate(clientActivate: ActivatePayload?) {
        when (activateType) {
            Constants.ACTIVATE_CLIENT -> activatePresenter?.activateClient(id, clientActivate)
            Constants.ACTIVATE_CENTER -> activatePresenter?.activateCenter(id, clientActivate)
            Constants.ACTIVATE_GROUP -> activatePresenter?.activateGroup(id, clientActivate)
            else -> {}
        }
    }

    override fun showActivatedSuccessfully(message: Int) {
        Toast.makeText(
            activity,
            getString(message), Toast.LENGTH_SHORT
        ).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showError(errorMessage: String) {
        show(rootView, errorMessage, Toaster.INDEFINITE)
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activatePresenter?.detachView()
    }

    companion object {
        val LOG_TAG = ActivateFragment::class.java.simpleName
        fun newInstance(id: Int, activationType: String?): ActivateFragment {
            val activateFragment = ActivateFragment()
            val args = Bundle()
            args.putInt(Constants.ID, id)
            args.putString(Constants.ACTIVATE_TYPE, activationType)
            activateFragment.arguments = args
            return activateFragment
        }
    }
}