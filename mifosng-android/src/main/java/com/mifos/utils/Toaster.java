package com.mifos.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * @author fomenkoo
 */
public class Toaster {
    private static boolean DEFAULT = false;


    private static void showToast(Context context, String text, boolean isError) {
        if (DEFAULT) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {

        }
    }

    public static void showShort(String value, boolean isError) {
        showToast(MifosApplication.getContext(), value, isError);
    }

    public static void showShort(String value) {
        showToast(MifosApplication.getContext(), value, false);
    }


    public static void showShort(int resId, boolean isError) {
        showToast(MifosApplication.getContext(), MifosApplication.getContext().getString(resId), isError);
    }

    public static void showShort(Activity activity, String value, boolean isError) {
        if (activity != null) {
            showToast(MifosApplication.getContext(), value, isError);
        }
    }

    public static void showShort(Activity activity, int resId, boolean isError) {
        if (activity != null) {
            showToast(MifosApplication.getContext(), MifosApplication.getContext().getString(resId), isError);
        }
    }

}
