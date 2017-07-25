package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.twofactor.AccessToken;
import com.mifos.objects.twofactor.DeliveryMethod;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface TwoFactorService {

    @GET(APIEndPoint.TWOFACTOR)
    Observable<List<DeliveryMethod>> getDeliveryMethods();

    @POST(APIEndPoint.TWOFACTOR)
    Observable<String> requestOTP(@Query("deliveryMethod") String deliveryMethod);

    @POST(APIEndPoint.TWOFACTOR + "/validate")
    Observable<AccessToken> validateToken(@Query("token") String token);
}
