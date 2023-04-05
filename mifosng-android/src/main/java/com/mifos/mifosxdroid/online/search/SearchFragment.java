/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SearchAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.databinding.FragmentClientSearchBinding;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.mifosxdroid.online.ClientActivity;
import com.mifos.mifosxdroid.online.GroupsActivity;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment;
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment;
import com.mifos.objects.SearchedEntity;
import com.mifos.utils.Constants;
import com.mifos.utils.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class SearchFragment extends MifosBaseFragment
        implements SearchMvpView, AdapterView.OnItemSelectedListener {

    private FragmentClientSearchBinding binding;

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();

    String[] searchOptionsValues;

    SearchAdapter searchAdapter;

    @Inject
    SearchPresenter searchPresenter;

    // determines whether search is triggered by user or system
    boolean autoTriggerSearch = false;

    private List<SearchedEntity> searchedEntities;
    private ArrayAdapter<CharSequence> searchOptionsAdapter;
    private String resources;
    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        searchedEntities = new ArrayList<>();
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        binding = FragmentClientSearchBinding.inflate(inflater);
        setToolbarTitle(getResources().getString(R.string.dashboard));
        searchPresenter.attachView(this);
        searchOptionsValues = getResources().getStringArray(R.array.search_options_values);
        showUserInterface();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSearch.setOnClickListener(view1 -> onClickSearch());
        binding.fabCreate.setOnClickListener(view1 -> onClickCreate());
        binding.fabGroup.setOnClickListener(view1 -> onClickCreateCGroup());
        binding.fabCenter.setOnClickListener(view1 -> onClickCreateCenter());
        binding.fabClient.setOnClickListener(view1 -> onClickCreateClient());

    }

    @Override
    public void showUserInterface() {
        searchOptionsAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.search_options, android.R.layout.simple_spinner_item);
        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spSearch.setAdapter(searchOptionsAdapter);
        binding.spSearch.setOnItemSelectedListener(this);
        binding.etSearch.requestFocus();
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvSearch.setLayoutManager(layoutManager);
        binding.rvSearch.setHasFixedSize(true);
        searchAdapter = new SearchAdapter(searchedEntity -> {
                Intent activity = null;
                switch (searchedEntity.getEntityType()) {
                    case Constants.SEARCH_ENTITY_LOAN:
                        activity = new Intent(getActivity(), ClientActivity.class);
                        activity.putExtra(Constants.LOAN_ACCOUNT_NUMBER,
                                searchedEntity.getEntityId());
                        break;
                    case Constants.SEARCH_ENTITY_CLIENT:
                        activity = new Intent(getActivity(), ClientActivity.class);
                        activity.putExtra(Constants.CLIENT_ID,
                                searchedEntity.getEntityId());
                        break;
                    case Constants.SEARCH_ENTITY_GROUP:
                        activity = new Intent(getActivity(), GroupsActivity.class);
                        activity.putExtra(Constants.GROUP_ID,
                                searchedEntity.getEntityId());
                        break;
                    case Constants.SEARCH_ENTITY_SAVING:
                        activity = new Intent(getActivity(), ClientActivity.class);
                        activity.putExtra(Constants.SAVINGS_ACCOUNT_NUMBER,
                                searchedEntity.getEntityId());
                        break;
                    case Constants.SEARCH_ENTITY_CENTER:
                        activity = new Intent(getActivity(), CentersActivity.class);
                        activity.putExtra(Constants.CENTER_ID,
                                searchedEntity.getEntityId());
                        break;
                }
                startActivity(activity);
                return null;
            }
        );
        binding.rvSearch.setAdapter(searchAdapter);

        binding.cbExactMatch
                .setOnCheckedChangeListener((compoundButton, b) -> onClickSearch());

        showGuide();
    }

    void showGuide() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(250); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "123");

        sequence.setConfig(config);

        String et_search_intro = getString(R.string.et_search_intro);
        int i = 1;
        for (String s: searchOptionsValues) {
            et_search_intro += "\n" + i + '.' + s;
            i++;
        }

        String sp_search_intro = getString(R.string.sp_search_intro);
        String cb_exactMatch_intro = getString(R.string.cb_exactMatch_intro);
        String bt_search_intro = getString(R.string.bt_search_intro);

        sequence.addSequenceItem(binding.etSearch,
                et_search_intro, getString(R.string.got_it));
        sequence.addSequenceItem(binding.spSearch,
                sp_search_intro, getString(R.string.next));
        sequence.addSequenceItem(binding.cbExactMatch,
                cb_exactMatch_intro, getString(R.string.next));
        sequence.addSequenceItem(binding.btnSearch,
                bt_search_intro, getString(R.string.finish));

        sequence.start();
    }


    public void onClickCreateClient() {
        ((MifosBaseActivity) getActivity()).replaceFragment(CreateNewClientFragment.newInstance(),
                true, R.id.container_a);
    }

    public void onClickCreateCenter() {
        ((MifosBaseActivity) getActivity()).replaceFragment(CreateNewCenterFragment.newInstance(),
                true, R.id.container_a);
    }

    public void onClickCreateCGroup() {
        ((MifosBaseActivity) getActivity()).replaceFragment(CreateNewGroupFragment.newInstance(),
                true, R.id.container_a);
    }

    public void onClickSearch() {
        hideKeyboard(binding.etSearch);
        String query = binding.etSearch.getEditableText().toString().trim();
        if (!query.isEmpty()) {
            EspressoIdlingResource.increment(); // App is busy until further notice.
            searchPresenter.searchResources(query, resources, binding.cbExactMatch.isChecked());
        } else {
            if (!autoTriggerSearch) {
                Toaster.show(binding.etSearch, getString(R.string.no_search_query_entered));
            }
        }
    }

    void onClickCreate() {
        if (isFabOpen) {
            binding.fabCreate.startAnimation(rotate_backward);
            binding.fabClient.startAnimation(fab_close);
            binding.fabCenter.startAnimation(fab_close);
            binding.fabGroup.startAnimation(fab_close);
            binding.fabClient.setClickable(false);
            binding.fabCenter.setClickable(false);
            binding.fabGroup.setClickable(false);
            isFabOpen = false;
        } else {
            binding.fabCreate.startAnimation(rotate_forward);
            binding.fabClient.startAnimation(fab_open);
            binding.fabCenter.startAnimation(fab_open);
            binding.fabGroup.startAnimation(fab_open);
            binding.fabClient.setClickable(true);
            binding.fabCenter.setClickable(true);
            binding.fabGroup.setClickable(true);
            isFabOpen = true;
        }
        autoTriggerSearch = false;
    }

    @Override
    public void showSearchedResources(List<SearchedEntity> searchedEntities) {
        searchAdapter.setSearchResults(searchedEntities);
        this.searchedEntities = searchedEntities;
        EspressoIdlingResource.decrement(); // App is idle.
    }

    @Override
    public void showNoResultFound() {
        searchedEntities.clear();
        searchAdapter.notifyDataSetChanged();
        Toaster.show(binding.etSearch, getString(R.string.no_search_result_found));
    }

    @Override
    public void showMessage(int message) {
        Toast.makeText(getActivity(), getString(message), Toast.LENGTH_SHORT).show();
        EspressoIdlingResource.decrement(); // App is idle.
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchPresenter.detachView();
    }

    @Override
    public void onPause() {
        //Fragment getting detached, keyboard if open must be hidden
        hideKeyboard(binding.etSearch);
        super.onPause();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_search) {
            if (position == 0) {
                resources = searchOptionsValues[0] + "," + searchOptionsValues[1] + "," +
                        searchOptionsValues[2] + "," + searchOptionsValues[3];
            } else {
                resources = searchOptionsValues[position - 1];
            }
            autoTriggerSearch = true;
            onClickSearch();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * There is a need for this method in the following cases :
     * <p/>
     * 1. If user entered a search query and went out of the app.
     * 2. If user entered a search query and got some search results and went out of the app.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            String queryString = binding.etSearch.getEditableText().toString();
            if (!queryString.equals("")) {
                outState.putString(LOG_TAG + binding.etSearch.getId(), queryString);
            }
        } catch (NullPointerException npe) {
            //Looks like edit text didn't get initialized properly
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            String queryString = savedInstanceState.getString(LOG_TAG + binding.etSearch.getId());
            if (!TextUtils.isEmpty(queryString)) {
                binding.etSearch.setText(queryString);
            }
        }
    }
}