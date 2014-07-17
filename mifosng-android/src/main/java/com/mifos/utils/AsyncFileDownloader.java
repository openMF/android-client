/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.User;
import com.mifos.services.API;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ishankhanna on 02/07/14.
 */
public class AsyncFileDownloader extends AsyncTask<String, Integer, File> {

    Context context;
    String fileName;
    InputStream inputStream;
    OutputStream outputStream;
    SafeUIBlockingUtility safeUIBlockingUtility;

    public AsyncFileDownloader(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        safeUIBlockingUtility = new SafeUIBlockingUtility(context);
        safeUIBlockingUtility.safelyBlockUI();
    }

    /**
     * @param strings
     * @return
     */

    @Override
    protected File doInBackground(String... strings) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(Constants.applicationContext);
        String authToken = pref.getString(User.AUTHENTICATION_KEY, "NA");
        String mInstanceUrl = pref.getString(Constants.INSTANCE_URL_KEY,
                context.getString(R.string.default_instance_url));

        String url = Constants.PROTOCOL_HTTPS
                + mInstanceUrl
                + Constants.API_PATH + "/"
                + strings[0] + "/" // {entityType}
                + strings[1] + "/"//{entityId}
                + "documents/"
                + strings[2] + "/" //{documentId}
                + "attachment";

        File documentFile = new File(Environment.getExternalStorageDirectory().getPath()+"/"
                + fileName);
        try {

            HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("X-Mifos-Platform-TenantId", "default");
            httpURLConnection.setRequestProperty(API.HEADER_AUTHORIZATION, authToken);
            //httpURLConnection.setRequestProperty("Accept", "application/octet-stream");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            Log.i("Connected", "True");
            inputStream = httpURLConnection.getInputStream();



            if(!documentFile.exists())
                documentFile.createNewFile();

            outputStream = new FileOutputStream(documentFile);


            int read = 0;

            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            httpURLConnection.disconnect();
            Log.i("Connected", "False");

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException ioe) {

            ioe.printStackTrace();

        } finally {
            if (inputStream != null) {
                try {

                    inputStream.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
            if (outputStream != null) {
                try {

                    outputStream.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        }


        return documentFile;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        safeUIBlockingUtility.safelyUnBlockUI();
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //TODO Add Support for maximum Mime Types
        String fileType = "";

        if(fileName.contains(".pdf")) {
            fileType = "application/pdf";
        } else if (fileName.contains(".rtf")) {
            fileType = "application/rtf";
        } else if (fileName.contains(".png")) {
            fileType = "image/png";
        } else if (fileName.contains(".jpg") || fileName.contains(".jpeg")) {
            fileType = "image/jpeg";
        } else if (fileName.contains(".txt")) {
            fileType = "text/plain";
        } else if (fileName.contains(".html") || fileName.contains(".htm")) {
            fileType = "text/html";
        } else if (fileName.contains(".xls")) {
            fileType = "application/vnd.ms-excel";
        }

        intent.setDataAndType(Uri.fromFile(file), fileType);
        context.startActivity(intent);
    }
}
