package com.mifos.mifosxdroid.formwidgets;

import android.content.Context;
import androidx.appcompat.widget.SwitchCompat;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Tarun on 1/28/2017.
 */
public class FormToggleButton extends FormWidget {
    protected TextView label;
    protected SwitchCompat switchButton;

    private LinearLayout.LayoutParams weightedLayoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, (float) 0.5);

    private boolean isTrue = false;

    public FormToggleButton(Context context, String name) {

        super(context, name);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(FormWidget.defaultLayoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        label = new TextView(context);
        label.setText(getDisplayText());
        label.setLayoutParams(weightedLayoutParams);
        linearLayout.addView(label);

        switchButton = new SwitchCompat(context);
        switchButton.setLayoutParams(weightedLayoutParams);
        switchButton.setGravity(Gravity.CENTER_HORIZONTAL);
        switchButton.setSwitchMinWidth(50);
        switchButton.setShowText(true);
        switchButton.setChecked(false);
        switchButton.setTextOn("True");
        switchButton.setTextOff("False");
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isTrue = b;
            }
        });
        linearLayout.addView(switchButton);

        layout.addView(linearLayout);

    }

    @Override
    public String getValue() {
        return isTrue ? "true" : "false";
    }

}
