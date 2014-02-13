package com.mifos.utils;

import android.app.ProgressDialog;
import android.content.Context;


/**
 * @author ishankhanna
 * Class To Block User Interface Safely for Asynchronous Network Calls
 * and/or Heavy Operations
 */
public class SafeUIBlockingUtility {



    public static String utilityTitle = "Working";
    public static String utilityMessage = "Message";

    private ProgressDialog progressDialog;

    public SafeUIBlockingUtility(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(utilityMessage);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(utilityTitle);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void safelyBlockUI(){
        progressDialog.show();
    }

    public void safeUnBlockUI(){
        progressDialog.dismiss();
    }

    public static String getUtilityTitle() {
        return utilityTitle;
    }

    public static void setUtilityTitle(String utilityTitle) {
        SafeUIBlockingUtility.utilityTitle = utilityTitle;
    }

    public static String getUtilityMessage() {
        return utilityMessage;
    }

    public static void setUtilityMessage(String utilityMessage) {
        SafeUIBlockingUtility.utilityMessage = utilityMessage;
    }




}
