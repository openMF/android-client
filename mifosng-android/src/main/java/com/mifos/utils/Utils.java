package com.mifos.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.client.Client;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * Created by Rajan Maurya on 18/08/16.
 */
public class Utils {

    public static final String LOG_TAG = Utils.class.getSimpleName();

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
        Observable.fromIterable(paymentTypeOptions)
                .flatMap(new Function<PaymentTypeOption, ObservableSource<?>>() {

                    @Override
                    public Observable<String> apply(PaymentTypeOption paymentTypeOption) {
                        return Observable.just(paymentTypeOption.getName());
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

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
        Observable.fromIterable(paymentTypeOptions)
                .filter(new Predicate<PaymentTypeOption>() {
                    @Override
                    public boolean test(PaymentTypeOption paymentTypeOption) throws Exception {
                        return (paymentTypeOption.getId() == paymentId);
                    }
                })
                .subscribe(new Consumer<PaymentTypeOption>() {
                    @Override
                    public void accept(PaymentTypeOption paymentTypeOption) {
                        paymentTypeName[0] = paymentTypeOption.getName();
                    }
                });
        return paymentTypeName[0];
    }

    public static List<LoanAccount> getActiveLoanAccounts(List<LoanAccount> loanAccountList) {
        final List<LoanAccount> loanAccounts = new ArrayList<>();
        Observable.fromIterable(loanAccountList)
                .filter(new Predicate<LoanAccount>() {
                    @Override
                    public boolean test(LoanAccount loanAccount) throws Exception {
                        return loanAccount.getStatus().getActive();
                    }
                })
                .subscribe(new Consumer<LoanAccount>() {
                    @Override
                    public void accept(LoanAccount loanAccount) throws Exception {
                        loanAccounts.add(loanAccount);
                    }
                });
        return loanAccounts;
    }

    public static List<SavingsAccount> getActiveSavingsAccounts(List<SavingsAccount>
                                                                        savingsAccounts) {
        final List<SavingsAccount> accounts = new ArrayList<>();
        Observable.fromIterable(savingsAccounts)
                .filter(new Predicate<SavingsAccount>() {
                    @Override
                    public boolean test(SavingsAccount savingsAccount) throws Exception {
                        return (savingsAccount.getStatus().getActive() &&
                                !savingsAccount.isRecurring());
                    }
                })
                .subscribe(new Consumer<SavingsAccount>() {
                    @Override
                    public void accept(SavingsAccount savingsAccount) {
                        accounts.add(savingsAccount)
                        ;
                    }
                });
        return accounts;
    }

    public static List<Client> getActiveClients(List<Client> clients) {
        final List<Client> accounts = new ArrayList<>();
        Observable.fromIterable(clients)
                .filter(new Predicate<Client>() {
                    @Override
                    public boolean test(Client client) throws Exception {
                        return (client.isActive());
                    }
                })
                .subscribe(new Consumer<Client>() {
                    @Override
                    public void accept(Client client) throws Exception {
                        accounts.add(client);
                    }
                });
        return accounts;
    }

    public static List<SavingsAccount> getSyncableSavingsAccounts(List<SavingsAccount>
                                                                        savingsAccounts) {
        final List<SavingsAccount> accounts = new ArrayList<>();
        Observable.fromIterable(savingsAccounts)
                .filter(new Predicate<SavingsAccount>() {
                    @Override
                    public boolean test(SavingsAccount savingsAccount) throws Exception {
                        return (savingsAccount.getDepositType().getValue().equals("Savings") &&
                                savingsAccount.getStatus().getActive() &&
                                !savingsAccount.isRecurring());
                    }
                })
                .subscribe(new Consumer<SavingsAccount>() {
                    @Override
                    public void accept(SavingsAccount savingsAccount) throws Exception {
                        accounts.add(savingsAccount)
                        ;
                    }
                });
        return accounts;
    }

    /**
     * This Method Converting the List<Integer> of Activation Date to String.
     *
     * @param dateObj List<Integer> of Date
     * @return
     */
    public static String getStringOfDate(List<Integer> dateObj) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, dateObj.get(0));
        //in Calendar months are indexed from 0 to 11
        calendar.set(Calendar.MONTH, dateObj.get(1) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dateObj.get(2));

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return df.format(calendar.getTime());
    }

    public static LayerDrawable setCircularBackground(int colorId, Context context) {
        Drawable color = new ColorDrawable(ContextCompat.getColor(context, colorId));
        Drawable image = ContextCompat.getDrawable(context, R.drawable.circular_background);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{image, color});
        return ld;
    }
}
