/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.formwidgets;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import org.json.JSONObject;

/**
 * Created by ishankhanna on 01/08/14.
 */
public abstract class FormWidget {

    public static final LayoutParams defaultLayoutParams = new LinearLayout.LayoutParams
            (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    public static String SCHEMA_KEY_TYPE = "type";
    public static String SCHEMA_KEY_BOOL = "BOOLEAN";
    public static String SCHEMA_KEY_INT = "INTEGER";
    public static String SCHEMA_KEY_DECIMAL = "DECIMAL";
    public static String SCHEMA_KEY_STRING = "STRING";
    public static String SCHEMA_KEY_TEXT = "TEXT";
    public static String SCHEMA_KEY_CODELOOKUP = "CODELOOKUP";
    public static String SCHEMA_KEY_CODEVALUE = "CODEVALUE";
    public static String SCHEMA_KEY_DATE = "DATE";
    public static String SCHEMA_KEY_PRIORITY = "priority";
    public static String SCHEMA_KEY_DEFAULT = "default";
    public static String SCHEMA_KEY_OPTIONS = "options";
    public static String SCHEMA_KEY_META = "meta";
    public static String SCHEMA_KEY_HINT = "hint";
    protected View view;
    protected String property;
    protected String displayText;
    protected int priority;
    protected LinearLayout layout;
    protected String returnType;

    public FormWidget(Context context, String name) {
        layout = new LinearLayout(context);
        layout.setLayoutParams(defaultLayoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(4, 4, 4, 4);
        property = name;
        displayText = name.replace("", " ");
        displayText = toTitleCase(displayText);
        returnType = SCHEMA_KEY_TEXT;

    }

    // -----------------------------------------------
    //
    // view
    //
    // -----------------------------------------------

    /**
     * return LinearLayout containing this widget's view elements
     */
    public View getView() {
        return layout;
    }

    /**
     * toggles the visibility of this widget
     *
     * @param value
     */
    public void setVisibility(int value) {
        layout.setVisibility(value);
    }

    // -----------------------------------------------
    //
    // set / get value
    //
    // -----------------------------------------------

    /**
     * returns value of this widget as String
     */
    public String getValue() {
        return "";
    }

    /**
     * sets value of this widget, method should be overridden in sub-class
     *
     * @param value
     */
    public void setValue(String value) {
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
    public void setHint(String value) {
        // -- override
    }

    /**
     * sets an object that contains keys for special properties on an object
     *
     * @param modifiers
     */
    public void setModifiers(JSONObject modifiers) {
        // -- override
    }

    // -----------------------------------------------
    //
    // set / get priority
    //
    // -----------------------------------------------

    /**
     * returns visual priority
     *
     * @return
     */
    public int getPriority() {
        return priority;
    }

    /**
     * sets the visual priority of this widget
     * essentially this means it's physical location in the form
     */
    public void setPriority(int value) {
        priority = value;
    }

    // -----------------------------------------------
    //
    // property name mods
    //
    // -----------------------------------------------

    /**
     * returns the un-modified name of the property this widget represents
     */
    public String getPropertyName() {
        return property;
    }

    /**
     * returns a title case version of this property
     *
     * @return
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * takes a property name and modifies
     *
     * @param s
     * @return
     */
    public String toTitleCase(String s) {
        char[] chars = s.trim().toLowerCase().toCharArray();
        boolean found = false;

        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i])) {
                found = false;
            }
        }

        return String.valueOf(chars);
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}
