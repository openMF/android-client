package com.mifos.mifosxdroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int state = Constants.USER_OFFLINE;
        if (Network.isOnline(context)) {
            state = Constants.USER_ONLINE;
        }
        Intent i = new Intent(context, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(context.getString(R.string.network_state), state);
        context.startActivity(i);
    }
}
