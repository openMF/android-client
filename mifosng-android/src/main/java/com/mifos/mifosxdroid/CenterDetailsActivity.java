package com.mifos.mifosxdroid;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import java.util.Calendar;

public class CenterDetailsActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {
    public static String PREF_CENTER_DETAILS = "pref_center_details";
    public static String CENTER_ID_KEY = "pref_center_id";
    public static String TRANSACTION_DATE_KEY = "pref_transaction_date";
    @InjectView(R.id.et_center_id)
    EditText etCenterId;
    @InjectView(R.id.tv_select_date)
    TextView tvSelectDate;
    @InjectView(R.id.btnSave)
    Button btnSave;
    private String date;
    private int centerId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isCenterIdAvailable()) {
            finishAndStartGroupActivity();
        }
        setContentView(R.layout.activity_center_details);
        ButterKnife.inject(this);

    }

    private boolean isCenterIdAvailable() {
        SharedPreferences preferences = getSharedPreferences(CenterDetailsActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
        int centerId = preferences.getInt(CenterDetailsActivity.CENTER_ID_KEY, -1);
        if (centerId != -1)
            return true;
        else
            return false;

    }

    @OnClick(R.id.tv_select_date)
    public void OnSelectDate(TextView textView) {
        createDatePicker(this, this);
    }

    @OnClick(R.id.btnSave)
    public void OnClickSave(Button button) {
        if (getData()) {
            saveCenterIdToPref();
            finishAndStartGroupActivity();
        }
    }

    private void finishAndStartGroupActivity() {
        finish();
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

    private boolean getData() {
        boolean isAllDetailsFilled = true;
        if (etCenterId.getText().toString().length() > 0 && tvSelectDate.getText().toString().length() > 0) {
            centerId = Integer.parseInt(etCenterId.getEditableText().toString());
            date = tvSelectDate.getText().toString();
        } else {
            isAllDetailsFilled = false;
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        }
        return isAllDetailsFilled;
    }

    private void createDatePicker(Context context, DatePickerDialog.OnDateSetListener dateSetListener) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                          int dayOfMonth) {
        StringBuilder date = new StringBuilder();
        date.append(dayOfMonth);
        date.append("-");
        date.append(monthOfYear + 1);
        date.append("-");
        date.append(year);
        tvSelectDate.setText(date.toString());
    }

    private void saveCenterIdToPref() {
        SharedPreferences preferences = getSharedPreferences(PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CENTER_ID_KEY, centerId);
        editor.putString(TRANSACTION_DATE_KEY, date);
        editor.commit();
    }
}
