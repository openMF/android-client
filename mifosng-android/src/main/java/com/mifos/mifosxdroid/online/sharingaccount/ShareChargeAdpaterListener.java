package com.mifos.mifosxdroid.online.sharingaccount;

/**
 * Created by mayankjindal on 23/08/17.
 */

public interface ShareChargeAdpaterListener {

    void removeChargeItem(int position);

    void editAmount(int position, Double value);
}