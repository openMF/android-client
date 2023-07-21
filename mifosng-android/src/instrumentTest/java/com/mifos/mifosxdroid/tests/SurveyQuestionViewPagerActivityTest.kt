/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.tests

import android.test.ActivityInstrumentationTestCase2
import com.mifos.mifosxdroid.R

/**
 * Created by ahmed fathy on 11/04/16.
 */
class SurveyQuestionViewPagerActivityTest :
    ActivityInstrumentationTestCase2<SurveyQuestionActivity?>(
        SurveyQuestionActivity::class.java
    ) {
    /* tests */
    @Throws(Exception::class)
    fun setUp() {
        super.setUp()

        // pass the mock intent
        val intent = Intent()
        intent.putExtra("ClientId", "000000001")
        intent.putExtra("Survey", SURVEY_JSON)
        setActivityIntent(intent)
        getActivity()
    }

    /**
     * checks all th questions and answers are displayed correctly
     */
    @MediumTest
    @Throws(InterruptedException::class)
    fun testQuestionAndAnswersDisplayed() {
        for (i in 1..N_QUESTIONS) {

            // check the numbering in the subtitle
            assertEquals(i.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

            // check question
            Espresso.onView(ViewMatchers.withText("This is question $i"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

            // check answers
            Espresso.onView(ViewMatchers.withText("Answer " + i + "a"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withText("Answer " + i + "b"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withText("Answer " + i + "c"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

            // go to the next question
            Espresso.onView(ViewMatchers.withId(R.id.btnNext))
                .perform(ViewActions.click())
        }
    }

    /**
     * answers each question then press next
     * press submit
     */
    @MediumTest
    @Throws(InterruptedException::class)
    fun testAnswerSurvey() {

        // answer all questions
        for (i in 1..N_QUESTIONS) {

            // choose an answer
            Espresso.onView(ViewMatchers.withText("Answer " + i + "a"))
                .perform(ViewActions.click())
                .check(ViewAssertions.matches(ViewMatchers.isChecked()))

            // go to the next question
            Espresso.onView(ViewMatchers.withId(R.id.btnNext))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click())

            // check the numbering in the subtitle
            if (i < N_QUESTIONS) assertEquals(
                (i + 1).toString() + "/" + N_QUESTIONS,
                getActivity().getToolbar().getSubtitle()
            )
        }

        // check we reached the submission page
        assertEquals("Submit Survey", getActivity().getToolbar().getSubtitle())
        Espresso.onView(ViewMatchers.withId(R.id.btn_submit))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())
        Thread.sleep(1000)
    }

    /**
     * swipe right to go to question 2
     * swipe left to go back to question 1
     * swipe right two times to question 3
     */
    fun testSwipeNavigate() {
        assertEquals(1.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

        // to page 2
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeLeft())
        assertEquals(2.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

        // to page 1
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeRight())
        assertEquals(1.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

        // to page 3
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeLeft())
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeLeft())
        assertEquals(3.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())
    }

    /**
     * answers all the questions
     * goes back to each question and check the answers are still checked
     */
    @Throws(InterruptedException::class)
    fun testAnswersPersistent() {
        // answer the first question
        assertEquals(1.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())
        Espresso.onView(ViewMatchers.withText("Answer 1a"))
            .perform(ViewActions.click())

        // go to the second question
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeLeft())
        assertEquals(2.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

        // answer the second question
        Thread.sleep(300)
        Espresso.onView(ViewMatchers.withText("Answer 2b"))
            .perform(ViewActions.click())

        // go to the third question
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeLeft())
        assertEquals(3.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

        // answer the third question
        Thread.sleep(300)
        Espresso.onView(ViewMatchers.withText("Answer 3c"))
            .perform(ViewActions.click())

        // go go back to the first question
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeRight())
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeRight())
        assertEquals(1.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

        // check the answer is checked
        Espresso.onView(ViewMatchers.withText("Answer 1a"))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))


        // go to the second question
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeLeft())
        assertEquals(2.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

        // check the answer is checked
        Espresso.onView(ViewMatchers.withText("Answer 2b"))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        // go to the third question
        Espresso.onView(ViewMatchers.withId(R.id.surveyPager))
            .perform(ViewActions.swipeLeft())
        assertEquals(3.toString() + "/" + N_QUESTIONS, getActivity().getToolbar().getSubtitle())

        // check the answer is checked
        Espresso.onView(ViewMatchers.withText("Answer 3c"))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    companion object {
        /* mock data */
        const val N_QUESTIONS = 3
        const val SURVEY_JSON = "{\n" +
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
                "}"
    }
}