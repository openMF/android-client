package com.mifos.mifosxdroid.online.clientidentifiers;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.noncore.Identifier;

import java.util.List;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface ClientIdentifiersMvpView extends MvpView {

    void showClientIdentifiers(List<Identifier> identifiers);

    void showFetchingError(String s);

    void onClickRemoveIdentifier(int identifierId, int position);

    void identifierDeletedSuccessfully(String s, int position);
}