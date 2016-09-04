package com.mifos.utils;

/**
 *
 * Created by Rajan Maurya on 04/09/16.
 */
public class AndroidVersionUtil {

    public static int getApiVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static boolean isApiVersionGreaterOrEqual(int thisVersion) {
        return android.os.Build.VERSION.SDK_INT >= thisVersion;
    }
}