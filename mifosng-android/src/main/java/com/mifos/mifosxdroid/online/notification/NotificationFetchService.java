package com.mifos.mifosxdroid.online.notification;

import android.app.IntentService;
import android.content.Intent;

import rx.Subscription;

/**
 * Created by mayankjindal on 14/08/17.
 */

public class NotificationFetchService extends IntentService {

    //private final DataManager mDataManager;
    private Subscription mSubscription;


    public NotificationFetchService() {
        super("NotificationFetchService");
    }

//    @Inject
//    public NotificationFetchService(String name,
//                                    DataManager dataManager) {
//        super(name);
//        mDataManager = dataManager;
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fetchNotification();
    }

    public void fetchNotification() {
//        mSubscription = mDataManager.getNotification()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<NotificationTemplate>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onNext(NotificationTemplate notificationTemplate) {
//                        NotificationUtil.createNotification(App.getContext(),
//                          notificationTemplate);
//                    }
//                });
    }
}
