/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientIdentifiersfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.noncore.Identifier;

import java.util.List;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public interface ClientIdentifiersMvpView extends MvpView {

    void showIdentifiers(List<Identifier> identifiers);

    void showIdentifierFetchError();

    void showIdentifierProgressBar(boolean status);
}
