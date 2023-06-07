@file:Suppress("DEPRECATION")

package com.mifos.mifosxdroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.preference.Preference.OnPreferenceChangeListener
import android.widget.Toast
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment
import com.mifos.mifosxdroid.online.DashboardActivity
import com.mifos.mifosxdroid.passcode.PassCodeActivity
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import com.mifos.utils.*

/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    var mEnableSyncSurvey: SwitchPreference? = null
    var mInstanceUrlPref: EditTextPreference? = null
    private lateinit var languages: Array<String>
    private var languageCallback: LanguageCallback? = null
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
        addPreferencesFromResource(R.xml.preferences)
        languages = activity.resources.getStringArray(R.array.language_option)

        initSurveyPreferences()
        initInstanceUrlPreferences()
        initLanguagePreferences()
        initThemePreferences()
    }

    private fun initSurveyPreferences() {
        mEnableSyncSurvey = findPreference(resources.getString(R.string.sync_survey)) as SwitchPreference
        mEnableSyncSurvey!!.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
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
    }

    private fun initInstanceUrlPreferences() {
        mInstanceUrlPref = findPreference(
            getString(R.string.hint_instance_url)
        ) as EditTextPreference
        val instanceUrl = PrefManager.getInstanceUrl()
        mInstanceUrlPref!!.text = instanceUrl
        mInstanceUrlPref!!.isSelectable = true
        mInstanceUrlPref!!.dialogTitle = "Edit Instance Url"
        mInstanceUrlPref!!.setDialogIcon(R.drawable.ic_baseline_edit_24)
        mInstanceUrlPref!!.onPreferenceChangeListener =
            OnPreferenceChangeListener { _, o ->
                val newUrl = o.toString()
                if (newUrl != instanceUrl) {
                    PrefManager.setInstanceUrl(newUrl)
                    Toast.makeText(activity, newUrl, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(activity, DashboardActivity::class.java))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activity.finishAffinity()
                    } else {
                        activity.finish()
                    }
                }
                true
            }
    }

    private fun initLanguagePreferences() {
        val langPref = findPreference("language_type") as ListPreference
        langPref.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
            LanguageHelper.setLocale(this.activity, newValue.toString())
            startActivity(Intent(activity, activity.javaClass))
            activity.finish()
            preferenceScreen = null
            addPreferencesFromResource(R.xml.preferences)
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            true
        }
    }

    private fun initThemePreferences() {
        val themePreference = findPreference(resources.getString(R.string.mode_key)) as ListPreference
        themePreference.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
            val themeOption = newValue as String
            ThemeHelper.applyTheme(themeOption)
            Toast.makeText(activity, "Switched to $themeOption Mode", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    fun setLanguageCallback(languageCallback: LanguageCallback?) {
        this.languageCallback = languageCallback
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        val preference = findPreference(s) as ListPreference
        LanguageHelper.setLocale(this.activity, preference.value)
    }

    interface LanguageCallback

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference?): Boolean {
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
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }
}