/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.grouplistfragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.App;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.utils.Constants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class GroupListFragment extends MifosBaseFragment implements GroupListMvpView{

    @InjectView(R.id.lv_group_list)
    ListView lv_groupList;

    private View rootView;
    private OnFragmentInteractionListener mListener;

    private int centerId;
    private DataManager dataManager;
    private GroupListPresenter mGroupListPresenter;

    public static GroupListFragment newInstance(int centerId) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CENTER_ID, centerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            centerId = getArguments().getInt(Constants.CENTER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_group_list, container, false);
        ButterKnife.inject(this, rootView);
        dataManager = new DataManager();
        mGroupListPresenter = new GroupListPresenter(dataManager);
        mGroupListPresenter.attachView(this);
        setToolbarTitle(getResources().getString(R.string.group));
        inflateGroupList();
        return rootView;
    }

    public void inflateGroupList() {
        showProgress();
        mGroupListPresenter.loadGroupByCenter(centerId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void showGroupList(final CenterWithAssociations centerWithAssociations) {
        if (centerWithAssociations != null) {

            GroupListAdapter groupListAdapter = new GroupListAdapter(getActivity(), centerWithAssociations.getGroupMembers());
            lv_groupList.setAdapter(groupListAdapter);
            lv_groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int groupId = centerWithAssociations.getGroupMembers().get(i).getId();
                    App.apiManager.getGroups(groupId, new Callback<GroupWithAssociations>() {
                        @Override
                        public void success(GroupWithAssociations groupWithAssociations, Response response) {
                            if (groupWithAssociations != null)
                                mListener.loadClientsOfGroup(groupWithAssociations.getClientMembers());
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {

                        }
                    });
                }
            });
        }
        hideProgress();
    }

    @Override
    public void ResponseError(String s) {
        hideProgress();
        Toaster.show(rootView, s);
    }

    public interface OnFragmentInteractionListener {

        void loadClientsOfGroup(List<Client> clientList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGroupListPresenter.detachView();
    }

}

