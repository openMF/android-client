package com.mifos.mifosxdroid.formwidgets

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

/**
 * Created by Tarun on 1/28/2017.
 */
class FormToggleButton(context: Context?, name: String?) : FormWidget(context, name) {
    private var label: TextView
    private var switchButton: SwitchCompat
    private val weightedLayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT, 0.5.toFloat()
    )
    private var isTrue = false

    init {
        val linearLayout = LinearLayout(context)
        linearLayout.layoutParams = defaultLayoutParams
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.gravity = Gravity.CENTER_HORIZONTAL
        label = TextView(context)
        label.text = displayText
        label.layoutParams = weightedLayoutParams
        linearLayout.addView(label)
        switchButton = SwitchCompat(context!!)
        switchButton.layoutParams = weightedLayoutParams
        switchButton.gravity = Gravity.CENTER_HORIZONTAL
        switchButton.switchMinWidth = 50
        switchButton.isChecked = false
        switchButton.setOnCheckedChangeListener { compoundButton, b -> isTrue = b }
        linearLayout.addView(switchButton)
        layout.addView(linearLayout)
    }

    override var value: String
        get() = if (isTrue) "true" else "false"
        set(value) {}
}