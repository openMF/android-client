package com.mifos.mifosxdroid.tests

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.R
import com.mifos.objects.client.Client
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test

/**
 * Created by Gabriel Esteban on 12/12/14.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ClientListFragmentTest constructor() {
    @Rule
    var mDashboardActivity: ActivityTestRule<DashboardActivity> =
        ActivityTestRule<DashboardActivity>(
            DashboardActivity::class.java
        )
    private var clientList: MutableList<Client> = ArrayList()

    /**
     * A custom [Matcher] which matches an item in a [RecyclerView] by its text.
     *
     *
     *
     *
     * View constraints:
     *
     *  * View must be a child of a [RecyclerView]
     *
     *
     * @param itemText the text to match
     * @return Matcher that matches text in the given view
     */
    private fun withItemText(itemText: String?): Matcher<View> {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty")
        return object : TypeSafeMatcher<View?>() {
            public override fun matchesSafely(item: View?): Boolean {
                return Matchers.allOf<View>(
                    ViewMatchers.isDescendantOfA(ViewMatchers.isAssignableFrom(RecyclerView::class.java)),
                    ViewMatchers.withText(itemText)
                ).matches(item)
            }

            public override fun describeTo(description: Description) {
                description.appendText("is isDescendantOfA RV with text " + itemText)
            }
        }
    }

    /**
     * Convenience method to register an IdlingResources with Espresso. IdlingResource resource is
     * a great way to tell Espresso when your app is in an idle state. This helps Espresso to
     * synchronize your test actions, which makes tests significantly more reliable.
     */
    @Before
    fun registerIdlingResource() {
        val gson: Gson = Gson()
        clientList =
            gson.fromJson(CLIENTLIST_JSON, object : TypeToken<MutableList<Client?>?>() {}.getType())
        Log.d("Clients", clientList.get(5).displayName)
        Espresso.registerIdlingResources(
            mDashboardActivity.getActivity().getCountingIdlingResource()
        )
    }

    /**
     * When project is successfully enable any architecture, we have to pass
     * this fake client list data to the view layer and remain will not to change.
     * test case automatically pass.
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun testOpenClientListFragment() {
        // Open Drawer to click on navigation.
        Espresso.onView(ViewMatchers.withId(R.id.drawer))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        // Start Transaction of ClientListFragment screen.
        Espresso.onView(ViewMatchers.withId(R.id.navigation_view))
            .perform(navigateTo(R.id.item_clients))

        // Scroll notes list to added note, by finding its description
        Espresso.onView(ViewMatchers.withId(R.id.rv_clients)).perform(
            RecyclerViewActions.scrollTo(
                ViewMatchers.hasDescendant(
                    ViewMatchers.withText(
                        clientList.get(
                            4
                        ).accountNo
                    )
                )
            )
        )
        // Verify note is displayed on screen
        Espresso.onView(withItemText(clientList.get(4).accountNo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Scroll notes list to added note, by finding its description
        Espresso.onView(ViewMatchers.withId(R.id.rv_clients)).perform(
            RecyclerViewActions.scrollTo(
                ViewMatchers.hasDescendant(
                    ViewMatchers.withText(
                        clientList.get(
                            9
                        ).accountNo
                    )
                )
            )
        )
        // Verify note is displayed on screen
        Espresso.onView(withItemText(clientList.get(9).accountNo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
            mDashboardActivity.getActivity().getCountingIdlingResource()
        )
    }

    companion object {
        val CLIENTLIST_JSON: String = ("[\n" +
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
                "  ]")
    }
}