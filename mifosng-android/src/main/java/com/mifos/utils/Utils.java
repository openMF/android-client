package com.mifos.utils;

import com.mifos.objects.PaymentTypeOption;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Rajan Maurya on 18/08/16.
 */
public class Utils {


    public static List<String> getPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        final List<String> paymentOptions = new ArrayList<>();
        Observable.from(paymentTypeOptions)
                .flatMap(new Func1<PaymentTypeOption, Observable<String>>() {
                    @Override
                    public Observable<String> call(PaymentTypeOption paymentTypeOption) {
                        return Observable.just(paymentTypeOption.getName());
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        paymentOptions.add(s);
                    }
                });
        return paymentOptions;
    }
}
