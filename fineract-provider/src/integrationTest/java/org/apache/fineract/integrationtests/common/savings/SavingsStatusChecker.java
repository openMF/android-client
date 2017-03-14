/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.integrationtests.common.savings;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.fineract.integrationtests.common.Utils;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings("rawtypes")
public class SavingsStatusChecker {
    
    private static final String SAVINGS_ACCOUNT_URL = "/fineract-provider/api/v1/savingsaccounts";

    public static void verifySavingsIsApproved(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS APPROVED ------------------------------------");
        assertTrue("ERROR IN APPROVING SAVINGS APPLICATION", getStatus(savingsStatusHashMap, "approved"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static void verifySavingsIsPending(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS PENDING ------------------------------------");
        assertTrue("SAVINGS ACCOUNT IS NOT IN PENDING STATE", getStatus(savingsStatusHashMap, "submittedAndPendingApproval"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static void verifySavingsIsActive(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS ACTIVE ------------------------------------");
        assertTrue("ERROR IN ACTIVATING THE SAVINGS APPLICATION", getStatus(savingsStatusHashMap, "active"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }
    
    public static void verifySavingsIsRejected(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS REJECTED ------------------------------------");
        assertTrue("ERROR IN REJECTING THE SAVINGS APPLICATION", getStatus(savingsStatusHashMap, "rejected"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }
    
    public static void verifySavingsIsWithdrawn(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS WITHDRAWN ------------------------------------");
        assertTrue("ERROR IN WITHDRAW  THE SAVINGS APPLICATION", getStatus(savingsStatusHashMap, "withdrawnByApplicant"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }
    
    public static void verifySavingsAccountIsClosed(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS CLOSED ------------------------------------");
        assertTrue("ERROR IN CLOSING THE SAVINGS APPLICATION", getStatus(savingsStatusHashMap, "closed"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static void verifySavingsAccountIsNotActive(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS INACTIVE ------------------------------------");
        assertTrue(getStatus(savingsStatusHashMap, "active"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static HashMap getStatusOfSavings(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer savingsID) {
        final String url = SAVINGS_ACCOUNT_URL+"/" + savingsID + "?"+Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, url, "status");
    }

    private static boolean getStatus(final HashMap savingsStatusMap, final String nameOfSavingsStatusString) {
        return (Boolean) savingsStatusMap.get(nameOfSavingsStatusString);
    }
}