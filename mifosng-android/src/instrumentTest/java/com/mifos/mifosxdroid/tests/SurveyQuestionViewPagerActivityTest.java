/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.tests;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.SurveyQuestionViewPager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by ahmed fathy on 11/04/16.
 */
public class SurveyQuestionViewPagerActivityTest extends ActivityInstrumentationTestCase2<SurveyQuestionViewPager> {

    /* mock data */
    public static final int N_QUESTIONS = 3;
    public static final String SURVEY_JSON =
            "{\n" +
                    "    \"componentDatas\": [\n" +
                    "        {\n" +
                    "            \"id\": 5,\n" +
                    "            \"key\": \"Household\",\n" +
                    "            \"sequenceNo\": 1,\n" +
                    "            \"text\": \"Information about the household.\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"countryCode\": \"KE\",\n" +
                    "    \"id\": 24,\n" +
                    "    \"key\": \"ppi-kenya-2010\",\n" +
                    "    \"name\": \"PPI Survey for Kenya, version 2010\",\n" +
                    "    \"questionDatas\": [\n" +
                    "        {\n" +
                    "            \"componentKey\": \"CK\",\n" +
                    "            \"id\": 1,\n" +
                    "            \"key\": \"Key1\",\n" +
                    "            \"responseDatas\": [\n" +
                    "                {\n" +
                    "                    \"id\": 1,\n" +
                    "                    \"sequenceNo\": 1,\n" +
                    "                    \"text\": \"Answer 1a\",\n" +
                    "                    \"value\": 1\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"id\": 2,\n" +
                    "                    \"sequenceNo\": 2,\n" +
                    "                    \"text\": \"Answer 1b\",\n" +
                    "                    \"value\": 5\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"id\": 3,\n" +
                    "                    \"sequenceNo\": 3,\n" +
                    "                    \"text\": \"Answer 1c\",\n" +
                    "                    \"value\": 13\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"sequenceNo\": 1,\n" +
                    "            \"text\": \"This is question 1\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"componentKey\": \"CK\",\n" +
                    "            \"id\": 2,\n" +
                    "            \"key\": \"Key2\",\n" +
                    "            \"responseDatas\": [\n" +
                    "                {\n" +
                    "                    \"id\": 1,\n" +
                    "                    \"sequenceNo\": 1,\n" +
                    "                    \"text\": \"Answer 2a\",\n" +
                    "                    \"value\": 1\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"id\": 2,\n" +
                    "                    \"sequenceNo\": 2,\n" +
                    "                    \"text\": \"Answer 2b\",\n" +
                    "                    \"value\": 5\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"id\": 3,\n" +
                    "                    \"sequenceNo\": 3,\n" +
                    "                    \"text\": \"Answer 2c\",\n" +
                    "                    \"value\": 13\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"sequenceNo\": 2,\n" +
                    "            \"text\": \"This is question 2\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"componentKey\": \"CK\",\n" +
                    "            \"id\": 1,\n" +
                    "            \"key\": \"Key3\",\n" +
                    "            \"responseDatas\": [\n" +
                    "                {\n" +
                    "                    \"id\": 1,\n" +
                    "                    \"sequenceNo\": 1,\n" +
                    "                    \"text\": \"Answer 3a\",\n" +
                    "                    \"value\": 1\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"id\": 2,\n" +
                    "                    \"sequenceNo\": 2,\n" +
                    "                    \"text\": \"Answer 3b\",\n" +
                    "                    \"value\": 5\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"id\": 3,\n" +
                    "                    \"sequenceNo\": 3,\n" +
                    "                    \"text\": \"Answer 3c\",\n" +
                    "                    \"value\": 13\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"sequenceNo\": 3,\n" +
                    "            \"text\": \"This is question 3\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";


    public SurveyQuestionViewPagerActivityTest() {
        super(SurveyQuestionViewPager.class);
    }

