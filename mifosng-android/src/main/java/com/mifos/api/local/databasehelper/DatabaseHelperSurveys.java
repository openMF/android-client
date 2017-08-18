package com.mifos.api.local.databasehelper;


import com.mifos.objects.survey.QuestionDatas;
import com.mifos.objects.survey.QuestionDatas_Table;
import com.mifos.objects.survey.ResponseDatas;
import com.mifos.objects.survey.ResponseDatas_Table;
import com.mifos.objects.survey.Survey;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func0;

import static com.raizlabs.android.dbflow.sql.language.SQLite.select;

/**
 * Created by Rajan Maurya on 22/08/16.
 */
@Singleton
public class DatabaseHelperSurveys {

    @Inject
    public DatabaseHelperSurveys() {

    }

    /**
     * This Method save the single Survey in Database with SurveyId as Primary Id
     *
     * @param survey Survey
     * @return saved Survey
     */
    public Observable<Survey> saveSurvey(final Survey survey) {
        return Observable.defer(new Func0<Observable<Survey>>() {
            @Override
            public Observable<Survey> call() {
                //Saving Survey in Database
                survey.save();
                return Observable.just(survey);
            }
        });
    }

    /**
     * This Method save the single QuestionDatas in Database
     *
     * @param surveyId int, questionDatas QuestionDatas
     * @return saved QuestionDatas
     */
    public Observable<QuestionDatas> saveQuestionData(final int surveyId,
                                                      final QuestionDatas questionDatas) {
        return Observable.defer(new Func0<Observable<QuestionDatas>>() {
            @Override
            public Observable<QuestionDatas> call() {
                //Saving QuestionDatas in Database
                questionDatas.setSurveyId(surveyId);
                questionDatas.save();
                return Observable.just(questionDatas);
            }
        });
    }

    /**
     * This Method save the single ResponseDatas in Database
     *
     * @param questionId int, responseDatas ResponseDatas
     * @return saved ResponseDatas
     */
    public Observable<ResponseDatas> saveResponseData(final int questionId,
                                                      final ResponseDatas responseDatas) {
        return Observable.defer(new Func0<Observable<ResponseDatas>>() {
            @Override
            public Observable<ResponseDatas> call() {
                //Saving ResponseDatas in Database
                responseDatas.setQuestionId(questionId);
                responseDatas.save();
                return Observable.just(responseDatas);
            }
        });
    }

    /**
     * Reading All surveys from Database table of Survey and return the SurveyList
     *
     * @return List Of Surveys
     */
    public Observable<List<Survey>> readAllSurveys() {
        return Observable.defer(new Func0<Observable<List<Survey>>>() {
            @Override
            public Observable<List<Survey>> call() {
                List<Survey> surveyList = select()
                        .from(Survey.class)
                        .queryList();
                return Observable.just(surveyList);
            }
        });
    }

    /**
     * Reading All QuestionDatas from Database table of QuestionDatas
     * and return the QuestionDatasList
     * @return List Of QuestionDatas
     */
    public Observable<List<QuestionDatas>> getQuestionDatas(final int surveyId) {
        return Observable.defer(new Func0<Observable<List<QuestionDatas>>>() {
            @Override
            public Observable<List<QuestionDatas>> call() {
                List<QuestionDatas> questionDatas = select()
                        .from(QuestionDatas.class)
                        .where(QuestionDatas_Table.surveyId.eq(surveyId))
                        .orderBy(QuestionDatas_Table.sequenceNo, true)
                        .queryList();

                return Observable.just(questionDatas);
            }
        });
    }

    /**
     * Reading All ResponseDatas from Database table of ResponseDatas
     * and return the ResponseDatasList
     * @return List Of ResponseDatas
     */
    public Observable<List<ResponseDatas>> getResponseDatas(final int questionId) {
        return Observable.defer(new Func0<Observable<List<ResponseDatas>>>() {
            @Override
            public Observable<List<ResponseDatas>> call() {
                List<ResponseDatas> responseDatas = select()
                        .from(ResponseDatas.class)
                        .where(ResponseDatas_Table.questionId.eq(questionId))
                        .orderBy(ResponseDatas_Table.sequenceNo, true)
                        .queryList();

                return Observable.just(responseDatas);
            }
        });
    }
}
