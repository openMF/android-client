package com.mifos.mifosxdroid.dialogfragments.chargedialogfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Charges;

import java.util.List;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface ChargeDialogMvpView extends MvpView {

    void showAllCharges(List<Charges> charges);

    void ResponseError(String s);

    void createchargesRsult(Charges charges);


}
