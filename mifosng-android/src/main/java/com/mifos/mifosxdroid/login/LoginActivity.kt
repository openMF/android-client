/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnEditorAction
import com.mifos.api.BaseApiManager
import com.mifos.mifosxdroid.HomeActivity
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.passcode.PassCodeActivity
import com.mifos.objects.user.User
import com.mifos.utils.Constants
import com.mifos.utils.Network
import com.mifos.utils.PrefManager
import com.mifos.utils.ValidationUtil
import javax.inject.Inject

/**
 * Created by ishankhanna on 08/02/14.
 */
class LoginActivity : MifosBaseActivity(), LoginMvpView {
    @BindView(R.id.et_instanceURL)
    lateinit var et_domain: EditText

    @BindView(R.id.et_username)
    lateinit var et_username: EditText

    @BindView(R.id.et_password)
    lateinit var et_password: EditText

    @BindView(R.id.tv_constructed_instance_url)
    lateinit var tv_full_url: TextView

    @BindView(R.id.et_tenantIdentifier)
    lateinit var et_tenantIdentifier: EditText

    @BindView(R.id.et_instancePort)
    lateinit var et_port: EditText

    @BindView(R.id.ll_connectionSettings)
    lateinit var ll_connectionSettings: LinearLayout

    @Inject
    lateinit var mLoginPresenter: LoginPresenter
    private lateinit var username: String
    private lateinit var instanceURL: String
    private lateinit var password: String
    private lateinit var domain: String
    private var isValidUrl = false

    private val urlWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            var port = if (et_port.editableText.toString().isEmpty()) null else Integer
                .valueOf(et_port.editableText.toString())
            instanceURL = ValidationUtil.getInstanceUrl(et_domain.text.toString(), port)
            isValidUrl = ValidationUtil.isValidUrl(instanceURL)
            tv_full_url.text = instanceURL

            domain = et_domain.editableText.toString()

            if (domain.isEmpty() || domain.contains(" ")) {
                isValidUrl = false
            }
            tv_full_url.setTextColor(
                if (isValidUrl) ContextCompat.getColor(
                    applicationContext,
                    R.color.green_light
                ) else ContextCompat.getColor(
                    applicationContext, R.color.red_light
                )
            )
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)
        setContentView(R.layout.activity_login)

        ButterKnife.bind(this)
        mLoginPresenter.attachView(this)

        et_port.inputType = InputType.TYPE_CLASS_NUMBER
        if (PrefManager.getPort() != "80") et_port.setText(PrefManager.getPort())

        et_domain.setText(PrefManager.getInstanceDomain())
        et_domain.addTextChangedListener(urlWatcher)
        et_port.addTextChangedListener(urlWatcher)
        urlWatcher.afterTextChanged(null)
    }

    fun validateUserInputs(): Boolean {
        domain = et_domain.editableText.toString()
        if (domain.isEmpty() || domain.contains(" ")) {
            showToastMessage(getString(R.string.error_invalid_url))
            return false
        }
        if (!isValidUrl) {
            showToastMessage(getString(R.string.error_invalid_connection))
            return false
        }
        username = et_username.editableText.toString()
        password = et_password.editableText.toString()
        if (username.isEmpty() || password.isEmpty()) {
            showToastMessage(getString(R.string.error_enter_credentials))
            return false
        } else {
            if (username.length < 5) {
                showToastMessage(getString(R.string.error_username_length))
                return false
            }
            if (password.length < 6) {
                showToastMessage(getString(R.string.error_password_length))
                return false
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.mItem_connection_settings -> {
                ll_connectionSettings.visibility =
                    if (ll_connectionSettings.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showToastMessage(message: String) {
        Toaster.show(findViewById(android.R.id.content), message, Toaster.INDEFINITE)
    }

    override fun onLoginSuccessful(user: User) {
        // Saving userID
        PrefManager.setUserId(user.userId)
        // Saving user's token
        PrefManager.saveToken("Basic " + user.base64EncodedAuthenticationKey)
        // Saving user
        PrefManager.saveUser(user)
        Toast.makeText(
            this, getString(R.string.toast_welcome) + " " + user.username,
            Toast.LENGTH_SHORT
        ).show()
        if (PrefManager.getPassCodeStatus()) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            val intent = Intent(this, PassCodeActivity::class.java)
            intent.putExtra(Constants.INTIAL_LOGIN, true)
            startActivity(intent)
        }
        finish()
    }

    override fun onLoginError(errorMessage: String) {
        showToastMessage(errorMessage)
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            showProgress(getString(R.string.logging_in))
        } else {
            hideProgress()
        }
    }

    @OnClick(R.id.bt_login)
    fun onLoginClick() {
        //Hide the keyboard, when user clicks on login button
        hideKeyboard(findViewById(R.id.bt_login))
        login()
    }

    private fun login() {
        if (!validateUserInputs()) {
            return
        }
        // Saving tenant
        PrefManager.setTenant(et_tenantIdentifier.editableText.toString())
        // Saving InstanceURL for next usages
        PrefManager.setInstanceUrl(instanceURL)
        // Saving domain name
        PrefManager.setInstanceDomain(et_domain.editableText.toString())
        // Saving port
        PrefManager.setPort(et_port.editableText.toString())
        // Updating Services
        BaseApiManager.createService()
        if (Network.isOnline(this)) {
            mLoginPresenter.login(username, password)
        } else {
            showToastMessage(getString(R.string.error_not_connected_internet))
        }
    }

    @OnEditorAction(R.id.et_password)
    fun passwordSubmitted(keyEvent: KeyEvent?): Boolean {
        if (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN) {
            login()
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mLoginPresenter.detachView()
    }
}