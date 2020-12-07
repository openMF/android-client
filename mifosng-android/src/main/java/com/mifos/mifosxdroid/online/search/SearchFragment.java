/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.search;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SearchAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.mifosxdroid.online.ClientActivity;
import com.mifos.mifosxdroid.online.GroupsActivity;
import com.mifos.objects.SearchedEntity;
import com.mifos.utils.Constants;
import com.mifos.utils.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends MifosBaseFragment implements SearchMvpView,
        RecyclerItemClickListener.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();
    private static final int SPEECH_RECOGNISE_CODE = 1001;

    @BindView(R.id.et_search)
    EditText et_search;

    @BindView(R.id.sp_search)
    Spinner sp_search;

    @BindView(R.id.rv_search)
    RecyclerView rv_search;

    @BindView(R.id.cb_exact_match)
    CheckBox cb_exactMatch;

    @BindArray(R.array.search_options_values)
    String[] searchOptionsValues;

    @Inject
    SearchAdapter searchAdapter;

    @Inject
    SearchPresenter searchPresenter;

    private List<SearchedEntity> searchedEntities;
    private ArrayAdapter<CharSequence> searchOptionsAdapter;
    private String resources;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        searchedEntities = new ArrayList<>();
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

    @Override
    public void showUserInterface() {
        searchOptionsAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.search_options, android.R.layout.simple_spinner_item);
        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_search.setAdapter(searchOptionsAdapter);
        sp_search.setOnItemSelectedListener(this);
        et_search.requestFocus();
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_search.setLayoutManager(layoutManager);
        rv_search.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_search.setHasFixedSize(true);
        rv_search.setAdapter(searchAdapter);
    }


    @OnClick(R.id.btn_search)
    public void onClickSearch() {
        hideKeyboard(et_search);
        String query = et_search.getEditableText().toString().trim();
        if (!query.isEmpty()) {
            EspressoIdlingResource.increment(); // App is busy until further notice.
            searchPresenter.searchResources(query, resources, cb_exactMatch.isChecked());
        } else {
            Toaster.show(et_search, getString(R.string.no_search_query_entered));
        }

    }

    @OnClick(R.id.btn_voice_search)
    void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "SEARCH");
        try {
            startActivityForResult(intent, SPEECH_RECOGNISE_CODE);
        } catch (Exception e) {
            String errorMsg = "Try enabling speech to text in input from setting";
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNISE_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    textProcessing(result.get(0));
                }
        }
    }

    /**
     * this method is used for voice text manipulation and then set it to @et_search
     * @param text
     */
    void textProcessing(String text) {
        text = text.toLowerCase();
        String[] segment = text.split("using");
        for (int i = 0; i < segment.length; i++) {
            segment[i] = segment[i].trim();
        }
        if (text == null || text.equals("") || segment.length == 0) {
            Toast.makeText(getContext(),
                    getString(R.string.nothing_recognised),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (segment.length == 1) {
            et_search.setText(text);
            et_search.setSelection(text.length());
            onClickSearch();
            return;
        }
        switch (segment[1]) {
            case "clients":
            case "client":
                sp_search.setSelection(1);
                break;
            case "groups":
            case "group":
                sp_search.setSelection(2);
                break;
            case "loan accounts":
            case "loan account":
                sp_search.setSelection(3);
                break;
            case "savings account":
            case "saving accounts":
            case "saving account":
                sp_search.setSelection(4);
                break;
            default:
                sp_search.setSelection(0);
                break;
        }
        et_search.setText(segment[0]);
        et_search.setSelection(segment[0].length());
        onClickSearch();
    }

    @Override
    public void showSearchedResources(List<SearchedEntity> searchedEntities) {
        searchAdapter.setSearchResults(searchedEntities);
        this.searchedEntities = searchedEntities;
        EspressoIdlingResource.decrement(); // App is idle.
    }

    @Override
    public void showNoResultFound() {
        showAlertDialog(getString(R.string.dialog_message),
                getString(R.string.no_search_result_found));
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

    @Override
    public void onItemClick(View childView, int position) {
        Intent activity = null;
        switch (searchedEntities.get(position).getEntityType()) {
            case Constants.SEARCH_ENTITY_LOAN:
                activity = new Intent(getActivity(), ClientActivity.class);
                activity.putExtra(Constants.LOAN_ACCOUNT_NUMBER,
                        searchedEntities.get(position).getEntityId());
                break;
            case Constants.SEARCH_ENTITY_CLIENT:
                activity = new Intent(getActivity(), ClientActivity.class);
                activity.putExtra(Constants.CLIENT_ID,
                        searchedEntities.get(position).getEntityId());
                break;
            case Constants.SEARCH_ENTITY_GROUP:
                activity = new Intent(getActivity(), GroupsActivity.class);
                activity.putExtra(Constants.GROUP_ID,
                        searchedEntities.get(position).getEntityId());
                break;
            case Constants.SEARCH_ENTITY_SAVING:
                activity = new Intent(getActivity(), ClientActivity.class);
                activity.putExtra(Constants.SAVINGS_ACCOUNT_NUMBER,
                        searchedEntities.get(position).getEntityId());
                break;
            case Constants.SEARCH_ENTITY_CENTER:
                activity = new Intent(getActivity(), CentersActivity.class);
                activity.putExtra(Constants.CENTER_ID,
                        searchedEntities.get(position).getEntityId());
                break;
        }
        startActivity(activity);
    }

    @Override
    public void onItemLongPress(View childView, int position) {

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