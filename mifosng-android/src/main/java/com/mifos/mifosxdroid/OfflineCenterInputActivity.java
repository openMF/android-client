/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.R.id;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfflineCenterInputActivity extends MifosBaseActivity implements DatePickerDialog
        .OnDateSetListener {
    public static final String PREF_CENTER_DETAILS = "pref_center_details";
    public static final String STAFF_ID_KEY = "pref_staff_id";
    public static final String BRANCH_ID_KEY = "pref_branch_id";
    public static final String TRANSACTION_DATE_KEY = "pref_transaction_date";
    @BindView(R.id.et_staff_id)
    EditText etStaffId;
    @BindView(R.id.et_branch_id)
    EditText etBranchId;
    @BindView(R.id.tv_select_date)
    TextView tvSelectDate;
    private String date;
    private int staffId;
    private int branchId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isCenterIdAvailable()) {
            finishAndStartCenterListActivity();
        }
        setContentView(R.layout.activity_center_details);
        ButterKnife.bind(this);
        showBackButton();
    }

    private boolean isCenterIdAvailable() {
        SharedPreferences preferences = getSharedPreferences(
                PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
        int centerId = preferences.getInt(STAFF_ID_KEY, -1);
        if (centerId != -1) {
            return true;
        } else {
            return false;
        }

    }

    @OnClick(R.id.tv_select_date)
    public void onSelectDate(TextView textView) {
        createDatePicker(this, this);
    }

    @OnClick(R.id.btnSave)
    public void onClickSave(Button button) {
        if (isData()) {
            saveCenterIdToPref();
            finishAndStartCenterListActivity();
        }
    }

    private void finishAndStartCenterListActivity() {
        finish();
        Intent intent = new Intent(this, CenterListActivity.class);
        startActivity(intent);
    }

    private boolean isData() {
        boolean isAllDetailsFilled = true;
        if (etStaffId.getText().toString().length() > 0
                && tvSelectDate.getText().toString().length() > 0
                && etBranchId.getText().toString().length() > 0) {
            date = tvSelectDate.getText().toString();
            // Check valid Integers
            isAllDetailsFilled = hasValidStaffAndBranchId();
        } else {
            isAllDetailsFilled = false;
            Toaster.show(findViewById(id.content), "Please fill all the details");
        }
        return isAllDetailsFilled;
    }

    // Prevent crash in case user doesn't enter a valid Integer - NumberFormatException
    private boolean hasValidStaffAndBranchId() {
        boolean isValidInteger = true;
        try {
            staffId = Integer.parseInt(etStaffId.getEditableText().toString());
        } catch (NumberFormatException e) {
            //Here request user for a valid value
            Toaster.show(findViewById(id.content), "Staff ID is not valid Integer");
            etStaffId.requestFocus();
            isValidInteger = false;
        }

        try {
            branchId = Integer.parseInt(etBranchId.getEditableText().toString());
        } catch (NumberFormatException e) {
            //Here request user for a valid value
            Toaster.show(findViewById(id.content), "Branch ID is not valid Integer");
            etBranchId.requestFocus();
            isValidInteger = false;
        }
        return isValidInteger;
    }

    private void createDatePicker(Context context, DatePickerDialog.OnDateSetListener
            dateSetListener) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, year,
                month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                          int dayOfMonth) {
        StringBuilder date = new StringBuilder();
        date.append(dayOfMonth)
                .append('-')
                .append(monthOfYear + 1)
                .append('-')
                .append(year);
        tvSelectDate.setText(date.toString());
    }

    private void saveCenterIdToPref() {
        SharedPreferences preferences = getSharedPreferences(PREF_CENTER_DETAILS, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(STAFF_ID_KEY, staffId);
        editor.putInt(BRANCH_ID_KEY, branchId);
        editor.putString(TRANSACTION_DATE_KEY, date);
        editor.commit();
    }
}
