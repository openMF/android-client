package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperCharge;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.utils.PrefManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * This DataManager is for Managing Charge API, In which Request is going to Server
 * and In Response, We are getting Charge API Observable Response using Retrofit2.
 * DataManagerCharge saving response in Database and response to Presenter as accordingly.
 * <p/>
 * Created by Rajan Maurya on 4/7/16.
 */
@Singleton
public class DataManagerCharge {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperCharge mDatabaseHelperCharge;

    @Inject
    public DataManagerCharge(BaseApiManager baseApiManager,
                             DatabaseHelperCharge databaseHelperCharge) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperCharge = databaseHelperCharge;
    }


    /**
     * This Method Request the Charge API at
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/charges
     * and in response get the of the Charge Page that contains Charges list.
     *
     * @param clientId Client Id
     * @param offset   Offset From Which Position Charge List user want
     * @param limit    Maximum Limit of the Response Charge List Size
     * @return Page<Charge> Page of Charge in Which List Size is according to Limit and from
     * where position is Starting according to offset</>
     */
    public Observable<Page<Charges>> getClientCharges(final int clientId, int offset, int limit) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getChargeApi().getListOfCharges(clientId, offset, limit)
                        .concatMap(new Func1<Page<Charges>, Observable<? extends Page<Charges>>>() {
                            @Override
                            public Observable<? extends Page<Charges>> call(Page<Charges>
                                                                                    chargesPage) {
                                mDatabaseHelperCharge.saveClientCharges(chargesPage, clientId);
                                return Observable.just(chargesPage);
                            }
                        });

            case 1:
                /**
                 * Return Client Charges from DatabaseHelperClient only one time.
                 */
                if (offset == 0)
                    return mDatabaseHelperCharge.readClientCharges(clientId);

            default:
                return Observable.just(new Page<Charges>());
        }

    }

}
