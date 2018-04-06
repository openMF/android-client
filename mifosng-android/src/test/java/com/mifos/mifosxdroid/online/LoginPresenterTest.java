package com.mifos.mifosxdroid.online;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mifos.api.datamanager.DataManagerAuth;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.login.LoginMvpView;
import com.mifos.mifosxdroid.login.LoginPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.user.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

/**
 * Created by mohak on 5/4/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    LoginPresenter mLoginPresenter;

    @Mock
    DataManagerAuth mDataManagerAuth;

    @Mock
    LoginMvpView mLoginMvpView;

    User user;

    @Before
    public void setUp() throws Exception {

        mLoginPresenter = new LoginPresenter(mDataManagerAuth);
        mLoginPresenter.attachView(mLoginMvpView);

        user = FakeRemoteDataSource.getUser();
    }

    @Test
    public void testLogin() throws Exception {

        when(mDataManagerAuth.login("mifos", "password")).thenReturn(Observable.just(user));

        mLoginPresenter.login("mifos", "password");
        verify(mLoginMvpView).showProgressbar(false);
        verify(mLoginMvpView).onLoginSuccessful(user);
    }

    @Test
    public void testErrorLogin() throws Exception {

        HttpException exception = new HttpException(Response.error(401,
                ResponseBody.create(MediaType.parse("application/json"),
                        "Invalid authentication details were passed in api request.")));
        
        when(mDataManagerAuth.login("mifoss", "password"))
                .thenReturn(Observable.<User>error(exception));

        mLoginPresenter.login("mifoss", "password");

        verify(mLoginMvpView).showProgressbar(false);
        verify(mLoginMvpView, never()).onLoginSuccessful(user);

    }

    @After
    public void tearDown() throws Exception {
        mLoginPresenter.detachView();
    }
}


