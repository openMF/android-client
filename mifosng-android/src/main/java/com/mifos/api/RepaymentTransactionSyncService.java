/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.mifos.App;
import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.SaveResponse;
import com.mifos.mifosxdroid.OfflineCenterInputActivity;
import com.mifos.objects.db.AttendanceType;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.Currency;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.MeetingCenter;
import com.mifos.objects.db.MifosGroup;
import com.mifos.objects.db.RepaymentTransaction;
import com.mifos.utils.DateHelper;
import com.mifos.utils.Network;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

public class RepaymentTransactionSyncService {

    private static final String TAG = RepaymentTransactionSyncService.class.getSimpleName();
    private final String tag = getClass().getSimpleName();
    private SyncFinishListener syncFinishListener;
    private long centerId;

    public RepaymentTransactionSyncService(SyncFinishListener syncFinishListener, long centerId) {
        this.syncFinishListener = syncFinishListener;
        this.centerId = centerId;
    }

    public void syncRepayments(Context context) {
        List<RepaymentTransaction> transactions = Select.from(RepaymentTransaction.class).list();
        Log.i(TAG, "Fetching transactions from Database:" + transactions);
        List<MeetingCenter> centerList = Select.from(MeetingCenter.class).where(com.orm.query
                .Condition.prop("center_id").eq(centerId)).list();
        MeetingCenter center = centerList.get(0);
        if (transactions.size() > 0) {
            List<BulkRepaymentTransactions> repaymentTransactions = new
                    ArrayList<BulkRepaymentTransactions>();

            for (RepaymentTransaction transaction : transactions) {
                Loan loan = transaction.getLoan();
                //ToDO need to ask about hard coding
                if (loan.getAccountStatusId() == 300) {
                    repaymentTransactions.add(new BulkRepaymentTransactions(loan.getLoanId(),
                            transaction.getTransactionAmount()));
                }
            }

            BulkRepaymentTransactions[] repaymentTransactionArray = new
                    BulkRepaymentTransactions[repaymentTransactions.size()];
            repaymentTransactions.toArray(repaymentTransactionArray);


            CollectionSheetPayload payload = new CollectionSheetPayload();
            payload.bulkRepaymentTransactions = repaymentTransactionArray;
            payload.setCalendarId(center.getCollectionMeetingCalendar().getCalendarId());
            payload.setTransactionDate(DateHelper.getPayloadDate(context));
            SaveCollectionSheetTask task = new SaveCollectionSheetTask();
            task.execute(payload);
        } else {
            syncFinishListener.onSyncFinish("There is no data to sync", false);
        }

    }

    private void deleteAllOfflineCollectionSheetData() {
        RepaymentTransaction.deleteAll(RepaymentTransaction.class);
        MifosGroup.deleteAll(MifosGroup.class);
        Loan.deleteAll(Loan.class);
        Client.deleteAll(Client.class);
        AttendanceType.deleteAll(AttendanceType.class);
        Currency.deleteAll(Currency.class);
    }

    public interface SyncFinishListener {
        public void onSyncFinish(String message, boolean isSyncable);
    }

    private class SaveCollectionSheetTask extends AsyncTask<CollectionSheetPayload, Void, Void> {
        private boolean isResponseNull = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(CollectionSheetPayload... collectionSheetPayloads) {

            if (Network.isOnline(App.getContext())) {
                try {
                    SharedPreferences preferences = App.getContext().getSharedPreferences
                            (OfflineCenterInputActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
                    SaveResponse response = App.apiManager.saveCollectionSheet((int) centerId,
                            collectionSheetPayloads[0]);
                    if (response != null) {
                        Log.i(TAG, "saveCollectionSheet - Response:" + response.toString());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                    } else {
                        isResponseNull = true;
                    }

                } catch (RetrofitError error) {
                    isResponseNull = true;
                } catch (Exception ex) {
                    isResponseNull = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isResponseNull) {
                syncFinishListener.onSyncFinish("Sync is completed successfully", true);
            } else {
                syncFinishListener.onSyncFinish("Couldn't sync the data, please try again", true);
            }

        }
    }
}
