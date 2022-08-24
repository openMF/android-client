@file:Suppress("DEPRECATION")

package com.mifos.mifosxdroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment
import com.mifos.mifosxdroid.online.DashboardActivity
import com.mifos.mifosxdroid.passcode.PassCodeActivity
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import com.mifos.utils.*

/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    var mEnableSyncSurvey: SwitchPreferenceCompat? = null
    var mInstanceUrlPref: EditTextPreference? = null
    private lateinit var languages: Array<String>
    var preference: Preference? = null

    companion object {
        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpPreferences()

        when (preference?.key) {
            getString(R.string.password) -> {
                //    TODO("create changePasswordActivity and implement the logic for password change")
            }
            getString(R.string.passcode) -> {
                activity?.let {
                    val passCodePreferencesHelper = PasscodePreferencesHelper(activity)
                    val currPassCode = passCodePreferencesHelper.passCode
                    passCodePreferencesHelper.savePassCode("")
                    val intent = Intent(it, PassCodeActivity::class.java).apply {
                        putExtra(Constants.CURR_PASSWORD, currPassCode)
                        putExtra(Constants.IS_TO_UPDATE_PASS_CODE, true)
                    }
                    preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
                    startActivity(intent)
                }
            }
        }
    }

    fun setUpPreferences() {
        languages = requireActivity().resources.getStringArray(R.array.language_option)

        initSurveyPreferences()
        initInstanceUrlPreferences()
        initLanguagePreferences()
        initThemePreferences()
    }

    private fun initSurveyPreferences() {
        mEnableSyncSurvey = findPreference(resources.getString(R.string.sync_survey))
        mEnableSyncSurvey!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    val syncSurveysDialogFragment = SyncSurveysDialogFragment.newInstance()
                    syncSurveysDialogFragment.isCancelable = false
                    val txn = parentFragmentManager.beginTransaction()
                    txn.addToBackStack(FragmentConstants.FRAG_SURVEYS_SYNC)
                    syncSurveysDialogFragment.show(txn,resources.getString(R.string.sync_clients))
                }
                true
            }
    }

    private fun initInstanceUrlPreferences() {
        mInstanceUrlPref = findPreference(getString(R.string.hint_instance_url))
        val instanceUrl = PrefManager.getInstanceUrl()
        mInstanceUrlPref!!.text = instanceUrl
        mInstanceUrlPref!!.isSelectable = true
        mInstanceUrlPref!!.dialogTitle = "Edit Instance Url"
        mInstanceUrlPref!!.setDialogIcon(R.drawable.ic_baseline_edit_24)
        mInstanceUrlPref!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, o ->
                val newUrl = o.toString()
                if (newUrl != instanceUrl) {
                    PrefManager.setInstanceUrl(newUrl)
                    Toast.makeText(activity, newUrl, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(activity, DashboardActivity::class.java))
                    activity?.finishAffinity()
                }
                true
            }
    }

    private fun initLanguagePreferences() {
        findPreference<ListPreference>("language_type")?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            LanguageHelper.setLocale(this.activity, newValue.toString())
            startActivity(Intent(activity, activity?.javaClass))
            preferenceScreen = null
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            true
        }
    }

    private fun initThemePreferences() {
        findPreference<ListPreference>(resources.getString(R.string.mode_key))?.onPreferenceChangeListener =Preference.OnPreferenceChangeListener { _, newValue ->
            val themeOption = newValue as String
            ThemeHelper.applyTheme(themeOption)
            startActivity(Intent(activity, activity?.javaClass))
            Toast.makeText(activity, "Switched to $themeOption Mode", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        val preference: ListPreference? = findPreference(s)
        LanguageHelper.setLocale(this.activity, preference?.value)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            getString(R.string.password) -> {
                //    TODO("create changePasswordActivity and implement the logic for password change")
            }

            getString(R.string.passcode) -> {
                activity?.let {
                    val passCodePreferencesHelper = PasscodePreferencesHelper(activity)
                    val currPassCode = passCodePreferencesHelper.passCode
                    passCodePreferencesHelper.savePassCode("")
                    val intent = Intent(it, PassCodeActivity::class.java).apply {
                        putExtra(Constants.CURR_PASSWORD, currPassCode)
                        putExtra(Constants.IS_TO_UPDATE_PASS_CODE, true)
                    }
                    startActivity(intent)
                }
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen)
    }
}