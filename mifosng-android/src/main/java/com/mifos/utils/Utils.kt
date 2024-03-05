package com.mifos.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
import com.mifos.mifosxdroid.R
import rx.Observable
import java.text.DateFormat
import java.util.Calendar
import java.util.TimeZone

/**
 * Created by Rajan Maurya on 18/08/16.
 */
object Utils {
    val LOG_TAG = Utils::class.java.simpleName

    /**
     * This Method transforming the PaymentTypeOption to String(PaymentTypeName).
     * In Which Observable.from takes the List<PaymentTypeOptions> and flatmap the
     * PaymentTypeOptions one by one. finally returns the List<String> paymentTypeNames
     *
     * @param paymentTypeOptions List<PaymentTypeOption>
     * @return List<String>
    </String></PaymentTypeOption></String></PaymentTypeOptions> */
    fun getPaymentTypeOptions(paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>?): List<String> {
        val paymentOptions: MutableList<String> = ArrayList()
        Observable.from(paymentTypeOptions)
            .flatMap { (_, name) -> Observable.just(name) }
            .subscribe { s -> paymentOptions.add(s) }
        return paymentOptions
    }

    /**
     * This Method filtering the List<PaymentTypeOption> and if any PaymentTypeOption id is equal to
     * the paymentId. and return the match PaymentType Name.
     *
     * @param paymentId Payment Type Id
     * @param paymentTypeOptions PaymentTypeOptions
     * @return PaymentType Name
    </PaymentTypeOption> */
    @JvmStatic
    fun getPaymentTypeName(
        paymentId: Int,
        paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>?
    ): String? {
        val paymentTypeName = arrayOfNulls<String>(1)
        Observable.from(paymentTypeOptions)
            .filter { (id) -> id == paymentId }
            .subscribe { (_, name) -> paymentTypeName[0] = name }
        return paymentTypeName[0]
    }

    fun getActiveLoanAccounts(loanAccountList: List<LoanAccount>?): List<LoanAccount> {
        val loanAccounts: MutableList<LoanAccount> = ArrayList()
        Observable.from(loanAccountList)
            .filter { loanAccount -> loanAccount.status?.active }
            .subscribe { loanAccount -> loanAccounts.add(loanAccount) }
        return loanAccounts
    }

    fun getActiveSavingsAccounts(savingsAccounts: List<SavingsAccount>?): List<SavingsAccount> {
        val accounts: MutableList<SavingsAccount> = ArrayList()
        Observable.from(savingsAccounts)
            .filter { savingsAccount ->
                savingsAccount.status?.active == true &&
                        !savingsAccount.depositType!!.isRecurring
            }
            .subscribe { savingsAccount -> accounts.add(savingsAccount) }
        return accounts
    }

    fun getActiveClients(clients: List<Client>?): List<Client> {
        val accounts: MutableList<Client> = ArrayList()
        Observable.from(clients)
            .filter { client -> client.active }
            .subscribe { client -> accounts.add(client) }
        return accounts
    }

    fun getSyncableSavingsAccounts(savingsAccounts: List<SavingsAccount>?): List<SavingsAccount> {
        val accounts: MutableList<SavingsAccount> = ArrayList()
        Observable.from(savingsAccounts)
            .filter { savingsAccount ->
                savingsAccount.depositType?.value == "Savings" &&
                        savingsAccount.status?.active == true &&
                        !savingsAccount.depositType!!.isRecurring
            }
            .subscribe { savingsAccount -> accounts.add(savingsAccount) }
        return accounts
    }

    /**
     * This Method Converting the List<Integer> of Activation Date to String.
     *
     * @param dateObj List<Integer> of Date
     * @return
    </Integer></Integer> */
    fun getStringOfDate(dateObj: List<Int>): String {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar[Calendar.YEAR] = dateObj[0]
        //in Calendar months are indexed from 0 to 11
        calendar[Calendar.MONTH] = dateObj[1] - 1
        calendar[Calendar.DAY_OF_MONTH] = dateObj[2]
        val df = DateFormat.getDateInstance(DateFormat.MEDIUM)
        return df.format(calendar.time)
    }

    fun setCircularBackground(
        colorId: Int,
        context: Context?
    ): LayerDrawable {
        val color: Drawable = ColorDrawable(ContextCompat.getColor(context!!, colorId))
        val image = ContextCompat.getDrawable(
            context,
            R.drawable.circular_background
        )
        return LayerDrawable(arrayOf(image, color))
    }
}