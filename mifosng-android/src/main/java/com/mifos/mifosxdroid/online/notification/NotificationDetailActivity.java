package com.mifos.mifosxdroid.online.notification;

import android.os.Bundle;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.utils.Constants;

public class NotificationDetailActivity extends MifosBaseActivity {

    private String subject;
    private String object;
    private String action;
    private String actor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            subject = bundle.getString(Constants.NOTIFICATION_SUBJECT);
            object = bundle.getString(Constants.NOTIFICATION_OBJECT);
            action = bundle.getString(Constants.NOTIFICATION_ACTION);
            actor = bundle.getString(Constants.NOTIFICATION_ACTOR);
        }
    }

}
