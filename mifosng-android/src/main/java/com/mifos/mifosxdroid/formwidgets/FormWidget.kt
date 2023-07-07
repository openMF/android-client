/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.formwidgets

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import org.json.JSONObject
import java.util.Locale

/**
 * Created by ishankhanna on 01/08/14.
 */
abstract class FormWidget(context: Context?, name: String?) {


    /**
     * returns the un-modified name of the property this widget represents
     */
    var propertyName: String
        protected set

    /**
     * returns a title case version of this property
     *
     * @return
     */
    var displayText: String
        protected set
    /**
     * returns visual priority
     *
     * @return
     */
    /**
     * sets the visual priority of this widget
     * essentially this means it's physical location in the form
     */
    var priority = 0
    protected var layout: LinearLayout
    var returnType: String

    init {
        layout = LinearLayout(context)
        layout.layoutParams = defaultLayoutParams
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(4, 4, 4, 4)
        propertyName = name!!
        displayText = name.replace("", " ")
        displayText = toTitleCase(displayText)
        returnType = SCHEMA_KEY_TEXT
    }
    // -----------------------------------------------
    //
    // view
    //
    // -----------------------------------------------
    /**
     * return LinearLayout containing this widget's view elements
     */
    val view : View
        get() = layout

    /**
     * toggles the visibility of this widget
     *
     * @param value
     */
    fun setVisibility(value: Int) {
        layout.visibility = value
    }
    // -----------------------------------------------
    //
    // set / get value
    //
    // -----------------------------------------------
    /**
     * returns value of this widget as String
     */// -- override
    /**
     * sets value of this widget, method should be overridden in sub-class
     *
     * @param value
     */
    open var value: String
        get() = ""
        set(value) {
            // -- override
        }
    // -----------------------------------------------
    //
    // modifiers
    //
    // -----------------------------------------------
    /**
     * sets the hint for the widget, method should be overriden in sub-class
     */
    open fun setHint(value: String?) {
        // -- override
    }

    /**
     * sets an object that contains keys for special properties on an object
     *
     * @param modifiers
     */
    fun setModifiers(modifiers: JSONObject?) {
        // -- override
    }
    // -----------------------------------------------
    //
    // set / get priority
    //
    // -----------------------------------------------
    // -----------------------------------------------
    //
    // property name mods
    //
    // -----------------------------------------------
    /**
     * takes a property name and modifies
     *
     * @param s
     * @return
     */
    fun toTitleCase(s: String): String {
        val chars = s.trim { it <= ' ' }.lowercase(Locale.getDefault()).toCharArray()
        var found = false
        for (i in chars.indices) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = chars[i].uppercaseChar()
                found = true
            } else if (Character.isWhitespace(chars[i])) {
                found = false
            }
        }
        return String(chars)
    }

    companion object {
        val defaultLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        const val SCHEMA_KEY_TYPE = "type"
        const val SCHEMA_KEY_BOOL = "BOOLEAN"
        const val SCHEMA_KEY_INT = "INTEGER"
        const val SCHEMA_KEY_DECIMAL = "DECIMAL"
        const val SCHEMA_KEY_STRING = "STRING"
        const val SCHEMA_KEY_TEXT = "TEXT"
        const val SCHEMA_KEY_CODELOOKUP = "CODELOOKUP"
        const val SCHEMA_KEY_CODEVALUE = "CODEVALUE"
        const val SCHEMA_KEY_DATE = "DATE"
        const val SCHEMA_KEY_PRIORITY = "priority"
        const val SCHEMA_KEY_DEFAULT = "default"
        const val SCHEMA_KEY_OPTIONS = "options"
        const val SCHEMA_KEY_META = "meta"
        const val SCHEMA_KEY_HINT = "hint"
    }
}