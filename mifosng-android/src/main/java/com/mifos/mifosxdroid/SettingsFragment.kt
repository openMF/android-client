package com.mifos.mifosxdroid

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.widget.Toast
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment
import com.mifos.utils.FragmentConstants
import com.mifos.utils.LanguageHelper
import com.mifos.utils.ThemeHelper


/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsFragment : PreferenceFragment(), OnSharedPreferenceChangeListener {
    var mEnableSyncSurvey: SwitchPreference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        languages = activity.resources.getStringArray(R.array.language_option)
        mEnableSyncSurvey = findPreference(resources.getString(R.string.sync_survey)) as SwitchPreference
        mEnableSyncSurvey!!.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
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

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    fun setLanguageCallback(languageCallback: LanguageCallback?) {
        this.languageCallback = languageCallback
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        val preference = findPreference(s)
        if (preference is ListPreference) {
            LanguageHelper.setLocale(this.activity, preference.value)
            Toast.makeText(activity, R.string.lang_changed, Toast.LENGTH_SHORT).show()
            //this.languageCallback.updateNavDrawer();
            startActivity(Intent(activity, activity.javaClass))
            //refresh settings fragment
            preferenceScreen = null
            addPreferencesFromResource(R.xml.preferences)
        }
    }

    interface LanguageCallback {
        fun updateNavDrawer()
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