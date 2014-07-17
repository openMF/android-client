/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network
{
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork)
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context)
    {
        int conn = Network.getConnectivityStatus(context);
        String status = null;
        if (conn == Network.TYPE_WIFI)
        {
            status = "Wifi enabled";
        } else if (conn == Network.TYPE_MOBILE)
        {
            status = "Mobile data enabled";
        } else if (conn == Network.TYPE_NOT_CONNECTED)
        {
            status = "Not connected to Internet";
        }
        return status;
    }

    public static boolean isOnline(Context context){

        int connectivityStatus = Network.getConnectivityStatus(context);

        return (connectivityStatus == Network.TYPE_WIFI ||
                connectivityStatus == Network.TYPE_MOBILE);
    }
}
