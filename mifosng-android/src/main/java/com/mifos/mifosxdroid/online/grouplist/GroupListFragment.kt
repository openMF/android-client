/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.grouplist

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.GroupListAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.databinding.FragmentGroupListBinding
import com.mifos.objects.client.Client
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.utils.Constants
import javax.inject.Inject

class GroupListFragment : ProgressableFragment(), GroupListMvpView, AdapterView.OnItemClickListener {

    private lateinit var binding: FragmentGroupListBinding

    @JvmField
    @Inject
    var mGroupListPresenter: GroupListPresenter? = null
    private var mGroupListAdapter: GroupListAdapter? = null
    private var mCenterWithAssociations: CenterWithAssociations? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var centerId = 0
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        mGroupListPresenter!!.loadGroups(
                mCenterWithAssociations!!.groupMembers[position].id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) centerId = requireArguments().getInt(Constants.CENTER_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGroupListBinding.inflate(inflater,container,false)
        mGroupListPresenter!!.attachView(this)
        setToolbarTitle(resources.getString(R.string.title_center_list))
        binding.lvGroupList.onItemClickListener = this
        inflateGroupList()
        return binding.root
    }

    fun inflateGroupList() {
        mGroupListPresenter!!.loadGroupByCenter(centerId)
    }

    override fun showGroupList(centerWithAssociations: CenterWithAssociations?) {
        if (centerWithAssociations != null) {
            if (centerWithAssociations.groupMembers.size == 0) {
                showEmptyGroups(R.string.empty_groups)
            } else {
                mCenterWithAssociations = centerWithAssociations
                mGroupListAdapter = GroupListAdapter(activity,
                        centerWithAssociations.groupMembers)
                binding.lvGroupList.adapter = mGroupListAdapter
            }
        }
    }

    override fun showGroups(groupWithAssociations: GroupWithAssociations?) {
        if (groupWithAssociations != null) mListener!!.loadClientsOfGroup(groupWithAssociations
                .clientMembers)
    }

    override fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    override fun showEmptyGroups(message: Int) {
        binding.viewFlipper.visibility = View.GONE
        binding.llError.visibility = View.VISIBLE
        binding.noGroupsText.text = getStringMessage(message)
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = try {
            activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mGroupListPresenter!!.detachView()
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun loadClientsOfGroup(clientList: List<Client?>?)
    }

    companion object {
        @JvmStatic
        fun newInstance(centerId: Int): GroupListFragment {
            val fragment = GroupListFragment()
            val args = Bundle()
            args.putInt(Constants.CENTER_ID, centerId)
            fragment.arguments = args
            return fragment
        }
    }
}