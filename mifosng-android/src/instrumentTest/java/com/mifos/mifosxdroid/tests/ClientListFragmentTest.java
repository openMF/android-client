package com.mifos.mifosxdroid.tests;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.objects.client.Client;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static com.mifos.mifosxdroid.tests.action.NavigationViewActions.navigateTo;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Gabriel Esteban on 12/12/14.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ClientListFragmentTest {

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
    @Rule
    public ActivityTestRule<DashboardActivity> mDashboardActivity =
            new ActivityTestRule<>(DashboardActivity.class);
    private List<Client> clientList = new ArrayList<>();

    /**
     * A custom {@link Matcher} which matches an item in a {@link RecyclerView} by its text.
     * <p/>
     * <p/>
     * View constraints:
     * <ul>
     * <li>View must be a child of a {@link RecyclerView}
     * <ul>
     *
     * @param itemText the text to match
     * @return Matcher that matches text in the given view
     */
    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA RV with text " + itemText);
            }
        };
    }

    /**
     * Convenience method to register an IdlingResources with Espresso. IdlingResource resource is
     * a great way to tell Espresso when your app is in an idle state. This helps Espresso to
     * synchronize your test actions, which makes tests significantly more reliable.
     */
    @Before
    public void registerIdlingResource() {

        Gson gson = new Gson();
        clientList = gson.fromJson(CLIENTLIST_JSON, new TypeToken<List<Client>>() {
        }.getType());
        Log.d("Clients", clientList.get(5).getDisplayName());
        Espresso.registerIdlingResources(
                mDashboardActivity.getActivity().getCountingIdlingResource());
    }

    /**
     * When project is successfully enable any architecture, we have to pass
     * this fake client list data to the view layer and remain will not to change.
     * test case automatically pass.
     *
     * @throws Exception
     */
    @Test
    public void testOpenClientListFragment() throws Exception {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start Transaction of ClientListFragment screen.
        onView(withId(R.id.navigation_view))
                .perform(navigateTo(R.id.item_clients));

        // Scroll notes list to added note, by finding its description
        onView(withId(R.id.rv_clients)).perform(
                scrollTo(hasDescendant(withText(clientList.get(4).getAccountNo()))));
        // Verify note is displayed on screen
        onView(withItemText(clientList.get(4).getAccountNo())).check(matches(isDisplayed()));

        // Scroll notes list to added note, by finding its description
        onView(withId(R.id.rv_clients)).perform(
                scrollTo(hasDescendant(withText(clientList.get(9).getAccountNo()))));
        // Verify note is displayed on screen
        onView(withItemText(clientList.get(9).getAccountNo())).check(matches(isDisplayed()));

    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
                mDashboardActivity.getActivity().getCountingIdlingResource());
    }


}
