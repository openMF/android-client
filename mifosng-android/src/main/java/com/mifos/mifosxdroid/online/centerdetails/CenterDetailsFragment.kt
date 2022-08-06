package com.mifos.mifosxdroid.online.centerdetails

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.online.activate.ActivateFragment
import com.mifos.objects.group.CenterInfo
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Utils
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 05/02/17.
 */
class CenterDetailsFragment : MifosBaseFragment(), CenterDetailsMvpView {
    @JvmField
    @BindView(R.id.tv_center_activation_date)
    var tvActivationDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_staff_name)
    var tvStaffName: TextView? = null

    @JvmField
    @BindView(R.id.tv_meeting_date)
    var tvMeetingDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_meeting_frequency)
    var tvMeetingFrequency: TextView? = null

    @JvmField
    @BindView(R.id.tv_active_clients)
    var tvActiveClients: TextView? = null

    @JvmField
    @BindView(R.id.tv_active_group_loans)
    var tvActiveGroupLoans: TextView? = null

    @JvmField
    @BindView(R.id.tv_active_client_loans)
    var tvActiveClientLoans: TextView? = null

    @JvmField
    @BindView(R.id.tv_active_group_borrowers)
    var tvActiveGroupBorrowers: TextView? = null

    @JvmField
    @BindView(R.id.tv_active_client_borrowers)
    var tvActiveClientBorrowers: TextView? = null

    @JvmField
    @BindView(R.id.tv_active_overdue_group_loans)
    var tvActiveOverdueGroupLoans: TextView? = null

    @JvmField
    @BindView(R.id.tv_active_overdue_client_loans)
    var tvActiveOverdueClientLoans: TextView? = null

    @JvmField
    @BindView(R.id.rl_center)
    var rlCenter: RelativeLayout? = null

    @JvmField
    @BindView(R.id.ll_bottom_panel)
    var llBottomPanel: LinearLayout? = null

    @JvmField
    @Inject
    var centerDetailsPresenter: CenterDetailsPresenter? = null
    lateinit var rootView: View
    private var centerId = 0
    private var listener: OnFragmentInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            centerId = requireArguments().getInt(Constants.CENTER_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_center_details, container, false)
        ButterKnife.bind(this, rootView)
        centerDetailsPresenter!!.attachView(this)
        centerDetailsPresenter!!.loadCentersGroupAndMeeting(centerId)
        centerDetailsPresenter!!.loadSummaryInfo(centerId)
        return rootView
    }

    @OnClick(R.id.btn_activate_center)
    fun onClickActivateCenter() {
        val activateFragment = ActivateFragment.newInstance(centerId, Constants.ACTIVATE_CENTER)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CENTER_DETAIL)
        fragmentTransaction.replace(R.id.container, activateFragment)
        fragmentTransaction.commit()
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            rlCenter!!.visibility = View.GONE
            showMifosProgressBar()
        } else {
            rlCenter!!.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }

    override fun showCenterDetails(centerWithAssociations: CenterWithAssociations?) {
        setToolbarTitle(centerWithAssociations!!.name)
        if (!centerWithAssociations.activationDate.isEmpty()) {
            if (centerWithAssociations.staffName != null) {
                tvStaffName!!.text = centerWithAssociations.staffName
            } else {
                tvStaffName!!.setText(R.string.no_staff)
            }
            tvActivationDate!!.text = Utils.getStringOfDate(centerWithAssociations.activationDate)
        }
    }

    override fun showMeetingDetails(centerWithAssociations: CenterWithAssociations?) {
        if (!centerWithAssociations!!.active) {
            llBottomPanel!!.visibility = View.VISIBLE
            showErrorMessage(R.string.error_center_inactive)
        }
        if (centerWithAssociations.collectionMeetingCalendar.calendarInstanceId == null) {
            tvMeetingDate!!.text = getString(R.string.unassigned)
            if (view != null) {
                view!!.findViewById<View>(R.id.row_meeting_frequency).visibility = View.GONE
            }
        } else {
            tvMeetingDate!!.text = Utils.getStringOfDate(centerWithAssociations
                    .collectionMeetingCalendar.nextTenRecurringDates[0])
            if (view != null) {
                view!!.findViewById<View>(R.id.row_meeting_frequency).visibility = View.VISIBLE
                tvMeetingFrequency!!.text = centerWithAssociations.collectionMeetingCalendar
                        .humanReadable
            }
        }
    }

    override fun showSummaryInfo(centerInfos: List<CenterInfo?>?) {
        val centerInfo = centerInfos?.get(0)
        tvActiveClients!!.text = centerInfo!!.activeClients.toString()
        tvActiveGroupLoans!!.text = centerInfo.activeGroupLoans.toString()
        tvActiveClientLoans!!.text = centerInfo.activeClientLoans.toString()
        tvActiveClientBorrowers!!.text = centerInfo.activeClientBorrowers.toString()
        tvActiveGroupBorrowers!!.text = centerInfo.activeGroupBorrowers.toString()
        tvActiveOverdueClientLoans!!.text = centerInfo.overdueClientLoans.toString()
        tvActiveOverdueGroupLoans!!.text = centerInfo.overdueGroupLoans.toString()
    }

    override fun showErrorMessage(message: Int) {
        Toaster.show(rootView, message)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_center, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuItemId = item.itemId
        when (menuItemId) {
            R.id.add_savings_account -> listener!!.addCenterSavingAccount(centerId)
            R.id.view_group_list -> listener!!.loadGroupsOfCenter(centerId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        listener = try {
            activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener")
        }
    }

    interface OnFragmentInteractionListener {
        fun addCenterSavingAccount(centerId: Int)
        fun loadGroupsOfCenter(centerId: Int)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        centerDetailsPresenter!!.detachView()
    }

    companion object {
        @JvmStatic
        fun newInstance(centerId: Int): CenterDetailsFragment {
            val fragment = CenterDetailsFragment()
            val args = Bundle()
            args.putInt(Constants.CENTER_ID, centerId)
            fragment.arguments = args
            return fragment
        }
    }
}