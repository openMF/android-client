package com.mifos.mifosxdroid.online.centerdetails

import android.app.Activity
import android.os.Bundle
import android.view.*
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentCenterDetailsBinding
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

    private lateinit var binding: FragmentCenterDetailsBinding

    @JvmField
    @Inject
    var centerDetailsPresenter: CenterDetailsPresenter? = null
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
        binding = FragmentCenterDetailsBinding.inflate(inflater,container,false)
        centerDetailsPresenter!!.attachView(this)
        centerDetailsPresenter!!.loadCentersGroupAndMeeting(centerId)
        centerDetailsPresenter!!.loadSummaryInfo(centerId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnActivateCenter.setOnClickListener { onClickActivateCenter() }
    }

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
            binding.rlCenter.visibility = View.GONE
            showMifosProgressBar()
        } else {
            binding.rlCenter.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }

    override fun showCenterDetails(centerWithAssociations: CenterWithAssociations?) {
        setToolbarTitle(centerWithAssociations!!.name)
        if (centerWithAssociations.activationDate.isNotEmpty()) {
            if (centerWithAssociations.staffName != null) {
                binding.tvStaffName.text = centerWithAssociations.staffName
            } else {
                binding.tvStaffName.setText(R.string.no_staff)
            }
            binding.tvCenterActivationDate.text = Utils.getStringOfDate(centerWithAssociations.activationDate)
        }
    }

    override fun showMeetingDetails(centerWithAssociations: CenterWithAssociations?) {
        if (!centerWithAssociations!!.active) {
            binding.llBottomPanel.visibility = View.VISIBLE
            showErrorMessage(R.string.error_center_inactive)
        }
        if (centerWithAssociations.collectionMeetingCalendar.calendarInstanceId == null) {
            binding.tvMeetingDate.text = getString(R.string.unassigned)
            if (view != null) {
                requireView().findViewById<View>(R.id.row_meeting_frequency).visibility = View.GONE
            }
        } else {
            binding.tvMeetingDate.text = Utils.getStringOfDate(centerWithAssociations
                    .collectionMeetingCalendar.nextTenRecurringDates[0])
            if (view != null) {
                requireView().findViewById<View>(R.id.row_meeting_frequency).visibility = View.VISIBLE
                binding.tvMeetingFrequency.text = centerWithAssociations.collectionMeetingCalendar
                        .humanReadable
            }
        }
    }

    override fun showSummaryInfo(centerInfos: List<CenterInfo?>?) {
        val centerInfo = centerInfos?.get(0)
        binding.tvActiveClients.text = centerInfo!!.activeClients.toString()
        binding.tvActiveGroupLoans.text = centerInfo.activeGroupLoans.toString()
        binding.tvActiveClientLoans.text = centerInfo.activeClientLoans.toString()
        binding.tvActiveClientBorrowers.text = centerInfo.activeClientBorrowers.toString()
        binding.tvActiveGroupBorrowers.text = centerInfo.activeGroupBorrowers.toString()
        binding.tvActiveOverdueClientLoans.text = centerInfo.overdueClientLoans.toString()
        binding.tvActiveOverdueGroupLoans.text = centerInfo.overdueGroupLoans.toString()
    }

    override fun showErrorMessage(message: Int) {
        Toaster.show(binding.root, message)
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