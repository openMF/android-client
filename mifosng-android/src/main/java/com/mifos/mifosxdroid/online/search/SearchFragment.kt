/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.search

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.SearchedEntity
import com.mifos.core.objects.navigation.ClientArgs
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.activity.home.HomeActivity
import com.mifos.mifosxdroid.adapters.SearchAdapter
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentClientSearchBinding
import com.mifos.utils.Network
import dagger.hilt.android.AndroidEntryPoint
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig


@AndroidEntryPoint
class SearchFragment : MifosBaseFragment() {

    private lateinit var binding: FragmentClientSearchBinding

    private lateinit var viewModel: SearchViewModel

    private lateinit var searchOptionsValues: Array<String>
    private lateinit var searchAdapter: SearchAdapter

    // determines weather search is triggered by user or system
    private var autoTriggerSearch = false
    private lateinit var searchedEntities: MutableList<SearchedEntity>
    private lateinit var searchOptionsAdapter: ArrayAdapter<CharSequence>
    private var resources: String? = null
    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation
    private lateinit var layoutManager: LinearLayoutManager
    private var checkedFilter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchedEntities = ArrayList()
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        rotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientSearchBinding.inflate(inflater, container, false)
        (activity as HomeActivity).supportActionBar?.title = getString(R.string.dashboard)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchOptionsValues =
            requireActivity().resources.getStringArray(R.array.search_options_values)
        showUserInterface()


        viewModel.searchUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SearchUiState.ShowProgress -> showProgressbar(it.state)
                is SearchUiState.ShowSearchedResources -> {
                    showProgressbar(false)
                    showSearchedResources(it.searchedEntities)
                }

                is SearchUiState.ShowError -> {
                    showProgressbar(false)
                    showMessage(it.message)
                }

