/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.core

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.mifos.mifosxdroid.R
import com.mifos.utils.LanguageHelper
import com.mifos.utils.Network

/**
 * @author fomenkoo
 */
open class MifosBaseFragment() : Fragment() {
    private var callback: BaseActivityCallback? = null
    private var activity: Activity? = null
    private var inputManager: InputMethodManager? = null
    private var mMifosProgressBarHandler: MifosProgressBarHandler? = null
    override fun onAttach(context: Context) {
        super.onAttach(LanguageHelper.onAttach(context))
        val activity = if (context is Activity) context else null
        this.activity = activity
        callback = try {
            activity as BaseActivityCallback?
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString() + " must implement " +
                        "BaseActivityCallback"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mMifosProgressBarHandler = MifosProgressBarHandler(requireActivity())
    }

    fun showAlertDialog(title: String?, message: String?) {
        MaterialDialog.Builder().init(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(getStringMessage(R.string.dialog_action_ok)) { dialog, id -> dialog.dismiss() }
            .createMaterialDialog()
            .show()
    }

    val toolbar: Toolbar?
        get() = (getActivity() as MifosBaseActivity).toolbar

    protected fun showMifosProgressDialog(message: String = "Working...") {
        if (callback != null) callback?.showProgress(message)
    }

    protected fun hideMifosProgressDialog() {
        if (callback != null) callback?.hideProgress()
    }

    protected fun logout() {
        callback?.logout()
    }

    protected fun setToolbarTitle(title: String) {
        callback?.setToolbarTitle(title)
    }

    fun hideKeyboard(view: View) {
        inputManager?.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.RESULT_UNCHANGED_SHOWN
        )
    }

    protected fun showMifosProgressBar() {
        mMifosProgressBarHandler?.show()
    }

    protected fun hideMifosProgressBar() {
        mMifosProgressBarHandler?.hide()
    }

    protected fun getStringMessage(message: Int): String {
        return resources.getString(message)
    }

    protected val isOnline: Boolean
        get() = Network.isOnline(requireContext())
}