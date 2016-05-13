package com.mifos.services.data;

import com.mifos.utils.Constants;

/**
 * Created by ahmed on 4/14/2016.
 */
public class SavingsPayloadFactory {

    /**
     * @param id clientId or groupId or centerId
     * @param clientType  client or group or center
     * @return the corresponding SavingsPayload object(ClientSavingsPayload or GroupSavingsPayload)
     */
    public static SavingsPayload createSavingsPayload(int id, Constants.ClientType clientType) {

        switch (clientType) {
            case CLIENT:
                ClientSavingsPayload clientSavingsPayload = new ClientSavingsPayload();
                clientSavingsPayload.setClientId(id);
                return clientSavingsPayload;

            case GROUP:
            case CENTER:
                GroupSavingsPayload groupSavingsPayload = new GroupSavingsPayload();
                groupSavingsPayload.setGroupId(id);
                return groupSavingsPayload;
        };

        return null;
    }
}
