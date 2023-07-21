package com.mifos.mifosxdroid.core

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mifos.mifosxdroid.R

/**
 * This Class is the Material Dialog Builder Class
 * Created by Rajan Maurya on 03/08/16.
 */
class MaterialDialog {
    class Builder {
        private var mMaterialDialogBuilder: MaterialAlertDialogBuilder? = null

        //This is the Default Builder Initialization with Material Style
        fun init(context: Context?): Builder {
            mMaterialDialogBuilder =
                context?.let { MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialogStyle) }
            return this
        }

        //This method set the custom Material Style
        fun init(context: Context?, theme: Int): Builder {
            mMaterialDialogBuilder = context?.let { MaterialAlertDialogBuilder(it, theme) }
            return this
        }

        //This method set the String Title
        fun setTitle(title: String?): Builder {
            mMaterialDialogBuilder?.setTitle(title)
            return this
        }

        //This Method set the String Resources to Title
        fun setTitle(@StringRes title: Int): Builder {
            mMaterialDialogBuilder?.setTitle(title)
            return this
        }

        //This Method set the String Message
        fun setMessage(message: String?): Builder {
            mMaterialDialogBuilder?.setMessage(message)
            return this
        }

        //This Method set the String Resources message
        fun setMessage(@StringRes message: Int): Builder {
            mMaterialDialogBuilder?.setMessage(message)
            return this
        }

        //This Method set String Test to the Positive Button and set the Onclick null
        fun setPositiveButton(positiveText: String?): Builder {
            mMaterialDialogBuilder?.setPositiveButton(positiveText, null)
            return this
        }

        //This Method Set the String Resources Text To Positive Button
        fun setPositiveButton(@StringRes positiveText: Int): Builder {
            mMaterialDialogBuilder?.setPositiveButton(positiveText, null)
            return this
        }

        //This Method set the String Text to Positive Button and set the OnClick Event to it
        fun setPositiveButton(
            positiveText: String?,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mMaterialDialogBuilder?.setPositiveButton(positiveText, listener)
            return this
        }

        //This method set the String Resources text To Positive button and set the Onclick Event
        fun setPositiveButton(
            @StringRes positiveText: Int,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mMaterialDialogBuilder?.setPositiveButton(positiveText, listener)
            return this
        }

        //This Method the String Text to Negative Button and Set the onclick event to null
        fun setNegativeButton(negativeText: String?): Builder {
            mMaterialDialogBuilder?.setNegativeButton(negativeText, null)
            return this
        }

        //This Method set the String Resources Text to Negative button
        // and set the onclick event to null
        fun setNegativeButton(@StringRes negativeText: Int): Builder {
            mMaterialDialogBuilder?.setNegativeButton(negativeText, null)
            return this
        }

        //This Method set String Text to Negative Button and
        //Set the Onclick event
        fun setNegativeButton(
            negativeText: String?,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mMaterialDialogBuilder?.setNegativeButton(negativeText, listener)
            return this
        }

        //This method set String Resources Text to Negative Button and set Onclick Event
        fun setNegativeButton(
            @StringRes negativeText: Int,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mMaterialDialogBuilder?.setNegativeButton(negativeText, listener)
            return this
        }

        //This Method the String Text to Neutral Button and Set the onclick event to null
        fun setNeutralButton(neutralText: String?): Builder {
            mMaterialDialogBuilder?.setNeutralButton(neutralText, null)
            return this
        }

        //This Method set the String Resources Text to Neutral button
        // and set the onclick event to null
        fun setNeutralButton(@StringRes neutralText: Int): Builder {
            mMaterialDialogBuilder?.setNeutralButton(neutralText, null)
            return this
        }

        //This Method set String Text to Neutral Button and
        //Set the Onclick event
        fun setNeutralButton(
            neutralText: String?,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mMaterialDialogBuilder?.setNeutralButton(neutralText, listener)
            return this
        }

        //This method set String Resources Text to Neutral Button and set Onclick Event
        fun setNeutralButton(
            @StringRes neutralText: Int,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mMaterialDialogBuilder?.setNeutralButton(neutralText, listener)
            return this
        }

        fun setCancelable(cancelable: Boolean?): Builder {
            mMaterialDialogBuilder?.setCancelable(cancelable!!)
            return this
        }

        fun setItems(items: Int, listener: DialogInterface.OnClickListener?): Builder {
            mMaterialDialogBuilder?.setItems(items, listener)
            return this
        }

        fun setItems(
            items: Array<CharSequence>,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mMaterialDialogBuilder?.setItems(items, listener)
            return this
        }

        //This Method Create the Final Material Dialog
        fun createMaterialDialog(): Builder {
            mMaterialDialogBuilder?.create()
            return this
        }

        //This Method Show the Dialog
        fun show(): Builder {
            mMaterialDialogBuilder?.show()
            return this
        }
    }
}