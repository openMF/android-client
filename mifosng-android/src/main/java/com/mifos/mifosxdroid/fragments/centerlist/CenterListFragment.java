/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.fragments.centerlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mifos.mifosxdroid.GroupActivity;
import com.mifos.mifosxdroid.OfflineCenterInputActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CenterAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.objects.db.AttendanceType;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.CollectionMeetingCalendar;
import com.mifos.objects.db.Currency;
import com.mifos.objects.db.EntityType;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.MeetingCenter;
import com.mifos.objects.db.MeetingDate;
import com.mifos.objects.db.MifosGroup;
import com.mifos.objects.db.OfflineCenter;
import com.mifos.objects.db.RepaymentTransaction;
import com.mifos.objects.db.Status;
import com.mifos.utils.DateHelper;
import com.mifos.utils.Network;
import com.mifos.utils.SaveOfflineDataHelper;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.BindView;


public class CenterListFragment extends MifosBaseFragment implements
        AdapterView.OnItemClickListener,
        SaveOfflineDataHelper.OfflineDataSaveListener, CenterListMvpView {

    public static final String CENTER_ID = "offline_center_id";
    public final String TAG = getClass().getSimpleName();
    private final List<MeetingCenter> centerList = new ArrayList<MeetingCenter>();

    @BindView(R.id.lv_center)
    ListView lv_center;

    @BindView(R.id.progress_center)
    ProgressBar progressCenter;

    @BindView(R.id.tv_empty_center)
    TextView tv_empty_center;

    @Inject
    CenterListPresenter mCenterListPresenter;

    CenterAdapter adapter = null;

    View view;

    private String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center_list, null);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        if (getAllCenters().size() == 0)
            getData();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.offline_center_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int itemId = item.getItemId();
        if (itemId == R.id.action_clear_offline_data) {
            deleteAllOfflineCollectionSheetData();
            deleteAllOfflineData();
            startCenterInputActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    private void setAdapter() {
        centerList.clear();
        centerList.addAll(getAllCenters());
        if (adapter == null) {
            adapter = new CenterAdapter(getActivity(), centerList);
            lv_center.setAdapter(adapter);
        }
        lv_center.setOnItemClickListener(this);
        lv_center.setEmptyView(progressCenter);
        adapter.notifyDataSetChanged();

    }

    private List<MeetingCenter> getAllCenters() {
        return Select.from(MeetingCenter.class).list();
    }

    private void getData() {

        if (Network.isOnline(getActivity().getApplicationContext())) {
            String dateFormant = "dd-MMM-yyyy";
            String locale = "en";
            SharedPreferences preferences = getActivity().getSharedPreferences
                    (OfflineCenterInputActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
            int staffId = preferences.getInt(OfflineCenterInputActivity.STAFF_ID_KEY, -1);
            String meetingDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                    (preferences.getString(OfflineCenterInputActivity.TRANSACTION_DATE_KEY, null));
            int branchId = preferences.getInt(OfflineCenterInputActivity.BRANCH_ID_KEY, -1);
            Log.i(TAG, "staffId:" + staffId + ", meetingDate: " + meetingDate + " , branchId:" +
                    branchId);
            //TODO -- Need to ask ---  Hard coding date format and locale

            mCenterListPresenter.loadCenterList(dateFormant, locale, meetingDate,
                    branchId, staffId);
        }
    }

    private void deleteAllOfflineData() {
        try {
            MeetingDate.deleteAll(MeetingDate.class);
            Status.deleteAll(Status.class);
            EntityType.deleteAll(EntityType.class);
            CollectionMeetingCalendar.deleteAll(CollectionMeetingCalendar.class);
            MeetingCenter.deleteAll(MeetingCenter.class);
            OfflineCenter.deleteAll(OfflineCenter.class);
        } catch (Exception ex) {
        }
    }

    private void deleteAllOfflineCollectionSheetData() {
        try {
            RepaymentTransaction.deleteAll(RepaymentTransaction.class);
            MifosGroup.deleteAll(MifosGroup.class);
            Loan.deleteAll(Loan.class);
            Client.deleteAll(Client.class);
            AttendanceType.deleteAll(AttendanceType.class);
            Currency.deleteAll(Currency.class);
        } catch (Exception ex) {
        }
    }

    private void startCenterInputActivity() {
        SharedPreferences preferences = getActivity().getSharedPreferences
                (OfflineCenterInputActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(getActivity(), "Offline data has been cleared.", Toast.LENGTH_LONG).show();
        getActivity().finish();
        startActivity(new Intent(getActivity(), OfflineCenterInputActivity.class));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), GroupActivity.class);
        intent.putExtra(CENTER_ID, centerList.get(i).getCenterId());
        startActivity(intent);
    }

    @Override
    public void dataSaved() {
        centerList.clear();
        setAdapter();
    }

    @Override
    public void showCenterList(List<OfflineCenter> centers) {
        Log.i(TAG, "-----------Success-----Got the list of centers--------");
        for (OfflineCenter center : centers) {
            if (center != null) {
                SaveOfflineDataHelper helper =
                        new SaveOfflineDataHelper(getActivity());
                helper.setOfflineDataSaveListener(CenterListFragment.this);
                helper.saveOfflineCenterData(getActivity(), center);
            }
        }
        if (centers.size() > 0) {
            setAdapter();
        } else {
            startCenterInputActivity();
        }
    }

    @Override
    public void showError(String s) {
        try {
            Toast.makeText(getActivity(), getString(R.string
                    .error_login_again), Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        } finally {
            getActivity().finish();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void showProgressbar(boolean b) {

    }
}
