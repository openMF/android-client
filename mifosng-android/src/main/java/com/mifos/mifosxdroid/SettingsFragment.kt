package com.mifos.mifosxdroid

import android.content.Intent
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment
import com.mifos.utils.FragmentConstants
import com.mifos.utils.ThemeHelper


/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsFragment : PreferenceFragment() {
    var mEnableSyncSurvey: SwitchPreference? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        mEnableSyncSurvey = findPreference(resources.getString(R.string.sync_survey)) as SwitchPreference
        mEnableSyncSurvey!!.onPreferenceChangeListener =
                OnPreferenceChangeListener { preference, newValue ->
            if (newValue as Boolean) {
                val syncSurveysDialogFragment = SyncSurveysDialogFragment.newInstance()
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SURVEYS_SYNC)
                syncSurveysDialogFragment.isCancelable = false
                syncSurveysDialogFragment.show(fragmentTransaction,
                        resources.getString(R.string.sync_clients))
            }
            true
        }
        val themePreference = findPreference("dark_mode") as ListPreference
        themePreference.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            val themeOption = newValue as String
            ThemeHelper.applyTheme(themeOption)
            startActivity(Intent(activity, activity.javaClass))
            true
        }
    }

    companion object {
        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}