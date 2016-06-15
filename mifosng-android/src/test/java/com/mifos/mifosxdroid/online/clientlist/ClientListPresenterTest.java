package com.mifos.mifosxdroid.online.clientlist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rajan Maurya on 15/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientListPresenterTest {

    //TODO Shift Fake Data in
    public static final String CLIENTLIST_JSON = "[\n" +
            "    {\n" +
            "      \"id\": 1,\n" +
            "      \"accountNo\": \"000000001\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2016,\n" +
            "        3,\n" +
            "        28\n" +
            "      ],\n" +
            "      \"firstname\": \"Smith\",\n" +
            "      \"lastname\": \"R\",\n" +
            "      \"displayName\": \"Smith R\",\n" +
            "      \"gender\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"transferToOfficeId\": 11,\n" +
            "      \"transferToOfficeName\": \"Pooja_EDI_2015\",\n" +
            "      \"imageId\": 36,\n" +
            "      \"imagePresent\": true,\n" +
            "      \"staffId\": 1,\n" +
            "      \"staffName\": \"BB, AliyaBhath\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2010,\n" +
            "          1,\n" +
            "          1\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2016,\n" +
            "          3,\n" +
            "          28\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 9,\n" +
            "      \"accountNo\": \"000000009\",\n" +
            "      \"externalId\": \"1700512345\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2015,\n" +
            "        12,\n" +
            "        18\n" +
            "      ],\n" +
            "      \"firstname\": \"sun\",\n" +
            "      \"lastname\": \"por\",\n" +
            "      \"displayName\": \"sun por\",\n" +
            "      \"mobileNo\": \"123456789\",\n" +
            "      \"dateOfBirth\": [\n" +
            "        1990,\n" +
            "        8,\n" +
            "        25\n" +
            "      ],\n" +
            "      \"gender\": {\n" +
            "        \"id\": 22,\n" +
            "        \"name\": \"Male\",\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"staffId\": 1,\n" +
            "      \"staffName\": \"BB, AliyaBhath\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2014,\n" +
            "          1,\n" +
            "          1\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          18\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"savingsAccountId\": 1,\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 10,\n" +
            "      \"accountNo\": \"000000010\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2015,\n" +
            "        11,\n" +
            "        2\n" +
            "      ],\n" +
            "      \"firstname\": \"JAVA\",\n" +
            "      \"lastname\": \"BOY\",\n" +
            "      \"displayName\": \"JAVA BOY\",\n" +
            "      \"gender\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2015,\n" +
            "          11,\n" +
            "          2\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2015,\n" +
            "          11,\n" +
            "          2\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 11,\n" +
            "      \"accountNo\": \"000000011\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2015,\n" +
            "        12,\n" +
            "        18\n" +
            "      ],\n" +
            "      \"firstname\": \"KILLER\",\n" +
            "      \"lastname\": \"ZONE\",\n" +
            "      \"displayName\": \"KILLER ZONE\",\n" +
            "      \"gender\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          18\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          18\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 12,\n" +
            "      \"accountNo\": \"000000012\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2015,\n" +
            "        11,\n" +
            "        1\n" +
            "      ],\n" +
            "      \"firstname\": \"PERL\",\n" +
            "      \"lastname\": \"GERM\",\n" +
            "      \"displayName\": \"PERL GERM\",\n" +
            "      \"gender\": {\n" +
            "        \"id\": 22,\n" +
            "        \"name\": \"Male\",\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2015,\n" +
            "          11,\n" +
            "          1\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2015,\n" +
            "          11,\n" +
            "          1\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 13,\n" +
            "      \"accountNo\": \"000000013\",\n" +
            "      \"externalId\": \"267777\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2015,\n" +
            "        12,\n" +
            "        18\n" +
            "      ],\n" +
            "      \"firstname\": \"John\",\n" +
            "      \"middlename\": \"Olu\",\n" +
            "      \"lastname\": \"Olu\",\n" +
            "      \"displayName\": \"John Olu Olu\",\n" +
            "      \"mobileNo\": \"233264500099\",\n" +
            "      \"dateOfBirth\": [\n" +
            "        2000,\n" +
            "        11,\n" +
            "        30\n" +
            "      ],\n" +
            "      \"gender\": {\n" +
            "        \"id\": 22,\n" +
            "        \"name\": \"Male\",\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          18\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          18\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"savingsAccountId\": 4,\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 17,\n" +
            "      \"accountNo\": \"000000017\",\n" +
            "      \"externalId\": \"515096660\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2015,\n" +
            "        12,\n" +
            "        21\n" +
            "      ],\n" +
            "      \"firstname\": \"Okeleke\",\n" +
            "      \"middlename\": \"N\",\n" +
            "      \"lastname\": \"Mike\",\n" +
            "      \"displayName\": \"Okeleke N Mike\",\n" +
            "      \"mobileNo\": \"08035889650\",\n" +
            "      \"dateOfBirth\": [\n" +
            "        1975,\n" +
            "        12,\n" +
            "        7\n" +
            "      ],\n" +
            "      \"gender\": {\n" +
            "        \"id\": 22,\n" +
            "        \"name\": \"Male\",\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"imageId\": 3,\n" +
            "      \"imagePresent\": true,\n" +
            "      \"staffId\": 1,\n" +
            "      \"staffName\": \"BB, AliyaBhath\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          21\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          21\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"savingsAccountId\": 8,\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 20,\n" +
            "      \"accountNo\": \"000000020\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2016,\n" +
            "        3,\n" +
            "        28\n" +
            "      ],\n" +
            "      \"firstname\": \"Mosha\",\n" +
            "      \"lastname\": \"Pinto\",\n" +
            "      \"displayName\": \"Mosha Pinto\",\n" +
            "      \"dateOfBirth\": [\n" +
            "        1988,\n" +
            "        1,\n" +
            "        8\n" +
            "      ],\n" +
            "      \"gender\": {\n" +
            "        \"id\": 24,\n" +
            "        \"name\": \"Female\",\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          21\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2016,\n" +
            "          3,\n" +
            "          28\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 24,\n" +
            "      \"accountNo\": \"000000024\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 304,\n" +
            "        \"code\": \"clientStatusType.transfer.on.hold\",\n" +
            "        \"value\": \"Transfer on hold\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": false,\n" +
            "      \"activationDate\": [\n" +
            "        2009,\n" +
            "        1,\n" +
            "        4\n" +
            "      ],\n" +
            "      \"firstname\": \"ADi\",\n" +
            "      \"lastname\": \"Yst\",\n" +
            "      \"displayName\": \"ADi Yst\",\n" +
            "      \"gender\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"id\": 47,\n" +
            "        \"name\": \"SSB\",\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"staffId\": 1,\n" +
            "      \"staffName\": \"BB, AliyaBhath\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2009,\n" +
            "          1,\n" +
            "          4\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2009,\n" +
            "          1,\n" +
            "          4\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"savingsAccountId\": 26,\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 38,\n" +
            "      \"accountNo\": \"000000038\",\n" +
            "      \"externalId\": \"HMT0000001\",\n" +
            "      \"status\": {\n" +
            "        \"id\": 300,\n" +
            "        \"code\": \"clientStatusType.active\",\n" +
            "        \"value\": \"Active\"\n" +
            "      },\n" +
            "      \"subStatus\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"active\": true,\n" +
            "      \"activationDate\": [\n" +
            "        2015,\n" +
            "        12,\n" +
            "        24\n" +
            "      ],\n" +
            "      \"firstname\": \"Tejas\",\n" +
            "      \"middlename\": \"Ram\",\n" +
            "      \"lastname\": \"Vyas\",\n" +
            "      \"displayName\": \"Tejas Ram Vyas\",\n" +
            "      \"mobileNo\": \"7600330857\",\n" +
            "      \"dateOfBirth\": [\n" +
            "        1991,\n" +
            "        4,\n" +
            "        26\n" +
            "      ],\n" +
            "      \"gender\": {\n" +
            "        \"id\": 22,\n" +
            "        \"name\": \"Male\",\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientType\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"clientClassification\": {\n" +
            "        \"isActive\": false\n" +
            "      },\n" +
            "      \"officeId\": 1,\n" +
            "      \"officeName\": \"Head Office\",\n" +
            "      \"timeline\": {\n" +
            "        \"submittedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          24\n" +
            "        ],\n" +
            "        \"submittedByUsername\": \"mifos\",\n" +
            "        \"submittedByFirstname\": \"App\",\n" +
            "        \"submittedByLastname\": \"Administrator\",\n" +
            "        \"activatedOnDate\": [\n" +
            "          2015,\n" +
            "          12,\n" +
            "          24\n" +
            "        ],\n" +
            "        \"activatedByUsername\": \"mifos\",\n" +
            "        \"activatedByFirstname\": \"App\",\n" +
            "        \"activatedByLastname\": \"Administrator\"\n" +
            "      },\n" +
            "      \"savingsAccountId\": 13,\n" +
            "      \"clientNonPersonDetails\": {\n" +
            "        \"constitution\": {\n" +
            "          \"isActive\": false\n" +
            "        },\n" +
            "        \"mainBusinessLine\": {\n" +
            "          \"isActive\": false\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]";


    ClientListPresenter clientListPresenter;
    @Mock DataManager mDataManager;
    @Mock ClientListMvpView mClientListMvpView;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        clientListPresenter = new ClientListPresenter(mDataManager);
        clientListPresenter.attachView(mClientListMvpView);
    }


    @Test
    public void testLoadClients() throws Exception {

        Gson gson = new Gson();
        List<Client> clientList = gson.fromJson(CLIENTLIST_JSON, new TypeToken<List<Client>>() {
        }.getType());
        Page<Client> clientPage = new Page<>();
        clientPage.setPageItems(clientList);

        when(mDataManager.getAllClients()).thenReturn(Observable.just(clientPage));

        clientListPresenter.loadClients();

        verify(mClientListMvpView).showClientList(clientPage);


    }

    @Test(expected = NullPointerException.class)
    public void testLoadMoreClients() throws Exception {

        clientListPresenter.loadClients();
    }

    @After
    public void tearDown() {
        clientListPresenter.detachView();
    }
}