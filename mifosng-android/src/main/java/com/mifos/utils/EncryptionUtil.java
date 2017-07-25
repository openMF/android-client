package com.mifos.utils;

import android.util.Log;

/**
 * Created by mayankjindal on 30/06/17.
 */

public class EncryptionUtil {

    static {
        try {
            System.loadLibrary("encryption");
        } catch (UnsatisfiedLinkError e) {
            Log.e("LoadJniLib", "Error: Could not load native library: " + e.getMessage());
        }
    }

    private static final native String getPassCodeHash(String passcode);

    public static String getHash(String passCode) {
        return getPassCodeHash(passCode);
    }
}
