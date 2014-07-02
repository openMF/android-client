package com.mifos.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.mifos.mifosxdroid.CenterDetailsActivity;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.RepaymentTransaction;
import com.mifos.services.data.BulkRepaymentTransactions;
import com.mifos.services.data.CollectionSheetPayload;
import com.mifos.services.data.SaveResponse;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.orm.query.Select;
import retrofit.RetrofitError;

import java.util.ArrayList;
import java.util.List;

public class RepaymentTransactionSyncService {

    private static final String TAG = RepaymentTransactionSyncService.class.getSimpleName();
    private SyncFinishListener syncFinishListener;

    public RepaymentTransactionSyncService(SyncFinishListener syncFinishListener) {
        this.syncFinishListener = syncFinishListener;
    }

    public void syncRepayments() {
        List<RepaymentTransaction> transactions = Select.from(RepaymentTransaction.class).list();
        Log.i(TAG, "Fetching transactions from Database:" + transactions);

        if (transactions.size() > 0) {
            List<BulkRepaymentTransactions> repaymentTransactions = new ArrayList<BulkRepaymentTransactions>();

            for (RepaymentTransaction transaction : transactions) {
                Loan loan = transaction.getLoan();
                if (loan.getAccountStatusId() == 300) {
                    repaymentTransactions.add(new BulkRepaymentTransactions(loan.getLoanId(), transaction.getTransactionAmount()));
                }
            }

            BulkRepaymentTransactions[] repaymentTransactionArray = new BulkRepaymentTransactions[repaymentTransactions.size()];
            repaymentTransactions.toArray(repaymentTransactionArray);


            CollectionSheetPayload payload = new CollectionSheetPayload();
            payload.bulkRepaymentTransactions = repaymentTransactionArray;

            SaveCollectionSheetTask task = new SaveCollectionSheetTask();
            task.execute(payload);
        } else
            syncFinishListener.onSyncFinish("There is no data to sync");
    }

    public interface SyncFinishListener {
        public void onSyncFinish(String message);
    }

    private class SaveCollectionSheetTask extends AsyncTask<CollectionSheetPayload, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(CollectionSheetPayload... collectionSheetPayloads) {

            if (Network.isOnline(Constants.applicationContext)) {
                try {
                    SharedPreferences preferences = Constants.applicationContext.getSharedPreferences(CenterDetailsActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
                    int centerId = preferences.getInt(CenterDetailsActivity.CENTER_ID_KEY, -1);
                    SaveResponse response = API.centerService.saveCollectionSheet(centerId, collectionSheetPayloads[0]);
                    if (response != null) {
                        Log.i(TAG, "saveCollectionSheet - Response:" + response.toString());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        RepaymentTransaction.deleteAll(RepaymentTransaction.class);
                    }
                } catch (RetrofitError error) {

                } catch (Exception ex) {

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            syncFinishListener.onSyncFinish("Sync is completed successfully");
        }
    }
}
