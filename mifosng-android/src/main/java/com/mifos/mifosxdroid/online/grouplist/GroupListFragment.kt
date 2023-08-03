/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.grouplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.GroupListAdapter
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.databinding.FragmentGroupListBinding
import com.mifos.objects.client.Client
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.objects.navigation.ClientListArgs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupListFragment : ProgressableFragment(), GroupListMvpView,
    AdapterView.OnItemClickListener {

    private lateinit var binding: FragmentGroupListBinding
    private val arg: GroupListFragmentArgs by navArgs()

    @Inject
    lateinit var mGroupListPresenter: GroupListPresenter
    private var mGroupListAdapter: GroupListAdapter? = null
    private var mCenterWithAssociations: CenterWithAssociations? = null
    private var centerId = 0
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        mCenterWithAssociations!!.groupMembers[position].id?.let {
            mGroupListPresenter.loadGroups(
                it
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        centerId = arg.centerId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupListBinding.inflate(inflater, container, false)
        mGroupListPresenter.attachView(this)
        setToolbarTitle(resources.getString(R.string.title_center_list))
        binding.lvGroupList.onItemClickListener = this
        inflateGroupList()
        return binding.root
    }

    private fun inflateGroupList() {
        mGroupListPresenter.loadGroupByCenter(centerId)
    }

    override fun showGroupList(centerWithAssociations: CenterWithAssociations?) {
        if (centerWithAssociations != null) {
            if (centerWithAssociations.groupMembers.isEmpty()) {
                showEmptyGroups(R.string.empty_groups)
            } else {
                mCenterWithAssociations = centerWithAssociations
                mGroupListAdapter = GroupListAdapter(
                    requireActivity(),
                    centerWithAssociations.groupMembers
                )
                binding.lvGroupList.adapter = mGroupListAdapter
            }
        }
    }

    override fun showGroups(groupWithAssociations: GroupWithAssociations?) {
        if (groupWithAssociations != null) loadClientsOfGroup(
            groupWithAssociations
                .clientMembers
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        mGroupListPresenter.detachView()
    }

    private fun loadClientsOfGroup(clientList: List<Client>) {
        val action = GroupListFragmentDirections.actionGroupListFragmentToClientListFragment(
            ClientListArgs(clientList, true)
        )
        findNavController().navigate(action)
    }
}