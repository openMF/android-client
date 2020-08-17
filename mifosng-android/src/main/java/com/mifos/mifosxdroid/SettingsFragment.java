package com.mifos.mifosxdroid;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;;

import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment;
import com.mifos.utils.FragmentConstants;

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
                        SyncSurveysDialogFragment syncSurveysDialogFragment =
                                SyncSurveysDialogFragment.newInstance();
                        FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SURVEYS_SYNC);
                        syncSurveysDialogFragment.setCancelable(false);
                        syncSurveysDialogFragment.show(fragmentTransaction,
                                getResources().getString(R.string.sync_clients));
                    }
                    return true;
                }
            });
    }
}