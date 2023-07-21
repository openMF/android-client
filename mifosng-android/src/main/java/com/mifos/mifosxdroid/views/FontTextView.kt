/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.views.Font.Companion.getFont

/**
 * @author fomenkoo
 */
class FontTextView : AppCompatTextView {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        parseAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        parseAttributes(context, attrs)
    }

    constructor(context: Context) : super(context) {}

    private fun parseAttributes(context: Context, attrs: AttributeSet) {
        if (!isInEditMode) {
            val values = context.obtainStyledAttributes(attrs, R.styleable.CustomFont)
            val typeface = values.getInt(
                R.styleable.CustomFont_typeface, Font.ROBOTO_MEDIUM
                    .ordinal
            )
            val font = getFont(typeface)
            setTypeface(font.typeface)
        }
    }
}