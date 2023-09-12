/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.core

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.activity.splashscreen.SplashScreenActivity
import com.mifos.mifosxdroid.passcode.PassCodeActivity
import com.mifos.mobile.passcode.BasePassCodeActivity
import com.mifos.utils.Constants
import com.mifos.utils.LanguageHelper
import com.mifos.utils.PrefManager

/**
 * @author fomenkoo
 */
open class MifosBaseActivity : BasePassCodeActivity(), BaseActivityCallback {
    var toolbar: Toolbar? = null
    private var progress: ProgressDialog? = null
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
    }

    private fun setActionBarTitle(title: String?) {
        if (supportActionBar != null && getTitle() != null) {
            setTitle(title)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguageHelper.onAttach(base))
    }

    protected fun showBackButton() {
        if (supportActionBar != null) {
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    protected fun setActionBarTitle(title: Int) {
        setActionBarTitle(resources.getString(title))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showProgress(message: String) {
        if (progress == null) {
            progress = ProgressDialog(this, ProgressDialog.STYLE_SPINNER)
            progress?.setCancelable(false)
        }
        progress?.setMessage(message)
        progress?.show()
    }

    override fun setToolbarTitle(title: String) {
        setActionBarTitle(title)
    }

    override fun setUserStatus(userStatus: SwitchCompat) {
        if (PrefManager.userStatus == Constants.USER_ONLINE) {
            userStatus.isChecked = false
        } else if (PrefManager.userStatus == Constants.USER_OFFLINE) {
            userStatus.isChecked = true
        }
    }

    override fun hideProgress() {
        if (progress != null && progress?.isShowing == true) progress?.dismiss()
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun logout() {
        MaterialDialog.Builder().init(this@MifosBaseActivity)
            .setMessage(R.string.dialog_logout)
            .setPositiveButton(
                getString(R.string.logout)
            ) { dialog, which ->
                PrefManager.clear()
                startActivity(
                    Intent(
                        this@MifosBaseActivity,
                        SplashScreenActivity::class.java
                    )
                )
                Toast.makeText(
                    this@MifosBaseActivity,
                    R.string.logout_successfully,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .setNegativeButton(getString(R.string.cancel))
            .createMaterialDialog()
            .show()
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean, containerId: Int) {
        invalidateOptionsMenu()
        val backStateName = fragment.javaClass.name
        val fragmentPopped = supportFragmentManager.popBackStackImmediate(
            backStateName,
            0
        )
        if (!fragmentPopped && supportFragmentManager.findFragmentByTag(backStateName) ==
            null
        ) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(containerId, fragment, backStateName)
            if (addToBackStack) {
                transaction.addToBackStack(backStateName)
            }
            transaction.commit()
        }
    }

    fun clearFragmentBackStack() {
        val fm = supportFragmentManager
        val backStackCount = supportFragmentManager.backStackEntryCount
        for (i in 0 until backStackCount) {
            val backStackId = supportFragmentManager.getBackStackEntryAt(i).id
            fm.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun getPassCodeClass(): Class<*> {
        return PassCodeActivity::class.java
    }
}