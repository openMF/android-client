/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;


import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mifos.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.client.Response;

public class MFErrorParser {

    public static final String LOG_TAG = "MFErrorParser";

    public static void parseError(Response response) {


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()
                    .in()));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
            Log.d("MFErrorParser", out.toString());
            MFErrorResponse mfErrorResponse = new Gson().fromJson(out.toString(), MFErrorResponse
                    .class);
            Log.d(LOG_TAG, mfErrorResponse.toString());
            List<MFError> mfErrorList = mfErrorResponse.getErrors();

            for (MFError mfError : mfErrorList) {
                Toast.makeText(App.getContext(), mfError.getDefaultUserMessage(), Toast
                        .LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(LOG_TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }

    }


}