    /* tests */

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // pass the mock intent
        Intent intent = new Intent();
        intent.putExtra("ClientId", "000000001");
        intent.putExtra("Survey", SURVEY_JSON);
        setActivityIntent(intent);

        getActivity();
    }

    /**
     * checks all th questions and answers are displayed correctly
     */
    @MediumTest
    public void testQuestionAndAnswersDisplayed() throws InterruptedException {

        for (int i = 1; i <= N_QUESTIONS; i++) {

            // check the numbering in the subtitle
            assertEquals(i + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

            // check question
            onView(withText("This is question " + i))
                    .check(matches(isDisplayed()));

            // check answers
            onView(withText("Answer " + i + "a"))
                    .check(matches(isDisplayed()));
            onView(withText("Answer " + i + "b"))
                    .check(matches(isDisplayed()));
            onView(withText("Answer " + i + "c"))
                    .check(matches(isDisplayed()));

            // go to the next question
            onView(withId(R.id.btnNext))
                    .perform(click());
        }

    }

    /**
     * answers each question then press next
     * press submit
     */
    @MediumTest
    public void testAnswerSurvey() throws InterruptedException {

        // answer all questions
        for (int i = 1; i <= N_QUESTIONS; i++) {

            // choose an answer
            onView(withText("Answer " + i + "a"))
                    .perform(click())
                    .check(matches(isChecked()));

            // go to the next question
            onView(withId(R.id.btnNext))
                    .check(matches(isDisplayed()))
                    .perform(click());

            // check the numbering in the subtitle
            if (i < N_QUESTIONS)
                assertEquals((i + 1) + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        }

        // check we reached the submission page
        assertEquals("Submit Survey", getActivity().getToolbar().getSubtitle());
        onView(withId(R.id.btn_submit))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);

    }

    /**
     * swipe right to go to question 2
     * swipe left to go back to question 1
     * swipe right two times to question 3
     */
    public void testSwipeNavigate()  {

        assertEquals(1 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        // to page 2
        onView(withId(R.id.surveyPager))
                .perform(swipeLeft());
        assertEquals(2 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        // to page 1
        onView(withId(R.id.surveyPager))
                .perform(swipeRight());
        assertEquals(1 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        // to page 3
        onView(withId(R.id.surveyPager))
                .perform(swipeLeft());
        onView(withId(R.id.surveyPager))
                .perform(swipeLeft());
        assertEquals(3 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

    }

    /**
     * answers all the questions
     * goes back to each question and check the answers are still checked
     */
    public void testAnswersPersistent() throws InterruptedException {
        // answer the first question
        assertEquals(1 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());
        onView(withText("Answer 1a"))
                .perform(click());

        // go to the second question
        onView(withId(R.id.surveyPager))
                .perform(swipeLeft());
        assertEquals(2 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        // answer the second question
        Thread.sleep(300);
        onView(withText("Answer 2b"))
                .perform(click());

        // go to the third question
        onView(withId(R.id.surveyPager))
                .perform(swipeLeft());
        assertEquals(3 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        // answer the third question
        Thread.sleep(300);
        onView(withText("Answer 3c"))
                .perform(click());

        // go go back to the first question
        onView(withId(R.id.surveyPager))
                .perform(swipeRight());
        onView(withId(R.id.surveyPager))
                .perform(swipeRight());
        assertEquals(1 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        // check the answer is checked
        onView(withText("Answer 1a"))
                .check(matches(isChecked()));


        // go to the second question
        onView(withId(R.id.surveyPager))
                .perform(swipeLeft());
        assertEquals(2 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        // check the answer is checked
        onView(withText("Answer 2b"))
                .check(matches(isChecked()));

        // go to the third question
        onView(withId(R.id.surveyPager))
                .perform(swipeLeft());
        assertEquals(3 + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle());

        // check the answer is checked
        onView(withText("Answer 3c"))
                .check(matches(isChecked()));

    }

}
