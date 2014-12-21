package com.mifos.mifosxdroid.tests;

import android.view.View;
import android.test.ViewAsserts;
import android.widget.ListView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.LoanActivity;
import com.mifos.mifosxdroid.fragments.LoanFragment;

/**
 * Created by Stephen on 12/17/2014.
 */
public class LoanFragmentTest extends ActivityInstrumentationTestCase2<LoanActivity>{

    LoanActivity loanActivity;
    LoanFragment loanFragment;
    ListView listView;

    public LoanFragmentTest(){ super(LoanActivity.class); }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        loanActivity = getActivity();

        loanFragment = (LoanFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.global_container);
        listView = (ListView) getActivity().findViewById(R.id.lv_loan);

    }

    @SmallTest
    public void testFragmentNotNull()
    {
        assertNotNull(loanFragment);
    }

    @SmallTest
    public void listViewNotNull()
    {
        assertNotNull(listView);
    }

    @SmallTest
    public void testListViewVisible()
    {
        assertEquals(View.VISIBLE, listView.getVisibility());
    }

}