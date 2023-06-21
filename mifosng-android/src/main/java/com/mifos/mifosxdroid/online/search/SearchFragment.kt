/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.search

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindArray
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnEditorAction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SearchAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.online.CentersActivity
import com.mifos.mifosxdroid.online.ClientActivity
import com.mifos.mifosxdroid.online.GroupsActivity
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment
import com.mifos.objects.SearchedEntity
import com.mifos.utils.Constants
import com.mifos.utils.EspressoIdlingResource
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import javax.inject.Inject

class SearchFragment() : MifosBaseFragment(), SearchMvpView, OnItemSelectedListener {
    @JvmField
    @BindView(R.id.btn_search)
    var bt_search: Button? = null

    @JvmField
    @BindView(R.id.et_search)
    var et_search: EditText? = null

    @JvmField
    @BindView(R.id.sp_search)
    var sp_search: Spinner? = null

    @JvmField
    @BindView(R.id.rv_search)
    var rv_search: RecyclerView? = null

    @JvmField
    @BindView(R.id.cb_exact_match)
    var cb_exactMatch: CheckBox? = null

    @JvmField
    @BindView(R.id.fab_create)
    var fabCreate: FloatingActionButton? = null

    @JvmField
    @BindView(R.id.fab_client)
    var fabClient: FloatingActionButton? = null

    @JvmField
    @BindView(R.id.fab_center)
    var fabCenter: FloatingActionButton? = null

    @JvmField
    @BindView(R.id.fab_group)
    var fabGroup: FloatingActionButton? = null


    @BindArray(R.array.search_options_values)
    lateinit var searchOptionsValues: Array<String>
    var searchAdapter: SearchAdapter? = null

    @JvmField
    @Inject
    var searchPresenter: SearchPresenter? = null

