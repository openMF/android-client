/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.utils;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.mifos.objects.survey.ScorecardValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class  MyPreference {
    public static final String PREFS_NAME = "MY_PREFS";
    public static final String SCOREVALUES = "Score_Values";
    public static final String question_id = "question_id";
    public static final String response_id = "response_id";
    public static final String response_value = "response_value";


    public MyPreference (){
        super();
    }


    // This four methods are used for maintaining favorites.
    public void saveScorecards(Context context, List<ScorecardValues> scorecardValues) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        //mcontext=context;
        Gson gson = new Gson();
        String jsonScores = gson.toJson(scorecardValues);

        editor.putString(SCOREVALUES, jsonScores);

        editor.commit();
    }

    public void addScorecard(Context context, ScorecardValues product) {
        List<ScorecardValues> scorecardValues = getScorecards(context);
        //mcontext=context;
        if (scorecardValues == null)
            scorecardValues = new ArrayList<ScorecardValues>();
        scorecardValues.add(product);
        saveScorecards(context, scorecardValues);
    }


    public boolean checkScoreQid(Context context,ScorecardValues checkProduct) {
        boolean check = false;
        List<ScorecardValues> scorecardValues = getScorecards(context);
        if (scorecardValues != null) {
            for (ScorecardValues product : scorecardValues) {
                if (product.getQuestionId() == checkProduct.getQuestionId()) {
                    product.setResponseId(checkProduct.getResponseId()) ;
                    product.setValue(checkProduct.getValue());
                    // removeFavorite(context,product);
                    check = true;
                    break;
                }
            }
            saveScorecards(context, scorecardValues);
        }
        return check;
    }

    public void resetScorecard(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();

    }

    public ArrayList<ScorecardValues> getScorecards(Context context) {
        SharedPreferences settings;
        List<ScorecardValues> scorecardValues;
        // mcontext=context;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(SCOREVALUES)) {
            String jsonScores = settings.getString(SCOREVALUES, null);
            Gson gson = new Gson();
            ScorecardValues[] scoreItems = gson.fromJson(jsonScores,
                    ScorecardValues[].class);

            scorecardValues = Arrays.asList(scoreItems);
            scorecardValues = new ArrayList<ScorecardValues>(scorecardValues);
        } else
            return null;

        return (ArrayList<ScorecardValues>) scorecardValues;
    }
}
