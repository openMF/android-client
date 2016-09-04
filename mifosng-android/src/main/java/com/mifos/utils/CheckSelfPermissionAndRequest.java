package com.mifos.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 *
 * Created by Rajan Maurya on 04/09/16.
 */
public class CheckSelfPermissionAndRequest {


    public static Boolean checkSelfPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void requestPermission(AppCompatActivity activity,
                                         String permission,
                                         int MY_PERMISSIONS_REQUEST) {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(activity, new String[]{permission},
                    MY_PERMISSIONS_REQUEST);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }
}