    // determines weather search is triggered by user or system
    var autoTriggerSearch = false
    private var searchedEntities: MutableList<SearchedEntity>? = null
    private var searchOptionsAdapter: ArrayAdapter<CharSequence>? = null
    private var resources: String? = null
    private var isFabOpen = false
    private var fab_open: Animation? = null
    private var fab_close: Animation? = null
    private var rotate_forward: Animation? = null
    private var rotate_backward: Animation? = null
    private var layoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        searchedEntities = ArrayList()
        fab_open = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        rotate_forward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        rotate_backward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_client_search, null)
        setToolbarTitle(getResources().getString(R.string.dashboard))
        ButterKnife.bind(this, rootView)
        searchPresenter!!.attachView(this)
        showUserInterface()
        return rootView
    }

    override fun showUserInterface() {
        searchOptionsAdapter = ArrayAdapter.createFromResource(
            (activity)!!,
            R.array.search_options, android.R.layout.simple_spinner_item
        )
        searchOptionsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_search!!.adapter = searchOptionsAdapter
        sp_search!!.onItemSelectedListener = this
        et_search!!.requestFocus()
        layoutManager = LinearLayoutManager(activity)
        layoutManager!!.orientation = LinearLayoutManager.VERTICAL
        rv_search!!.layoutManager = layoutManager
        rv_search!!.setHasFixedSize(true)
        searchAdapter = SearchAdapter { searchedEntity: SearchedEntity ->
            var activity: Intent? = null
            when (searchedEntity.getEntityType()) {
                Constants.SEARCH_ENTITY_LOAN -> {
                    activity = Intent(getActivity(), ClientActivity::class.java)
                    activity.putExtra(
                        Constants.LOAN_ACCOUNT_NUMBER,
                        searchedEntity.getEntityId()
                    )
                }

                Constants.SEARCH_ENTITY_CLIENT -> {
                    activity = Intent(getActivity(), ClientActivity::class.java)
                    activity.putExtra(
                        Constants.CLIENT_ID,
                        searchedEntity.getEntityId()
                    )
                }

                Constants.SEARCH_ENTITY_GROUP -> {
                    activity = Intent(getActivity(), GroupsActivity::class.java)
                    activity.putExtra(
                        Constants.GROUP_ID,
                        searchedEntity.getEntityId()
                    )
                }

                Constants.SEARCH_ENTITY_SAVING -> {
                    activity = Intent(getActivity(), ClientActivity::class.java)
                    activity.putExtra(
                        Constants.SAVINGS_ACCOUNT_NUMBER,
                        searchedEntity.getEntityId()
                    )
                }

                Constants.SEARCH_ENTITY_CENTER -> {
                    activity = Intent(getActivity(), CentersActivity::class.java)
                    activity.putExtra(
                        Constants.CENTER_ID,
                        searchedEntity.getEntityId()
                    )
                }
            }
            startActivity(activity)
            null
        }
        rv_search!!.adapter = searchAdapter
        cb_exactMatch!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b -> onClickSearch() })
        showGuide()
    }

    fun showGuide() {
        val config = ShowcaseConfig()
        config.delay = 250 // half second between each showcase view
        val sequence = MaterialShowcaseSequence(activity, "123")
        sequence.setConfig(config)
        var et_search_intro: String = getString(R.string.et_search_intro)
        var i = 1
        for (s: String in searchOptionsValues) {
            et_search_intro += "\n$i.$s"
            i++
        }
        val sp_search_intro = getString(R.string.sp_search_intro)
        val cb_exactMatch_intro = getString(R.string.cb_exactMatch_intro)
        val bt_search_intro = getString(R.string.bt_search_intro)
        sequence.addSequenceItem(
            et_search,
            et_search_intro, getString(R.string.got_it)
        )
        sequence.addSequenceItem(
            sp_search,
            sp_search_intro, getString(R.string.next)
        )
        sequence.addSequenceItem(
            cb_exactMatch,
            cb_exactMatch_intro, getString(R.string.next)
        )
        sequence.addSequenceItem(
            bt_search,
            bt_search_intro, getString(R.string.finish)
        )
        sequence.start()
    }

    @OnClick(R.id.fab_client)
    fun onClickCreateClient() {
        (activity as MifosBaseActivity?)!!.replaceFragment(
            CreateNewClientFragment.newInstance(),
            true, R.id.container_a
        )
    }

    @OnClick(R.id.fab_center)
    fun onClickCreateCenter() {
        (activity as MifosBaseActivity?)!!.replaceFragment(
            CreateNewCenterFragment.newInstance(),
            true, R.id.container_a
        )
    }

    @OnClick(R.id.fab_group)
    fun onClickCreateCGroup() {
        (activity as MifosBaseActivity?)!!.replaceFragment(
            CreateNewGroupFragment.newInstance(),
            true, R.id.container_a
        )
    }

    @OnEditorAction(R.id.et_search)
    fun onEditorAction(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onClickSearch()
            return true
        }
        return false
    }

    @OnClick(R.id.btn_search)
    fun onClickSearch() {
        hideKeyboard(et_search)
        val query = et_search!!.editableText.toString().trim { it <= ' ' }
        if (!query.isEmpty()) {
            EspressoIdlingResource.increment() // App is busy until further notice.
            searchPresenter!!.searchResources(query, resources, cb_exactMatch!!.isChecked)
        } else {
            if (!autoTriggerSearch) {
                show(et_search, getString(R.string.no_search_query_entered))
            }
        }
    }

    @OnClick(R.id.fab_create)
    fun onClickCreate() {
        if (isFabOpen) {
            fabCreate!!.startAnimation(rotate_backward)
            fabClient!!.startAnimation(fab_close)
            fabCenter!!.startAnimation(fab_close)
            fabGroup!!.startAnimation(fab_close)
            fabClient!!.isClickable = false
            fabCenter!!.isClickable = false
            fabGroup!!.isClickable = false
            isFabOpen = false
        } else {
            fabCreate!!.startAnimation(rotate_forward)
            fabClient!!.startAnimation(fab_open)
            fabCenter!!.startAnimation(fab_open)
            fabGroup!!.startAnimation(fab_open)
            fabClient!!.isClickable = true
            fabCenter!!.isClickable = true
            fabGroup!!.isClickable = true
            isFabOpen = true
        }
        autoTriggerSearch = false
    }

    override fun showSearchedResources(searchedEntities: MutableList<SearchedEntity>) {
        searchAdapter!!.setSearchResults(searchedEntities)
        this.searchedEntities = searchedEntities
        EspressoIdlingResource.decrement() // App is idle.
    }

    override fun showNoResultFound() {
        searchedEntities!!.clear()
        searchAdapter!!.notifyDataSetChanged()
        show(et_search, getString(R.string.no_search_result_found))
    }

    override fun showMessage(message: Int) {
        Toast.makeText(activity, getString(message), Toast.LENGTH_SHORT).show()
        EspressoIdlingResource.decrement() // App is idle.
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchPresenter!!.detachView()
    }

    override fun onPause() {
        //Fragment getting detached, keyboard if open must be hidden
        hideKeyboard(et_search)
        super.onPause()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (parent.id == R.id.sp_search) {
            if (position == 0) {
                resources = (searchOptionsValues[0] + "," + searchOptionsValues[1] + "," +
                        searchOptionsValues[2] + "," + searchOptionsValues[3] + "," +
                        searchOptionsValues[4])
            } else {
                resources = searchOptionsValues[position - 1]
            }
            autoTriggerSearch = true
            onClickSearch()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

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
            val queryString = et_search!!.editableText.toString()
            if (queryString != "") {
                outState.putString(LOG_TAG + et_search!!.id, queryString)
            }
        } catch (npe: NullPointerException) {
            //Looks like edit text didn't get initialized properly
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val queryString = savedInstanceState.getString(LOG_TAG + et_search!!.id)
            if (!TextUtils.isEmpty(queryString)) {
                et_search!!.setText(queryString)
            }
        }
    }

    companion object {
        private val LOG_TAG = SearchFragment::class.java.simpleName
    }
}