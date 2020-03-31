package com.mifos.mifosxdroid.online.clientidentifiers;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.noncore.Identifier;

import java.util.List;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface ClientIdentifiersMvpView extends MvpView {

    void showUserInterface();

    void showClientIdentifiers(List<Identifier> identifiers);

    void showFetchingError(int errorMessage);

    void identifierDeletedSuccessfully(int position);
}