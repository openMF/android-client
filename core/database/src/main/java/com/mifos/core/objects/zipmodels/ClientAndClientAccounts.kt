package com.mifos.core.objects.zipmodels

import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.client.Client

/**
 * Model for Observable.zip. This Model used to combine the Client and ClientAccount in response
 * of RxAndroid.
 * Created by Rajan Maurya on 01/07/16.
 */
class ClientAndClientAccounts {
    var client: Client? = null
    var clientAccounts: ClientAccounts? = null
}