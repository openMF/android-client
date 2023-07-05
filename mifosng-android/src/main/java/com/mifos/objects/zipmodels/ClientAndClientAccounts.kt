package com.mifos.objects.zipmodels

import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.client.Client

/**
 * Model for Observable.zip. This Model used to combine the Client and ClientAccount in response
 * of RxAndroid.
 * Created by Rajan Maurya on 01/07/16.
 */
class ClientAndClientAccounts {
    var client: Client? = null
    var clientAccounts: ClientAccounts? = null
}