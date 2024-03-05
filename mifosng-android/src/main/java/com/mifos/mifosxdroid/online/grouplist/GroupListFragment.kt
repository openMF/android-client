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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.navigation.ClientListArgs
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.GroupListAdapter
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.databinding.FragmentGroupListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupListFragment : ProgressableFragment(),
    AdapterView.OnItemClickListener {

    private lateinit var binding: FragmentGroupListBinding
    private val arg: GroupListFragmentArgs by navArgs()

    private lateinit var viewModel: GroupListViewModel

    private var mGroupListAdapter: GroupListAdapter? = null
    private var mCenterWithAssociations: CenterWithAssociations? = null
    private var centerId = 0
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        mCenterWithAssociations!!.groupMembers[position].id?.let {
            viewModel.loadGroups(
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
        viewModel = ViewModelProvider(this)[GroupListViewModel::class.java]
        setToolbarTitle(resources.getString(R.string.title_center_list))
        binding.lvGroupList.onItemClickListener = this
        inflateGroupList()

        viewModel.groupListUiState.observe(viewLifecycleOwner) {
            when (it) {
                is GroupListUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is GroupListUiState.ShowGroups -> {
                    showProgressbar(false)
                    showGroups(it.groupWithAssociations)
                }

                is GroupListUiState.ShowProgress -> showProgressbar(it.state)
                is GroupListUiState.ShowGroupList -> {
                    showProgressbar(false)
                    showGroupList(it.centerWithAssociations)
                }
            }
        }

        return binding.root
    }

    private fun inflateGroupList() {
        viewModel.loadGroupByCenter(centerId)
    }

    private fun showGroupList(centerWithAssociations: CenterWithAssociations?) {
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

    private fun showGroups(groupWithAssociations: GroupWithAssociations?) {
        if (groupWithAssociations != null) loadClientsOfGroup(
            groupWithAssociations
                .clientMembers
        )
    }

    private fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    private fun showEmptyGroups(message: Int) {
        binding.viewFlipper.visibility = View.GONE
        binding.llError.visibility = View.VISIBLE
        binding.noGroupsText.text = getStringMessage(message)
    }

    private fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    private fun loadClientsOfGroup(clientList: List<Client>) {
        val action = GroupListFragmentDirections.actionGroupListFragmentToClientListFragment(
            ClientListArgs(clientList, true)
        )
        findNavController().navigate(action)
    }
}