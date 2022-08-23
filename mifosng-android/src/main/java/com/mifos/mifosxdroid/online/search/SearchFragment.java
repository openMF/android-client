/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.search;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SearchAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
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
import java.util.StringJoiner;

import javax.inject.Inject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class SearchFragment extends MifosBaseFragment implements SearchMvpView {

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();

    @BindView(R.id.btn_search)
    Button bt_search;

    @BindView(R.id.et_search)
    EditText et_search;

    @BindView(R.id.filterSelectionButton)
    Button filterSelectionButton;

    @BindView(R.id.rv_search)
    RecyclerView rv_search;

    @BindView(R.id.cb_exact_match)
    CheckBox cb_exactMatch;

    @BindView(R.id.fab_create)
    FloatingActionButton fabCreate;

    @BindView(R.id.fab_client)
    FloatingActionButton fabClient;

    @BindView(R.id.fab_center)
    FloatingActionButton fabCenter;

    @BindView(R.id.fab_group)
    FloatingActionButton fabGroup;

    @BindArray(R.array.search_options_values)
    String[] searchOptionsValues;

    SearchAdapter searchAdapter;

    @Inject
    SearchPresenter searchPresenter;

    // determines weather search is triggered by user or system
    boolean autoTriggerSearch = false;

    private List<SearchedEntity> searchedEntities;
    private ArrayAdapter<CharSequence> searchOptionsAdapter;
    private String resources;
    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

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
        View rootView = inflater.inflate(R.layout.fragment_client_search, null);
        setToolbarTitle(getResources().getString(R.string.dashboard));
        ButterKnife.bind(this, rootView);
        searchPresenter.attachView(this);
        showUserInterface();
        return rootView;
    }
    int checkedFilter = 0;
    private void showFilterDialog(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        dialogBuilder.setSingleChoiceItems(
            R.array.search_options,
            checkedFilter,
            (dialog, index) -> {
                checkedFilter = index;
                resources = checkedFilter == 0 ? String.join(",", searchOptionsValues) : searchOptionsValues[checkedFilter-1];
                autoTriggerSearch = true;
                onClickSearch();
                filterSelectionButton.setText(getResources().getStringArray(R.array.search_options)[index]);
                dialog.dismiss();
            }
        );
        dialogBuilder.show();
    }

    @Override
    public void showUserInterface() {
        searchOptionsAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.search_options, android.R.layout.simple_spinner_item);
        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSelectionButton.setOnClickListener(v -> showFilterDialog());
        filterSelectionButton.setText(getResources().getStringArray(R.array.search_options)[0]);
        et_search.requestFocus();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_search.setLayoutManager(layoutManager);
        rv_search.setHasFixedSize(true);
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
        rv_search.setAdapter(searchAdapter);

        cb_exactMatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onClickSearch();
            }
        });

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

        sequence.addSequenceItem(et_search,
                et_search_intro, getString(R.string.got_it));
        sequence.addSequenceItem(filterSelectionButton,
                sp_search_intro, getString(R.string.next));
        sequence.addSequenceItem(cb_exactMatch,
                cb_exactMatch_intro, getString(R.string.next));
        sequence.addSequenceItem(bt_search,
                bt_search_intro, getString(R.string.finish));

        sequence.start();
    }


    @OnClick(R.id.fab_client)
    public void onClickCreateClient() {
        ((MifosBaseActivity) getActivity()).replaceFragment(CreateNewClientFragment.newInstance(),
                true, R.id.container_a);
    }

    @OnClick(R.id.fab_center)
    public void onClickCreateCenter() {
        ((MifosBaseActivity) getActivity()).replaceFragment(CreateNewCenterFragment.newInstance(),
                true, R.id.container_a);
    }

    @OnClick(R.id.fab_group)
    public void onClickCreateCGroup() {
        ((MifosBaseActivity) getActivity()).replaceFragment(CreateNewGroupFragment.newInstance(),
                true, R.id.container_a);
    }

    @OnClick(R.id.btn_search)
    public void onClickSearch() {
        hideKeyboard(et_search);
        String query = et_search.getEditableText().toString().trim();
        if (!query.isEmpty()) {
            EspressoIdlingResource.increment(); // App is busy until further notice.
            searchPresenter.searchResources(query, resources, cb_exactMatch.isChecked());
        } else {
            if (!autoTriggerSearch) {
                Toaster.show(et_search, getString(R.string.no_search_query_entered));
            }
        }
    }

    @OnClick(R.id.fab_create)
    void onClickCreate() {
        if (isFabOpen) {
            fabCreate.startAnimation(rotate_backward);
            fabClient.startAnimation(fab_close);
            fabCenter.startAnimation(fab_close);
            fabGroup.startAnimation(fab_close);
            fabClient.setClickable(false);
            fabCenter.setClickable(false);
            fabGroup.setClickable(false);
            isFabOpen = false;
        } else {
            fabCreate.startAnimation(rotate_forward);
            fabClient.startAnimation(fab_open);
            fabCenter.startAnimation(fab_open);
            fabGroup.startAnimation(fab_open);
            fabClient.setClickable(true);
            fabCenter.setClickable(true);
            fabGroup.setClickable(true);
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
        Toaster.show(et_search, getString(R.string.no_search_result_found));
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
        hideKeyboard(et_search);
        super.onPause();
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
            String queryString = et_search.getEditableText().toString();
            if (!queryString.equals("")) {
                outState.putString(LOG_TAG + et_search.getId(), queryString);
            }
        } catch (NullPointerException npe) {
            //Looks like edit text didn't get initialized properly
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            String queryString = savedInstanceState.getString(LOG_TAG + et_search.getId());
            if (!TextUtils.isEmpty(queryString)) {
                et_search.setText(queryString);
            }
        }
    }
}