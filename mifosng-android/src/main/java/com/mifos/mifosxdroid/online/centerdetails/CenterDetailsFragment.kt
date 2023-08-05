package com.mifos.mifosxdroid.online.centerdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentCenterDetailsBinding
import com.mifos.objects.group.CenterInfo
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.utils.Constants
import com.mifos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 05/02/17.
 */
@AndroidEntryPoint
class CenterDetailsFragment : MifosBaseFragment(), CenterDetailsMvpView {

    private lateinit var binding: FragmentCenterDetailsBinding

    @Inject
    lateinit var centerDetailsPresenter: CenterDetailsPresenter
    private var centerId = 0

    //    private var listener: OnFragmentInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            centerId = requireArguments().getInt(Constants.CENTER_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCenterDetailsBinding.inflate(inflater, container, false)
        centerDetailsPresenter.attachView(this)
        centerDetailsPresenter.loadCentersGroupAndMeeting(centerId)
        centerDetailsPresenter.loadSummaryInfo(centerId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnActivateCenter.setOnClickListener {
            onClickActivateCenter()
        }
    }


    private fun onClickActivateCenter() {
        val action = CenterDetailsFragmentDirections.actionCenterDetailsFragmentToActivateFragment(
            centerId,
            Constants.ACTIVATE_CENTER
        )
        findNavController().navigate(action)
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
        centerWithAssociations?.name?.let { setToolbarTitle(it) }
        if (centerWithAssociations?.activationDate?.isNotEmpty() == true) {
            if (centerWithAssociations.staffName != null) {
                binding.tvStaffName.text = centerWithAssociations.staffName
            } else {
                binding.tvStaffName.setText(R.string.no_staff)
            }
            binding.tvCenterActivationDate.text =
                Utils.getStringOfDate(centerWithAssociations.activationDate)
        }
    }

    override fun showMeetingDetails(centerWithAssociations: CenterWithAssociations?) {
        if (!centerWithAssociations!!.active!!) {
            binding.llBottomPanel.visibility = View.VISIBLE
            showErrorMessage(R.string.error_center_inactive)
        }
        if (centerWithAssociations.collectionMeetingCalendar.calendarInstanceId == null) {
            binding.tvMeetingDate.text = getString(R.string.unassigned)
            if (view != null) {
                requireView().findViewById<View>(R.id.row_meeting_frequency).visibility = View.GONE
            }
        } else {
            binding.tvMeetingDate.text = Utils.getStringOfDate(
                centerWithAssociations
                    .collectionMeetingCalendar.nextTenRecurringDates[0]
            )
            if (view != null) {
                requireView().findViewById<View>(R.id.row_meeting_frequency).visibility =
                    View.VISIBLE
                binding.tvMeetingFrequency.text = centerWithAssociations.collectionMeetingCalendar
                    .humanReadable
            }
        }
    }

    override fun showSummaryInfo(centerInfos: List<CenterInfo?>?) {
        val centerInfo = centerInfos?.get(0)
        binding.tvActiveClients.text = centerInfo?.activeClients.toString()
        binding.tvActiveGroupLoans.text = centerInfo?.activeGroupLoans.toString()
        binding.tvActiveClientLoans.text = centerInfo?.activeClientLoans.toString()
        binding.tvActiveClientBorrowers.text = centerInfo?.activeClientBorrowers.toString()
        binding.tvActiveGroupBorrowers.text = centerInfo?.activeGroupBorrowers.toString()
        binding.tvActiveOverdueClientLoans.text = centerInfo?.overdueClientLoans.toString()
        binding.tvActiveOverdueGroupLoans.text = centerInfo?.overdueGroupLoans.toString()
    }

    override fun showErrorMessage(message: Int) {
        Toaster.show(binding.root, message)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_center, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_savings_account -> addCenterSavingAccount(centerId)
            R.id.view_group_list -> loadGroupsOfCenter(centerId)
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onAttach(activity: Activity) {
//        super.onAttach(activity)
//        listener = try {
//            activity as OnFragmentInteractionListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException(
//                activity.toString() + " must implement " +
//                        "OnFragmentInteractionListener"
//            )
//        }
//    }

    interface OnFragmentInteractionListener {
        fun addCenterSavingAccount(centerId: Int)
        fun loadGroupsOfCenter(centerId: Int)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        centerDetailsPresenter.detachView()
    }

    private fun addCenterSavingAccount(centerId: Int) {
        val action =
            CenterDetailsFragmentDirections.actionCenterDetailsFragmentToSavingsAccountFragment(
                centerId,
                true
            )
        findNavController().navigate(action)
    }

    private fun loadGroupsOfCenter(centerId: Int) {
        val action =
            CenterDetailsFragmentDirections.actionCenterDetailsFragmentToGroupListFragment(centerId)
        findNavController().navigate(action)
    }

}