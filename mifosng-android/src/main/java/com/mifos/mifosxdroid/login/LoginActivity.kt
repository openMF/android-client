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
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mifos.api.BaseApiManager
import com.mifos.mifosxdroid.HomeActivity
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.ActivityLoginBinding
import com.mifos.mifosxdroid.passcode.PassCodeActivity
import com.mifos.objects.user.User
import com.mifos.utils.Constants
import com.mifos.utils.Network
import com.mifos.utils.PrefManager.instanceDomain
import com.mifos.utils.PrefManager.instanceUrl
import com.mifos.utils.PrefManager.passCodeStatus
import com.mifos.utils.PrefManager.port
import com.mifos.utils.PrefManager.saveToken
import com.mifos.utils.PrefManager.saveUser
import com.mifos.utils.PrefManager.tenant
import com.mifos.utils.PrefManager.userId
import com.mifos.utils.ValidationUtil
import javax.inject.Inject

/**
 * Created by ishankhanna on 08/02/14.
 */
class LoginActivity : MifosBaseActivity(), LoginMvpView {

    private lateinit var binding: ActivityLoginBinding


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
        override fun afterTextChanged(editable: Editable) {
            val port = if (binding.etInstancePort.editableText.toString()
                    .isEmpty()
            ) null else Integer.valueOf(binding.etInstancePort.editableText.toString())
            instanceURL = ValidationUtil.getInstanceUrl(binding.etInstanceURL.text.toString(), port)
            isValidUrl = ValidationUtil.isValidUrl(instanceURL)
            binding.tvConstructedInstanceUrl.text = instanceURL
            domain = binding.etInstanceURL.editableText.toString()
            if (domain.isEmpty() || domain.contains(" ")) {
                isValidUrl = false
            }
            binding.tvConstructedInstanceUrl.setTextColor(
                if (isValidUrl) ContextCompat.getColor(
                    applicationContext, R.color.green_light
                ) else ContextCompat.getColor(
                    applicationContext, R.color.red_light
                )
            )
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent?.inject(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        title = null
        setContentView(binding.root)
        mLoginPresenter.attachView(this)
        binding.etInstancePort.inputType = InputType.TYPE_CLASS_NUMBER
        if (port != "80") binding.etInstancePort.setText(port)
        binding.etInstanceURL.setText(instanceDomain)
        binding.etInstanceURL.addTextChangedListener(urlWatcher)
        binding.etInstancePort.addTextChangedListener(urlWatcher)
        urlWatcher.afterTextChanged(Editable.Factory.getInstance().newEditable(""))

        binding.btLogin.setOnClickListener {
            hideKeyboard(binding.btLogin)
            login()
        }

        binding.etPassword.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                login()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun validateUserInputs(): Boolean {
        domain = binding.etInstanceURL.editableText.toString()
        if (domain.isEmpty() || domain.contains(" ")) {
            showToastMessage(getString(R.string.error_invalid_url))
            return false
        }
        if (!isValidUrl) {
            showToastMessage(getString(R.string.error_invalid_connection))
            return false
        }
        username = binding.etUsername.editableText.toString()
        password = binding.etPassword.editableText.toString()
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
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.mItem_connection_settings -> {
                binding.llConnectionSettings.visibility =
                    if (binding.llConnectionSettings.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showToastMessage(message: String) {
        Toaster.show(findViewById(android.R.id.content), message, Toaster.LONG)
    }

    override fun onLoginSuccessful(user: User) {
        // Saving userID
        userId = user.userId
        // Saving user's token
        saveToken("Basic " + user.base64EncodedAuthenticationKey)
        // Saving user
        saveUser(user)
        Toast.makeText(
            this, getString(R.string.toast_welcome) + " " + user.username, Toast.LENGTH_SHORT
        ).show()
        if (passCodeStatus) {
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


    private fun login() {
        if (!validateUserInputs()) {
            return
        }
        // Saving tenant
        tenant = binding.etTenantIdentifier.editableText.toString()
        // Saving InstanceURL for next usages
        instanceUrl = instanceURL
        // Saving domain name
        instanceDomain = binding.etInstanceURL.editableText.toString()
        // Saving port
        port = binding.etInstancePort.editableText.toString()
        // Updating Services
        BaseApiManager.createService()
        if (Network.isOnline(this)) {
            mLoginPresenter.login(username, password)
        } else {
            showToastMessage(getString(R.string.error_not_connected_internet))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLoginPresenter.detachView()
    }
}