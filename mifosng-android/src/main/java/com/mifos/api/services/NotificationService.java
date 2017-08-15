package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.NotificationTemplate;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by mayankjindal on 14/08/17.
 */

public interface NotificationService {

    @GET(APIEndPoint.NOTIFICATION)
    Observable<NotificationTemplate> getNotification();
}
