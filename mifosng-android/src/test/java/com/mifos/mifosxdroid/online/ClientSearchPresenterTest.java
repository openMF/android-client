package com.mifos.mifosxdroid.online;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.online.clientsearch.ClientSearchMvpView;
import com.mifos.mifosxdroid.online.clientsearch.ClientSearchPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Created by Rajan Maurya on 17/6/16.
 */
public class ClientSearchPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    ClientSearchPresenter mClientSearchPresenter;

    @Mock
    DataManager mDataManager;

    @Mock
    ClientSearchMvpView mClientSearchMvpView;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSearchClients() throws Exception {

    }
}