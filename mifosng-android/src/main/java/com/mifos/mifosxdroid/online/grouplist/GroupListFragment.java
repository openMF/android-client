/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.grouplist;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.BindView;


public class GroupListFragment extends ProgressableFragment
        implements GroupListMvpView, AdapterView.OnItemClickListener {

    @BindView(R.id.lv_group_list)
    ListView lv_groupList;

    @Inject
    GroupListPresenter mGroupListPresenter;

    private GroupListAdapter mGroupListAdapter;

    private CenterWithAssociations mCenterWithAssociations;

    private View rootView;

    private OnFragmentInteractionListener mListener;

    private int centerId;

    public static GroupListFragment newInstance(int centerId) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CENTER_ID, centerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mGroupListPresenter.loadGroups(
                mCenterWithAssociations.getGroupMembers().get(position).getId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            centerId = getArguments().getInt(Constants.CENTER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_group_list, container, false);

        ButterKnife.bind(this, rootView);
        mGroupListPresenter.attachView(this);

        setToolbarTitle(getResources().getString(R.string.title_center_list));
        lv_groupList.setOnItemClickListener(this);

        inflateGroupList();

        return rootView;
    }


    public void inflateGroupList() {
        mGroupListPresenter.loadGroupByCenter(centerId);
    }


    @Override
    public void showGroupList(CenterWithAssociations centerWithAssociations) {
        if (centerWithAssociations != null) {

            mCenterWithAssociations = centerWithAssociations;
            mGroupListAdapter = new GroupListAdapter(getActivity(),
                    centerWithAssociations.getGroupMembers());
            lv_groupList.setAdapter(mGroupListAdapter);

        }
    }

    @Override
    public void showGroups(GroupWithAssociations groupWithAssociations) {
        if (groupWithAssociations != null)
            mListener.loadClientsOfGroup(groupWithAssociations
                    .getClientMembers());
    }

    @Override
    public void showFetchingError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGroupListPresenter.detachView();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void loadClientsOfGroup(List<Client> clientList);
    }
}

