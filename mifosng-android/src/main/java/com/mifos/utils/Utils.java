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


    /**
     * This Method transforming the PaymentTypeOption to String(PaymentTypeName).
     * In Which Observable.from takes the List<PaymentTypeOptions> and flatmap the
     * PaymentTypeOptions one by one. finally returns the List<String> paymentTypeNames
     *
     * @param paymentTypeOptions List<PaymentTypeOption>
     * @return List<String>
     */
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

    /**
     * This Method filtering the List<PaymentTypeOption> and if any PaymentTypeOption id is equal to
     * the paymentId. and return the match PaymentType Name.
     *
     * @param paymentId Payment Type Id
     * @param paymentTypeOptions PaymentTypeOptions
     * @return PaymentType Name
     */
    public static String getPaymentTypeName(final int paymentId,
                                     List<PaymentTypeOption> paymentTypeOptions) {
        final String[] paymentTypeName = new String[1];
        Observable.from(paymentTypeOptions)
                .filter(new Func1<PaymentTypeOption, Boolean>() {
                    @Override
                    public Boolean call(PaymentTypeOption paymentTypeOption) {
                        return (paymentTypeOption.getId() == paymentId);
                    }
                })
                .subscribe(new Action1<PaymentTypeOption>() {
                    @Override
                    public void call(PaymentTypeOption paymentTypeOption) {
                        paymentTypeName[0] = paymentTypeOption.getName();
                    }
                });
        return paymentTypeName[0];
    }
}
