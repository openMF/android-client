package com.mifos.mifosxdroid.login

import com.mifos.api.datamanager.DataManagerAuth
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.user.User
import com.mifos.utils.MFErrorParser
import retrofit2.adapter.rxjava.HttpException
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 4/6/16.
 */
class LoginPresenter @Inject constructor(private val dataManagerAuth: DataManagerAuth) :
    BasePresenter<LoginMvpView?>() {
    private var subscription: Subscription? = null
    override fun attachView(mvpView: LoginMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        if (subscription != null) subscription!!.unsubscribe()
    }

    fun login(username: String?, password: String?) {
        mvpView!!.showProgressbar(true)
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
        subscription = dataManagerAuth.login(username, password)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe(object : Subscriber<User>() {
                override fun onCompleted() {
                    mvpView!!.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
                    mvpView!!.showProgressbar(false)
                    val errorMessage: String
                    try {
                        if (e is HttpException) {
                            errorMessage = e.response().errorBody().string()
                            mvpView!!.onLoginError(
                                MFErrorParser.parseError(errorMessage)
                                    .developerMessage
                            )
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(throwable)
                    }
                }

                override fun onNext(user: User) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.onLoginSuccessful(user)
                }
            })
    }
}