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
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.GroupListAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.objects.client.Client
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.utils.Constants
import javax.inject.Inject

class GroupListFragment : ProgressableFragment(), GroupListMvpView, AdapterView.OnItemClickListener {
    @JvmField
    @BindView(R.id.lv_group_list)
    var lv_groupList: ListView? = null

    @JvmField
    @BindView(R.id.ll_error)
    var ll_error: LinearLayout? = null

    @JvmField
    @BindView(R.id.view_flipper)
    var viewFlipper: ViewFlipper? = null

    @JvmField
    @BindView(R.id.noGroupsText)
    var noGroupsText: TextView? = null

    @JvmField
    @Inject
    var mGroupListPresenter: GroupListPresenter? = null
    private var mGroupListAdapter: GroupListAdapter? = null
    private var mCenterWithAssociations: CenterWithAssociations? = null
    private lateinit  var rootView: View
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
        rootView = inflater.inflate(R.layout.fragment_group_list, container, false)
        ButterKnife.bind(this, rootView)
        mGroupListPresenter!!.attachView(this)
        setToolbarTitle(resources.getString(R.string.title_center_list))
        lv_groupList!!.onItemClickListener = this
        inflateGroupList()
        return rootView
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
                lv_groupList!!.adapter = mGroupListAdapter
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
        viewFlipper!!.visibility = View.GONE
        ll_error!!.visibility = View.VISIBLE
        noGroupsText!!.text = getStringMessage(message)
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