                is SearchUiState.ShowNoResultFound -> {
                    showProgressbar(false)
                    showNoResultFound()
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabClient.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_createNewClientFragment)
        }

        binding.fabCenter.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_createNewCenterFragment)
        }

        binding.fabGroup.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_createNewGroupFragment)
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onClickSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }


        binding.btnSearch.setOnClickListener {
            onClickSearch()
        }

        binding.fabCreate.setOnClickListener {
            if (isFabOpen) {
                binding.fabCreate.startAnimation(rotateBackward)
                binding.fabClient.startAnimation(fabClose)
                binding.fabCenter.startAnimation(fabClose)
                binding.fabGroup.startAnimation(fabClose)
                binding.fabClient.isClickable = false
                binding.fabCenter.isClickable = false
                binding.fabGroup.isClickable = false
                isFabOpen = false
            } else {
                binding.fabCreate.startAnimation(rotateForward)
                binding.fabClient.startAnimation(fabOpen)
                binding.fabCenter.startAnimation(fabOpen)
                binding.fabGroup.startAnimation(fabOpen)
                binding.fabClient.isClickable = true
                binding.fabCenter.isClickable = true
                binding.fabGroup.isClickable = true
                isFabOpen = true
            }
            autoTriggerSearch = false
        }
    }

    private fun showFilterDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setSingleChoiceItems(
            R.array.search_options,
            checkedFilter
        ) { dialog, index ->
            checkedFilter = index
            resources = if (checkedFilter == 0) null else searchOptionsValues[checkedFilter - 1]
            autoTriggerSearch = true
            onClickSearch()
            binding.filterSelectionButton.text =
                getResources().getStringArray(R.array.search_options)[index]
            dialog.dismiss()
        }
        dialogBuilder.show()
    }

    private fun showUserInterface() {
        searchOptionsAdapter = ArrayAdapter.createFromResource(
            (requireActivity()),
            R.array.search_options, android.R.layout.simple_spinner_item
        )
        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSelectionButton.setOnClickListener { showFilterDialog() }
        binding.filterSelectionButton.text =
            getResources().getStringArray(R.array.search_options)[0]
        binding.etSearch.requestFocus()
        layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSearch.layoutManager = layoutManager
        binding.rvSearch.setHasFixedSize(true)
        searchAdapter = SearchAdapter { searchedEntity: SearchedEntity ->
            when (searchedEntity.entityType) {
                Constants.SEARCH_ENTITY_LOAN -> {
                    val action = SearchFragmentDirections.actionNavigationDashboardToClientActivity(
                        ClientArgs(clientId = searchedEntity.entityId)
                    )
                    findNavController().navigate(action)
                }

                Constants.SEARCH_ENTITY_CLIENT -> {
                    val action = SearchFragmentDirections.actionNavigationDashboardToClientActivity(
                        ClientArgs(clientId = searchedEntity.entityId)
                    )
                    findNavController().navigate(action)
                }

                Constants.SEARCH_ENTITY_GROUP -> {
                    val action = searchedEntity.entityName?.let {
                        SearchFragmentDirections.actionNavigationDashboardToGroupsActivity(
                            searchedEntity.entityId,
                            it
                        )
                    }
                    action?.let { findNavController().navigate(it) }
                }

                Constants.SEARCH_ENTITY_SAVING -> {
                    val action = SearchFragmentDirections.actionNavigationDashboardToClientActivity(
                        ClientArgs(savingsAccountNumber = searchedEntity.entityId)
                    )
                    findNavController().navigate(action)
                }

                Constants.SEARCH_ENTITY_CENTER -> {
                    val action =
                        SearchFragmentDirections.actionNavigationDashboardToCentersActivity(
                            searchedEntity.entityId
                        )
                    findNavController().navigate(action)
                }
            }
        }
        binding.rvSearch.adapter = searchAdapter
        binding.cbExactMatch.setOnCheckedChangeListener { _, _ -> onClickSearch() }
        showGuide()
    }

    private fun showGuide() {
        val config = ShowcaseConfig()
        config.delay = 250 // half second between each showcase view
        val sequence = MaterialShowcaseSequence(activity, "123")
        sequence.setConfig(config)
        var etSearchIntro: String = getString(R.string.et_search_intro)
        var i = 1
        for (s: String in searchOptionsValues) {
            etSearchIntro += "\n$i.$s"
            i++
        }
        val spSearchIntro = getString(R.string.sp_search_intro)
        val cbExactMatchIntro = getString(R.string.cb_exactMatch_intro)
        val btSearchIntro = getString(R.string.bt_search_intro)
        sequence.addSequenceItem(
            binding.etSearch,
            etSearchIntro, getString(R.string.got_it)
        )
        sequence.addSequenceItem(
            binding.filterSelectionButton,
            spSearchIntro, getString(R.string.next)
        )
        sequence.addSequenceItem(
            binding.cbExactMatch,
            cbExactMatchIntro, getString(R.string.next)
        )
        sequence.addSequenceItem(
            binding.btnSearch,
            btSearchIntro, getString(R.string.finish)
        )
        sequence.start()
    }

    private fun showSearchedResources(searchedEntities: List<SearchedEntity>) {
        searchAdapter.setSearchResults(searchedEntities)
        this.searchedEntities = searchedEntities.toMutableList()
    }

    private fun showNoResultFound() {
        searchedEntities.clear()
        searchAdapter.notifyDataSetChanged()
        show(binding.etSearch, getString(R.string.no_search_result_found))
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onPause() {
        //Fragment getting detached, keyboard if open must be hidden
        hideKeyboard(binding.etSearch)
        super.onPause()
    }

    /**
     * There is a need for this method in the following cases :
     *
     *
     * 1. If user entered a search query and went out of the app.
     * 2. If user entered a search query and got some search results and went out of the app.
     *
     * @param outState
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        try {
            val queryString = binding.etSearch.editableText.toString()
            if (queryString != "") {
                outState.putString(LOG_TAG + binding.etSearch.id, queryString)
            }
        } catch (npe: NullPointerException) {
            //Looks like edit text didn't get initialized properly
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val queryString = savedInstanceState.getString(LOG_TAG + binding.etSearch.id)
            if (!TextUtils.isEmpty(queryString)) {
                binding.etSearch.setText(queryString)
            }
        }
    }

    private fun onClickSearch() {
        hideKeyboard(binding.etSearch)
        if (!Network.isOnline(requireContext())) {
            showMessage(getStringMessage(com.github.therajanmaurya.sweeterror.R.string.no_internet_connection))
            return
        }
        val query = binding.etSearch.editableText.toString().trim { it <= ' ' }
        if (query.isNotEmpty()) {
            viewModel.searchResources(query, resources, binding.cbExactMatch.isChecked)
        } else {
            if (!autoTriggerSearch) {
                show(binding.etSearch, getString(R.string.no_search_query_entered))
            }
        }
    }

    companion object {
        private val LOG_TAG = SearchFragment::class.java.simpleName
    }
}