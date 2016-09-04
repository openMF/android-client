package com.mifos.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MaterialDialog;

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
    public static void requestPermission(final AppCompatActivity activity,
                                         final String permission,
                                         final int MY_PERMISSIONS_REQUEST,
                                         final String dialogMessage) {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            new MaterialDialog.Builder().init(activity)
                    .setTitle(R.string.permission_denied)
                    .setMessage(dialogMessage)
                    .setPositiveButton(R.string.dialog_action_i_am_sure, null)
                    .setNegativeButton(R.string.dialog_action_re_try,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission},
                                    MY_PERMISSIONS_REQUEST);
                        }
                    })
                    .createMaterialDialog()
                    .show();            // sees the explanation, try again to request the permission.

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
