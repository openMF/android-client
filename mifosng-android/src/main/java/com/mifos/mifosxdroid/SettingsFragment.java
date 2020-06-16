package com.mifos.mifosxdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Toast;;

import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment;
import com.mifos.objects.survey.Survey;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;

import java.util.List;

/**
 * Created by mayankjindal on 22/07/17.
 */

public class SettingsFragment extends PreferenceFragment {

    SwitchPreference mEnableSyncSurvey;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mEnableSyncSurvey = (SwitchPreference)
                findPreference(getResources().getString(R.string.sync_survey));
        mEnableSyncSurvey.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (((Boolean) newValue)) {
                            if (isOnline() && PrefManager.getUserStatus()
                                    == Constants.USER_ONLINE) {
                                SyncSurveysDialogFragment syncSurveysDialogFragment =
                                        SyncSurveysDialogFragment.newInstance();
                                FragmentTransaction fragmentTransaction =
                                        getFragmentManager().beginTransaction();
                                fragmentTransaction.addToBackStack(
                                        FragmentConstants.FRAG_SURVEYS_SYNC);
                                syncSurveysDialogFragment.setCancelable(false);
                                syncSurveysDialogFragment.show(fragmentTransaction,
                                        getResources().getString(R.string.sync_clients));
                            } else
                                connectToNetwork();
                        }
                        return true;
                    }
                });
    }

    public void connectToNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        builder.setTitle(R.string.failed_to_sync_survey).
                setMessage(R.string.kindly_connect_to_a_network).setCancelable(false);
        builder.setPositiveButton(R.string.open_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
            }
        })
                .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), R.string.no_internet_connection,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public Boolean isOnline() {
        return Network.isOnline(getActivity());
    }
}
