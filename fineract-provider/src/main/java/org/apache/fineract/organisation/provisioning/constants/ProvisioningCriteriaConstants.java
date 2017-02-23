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
package org.apache.fineract.organisation.provisioning.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ProvisioningCriteriaConstants {

    public final static String JSON_LOCALE_PARAM = "locale" ;
    
    public final static String JSON_CRITERIAID_PARAM = "criteriaId" ;
    
    public final static String JSON_CRITERIANAME_PARAM = "criteriaName";
    
    public final static String JSON_LOANPRODUCTS_PARAM = "loanProducts";

    public final static String JSON_LOAN_PRODUCT_ID_PARAM = "id" ;
    
    public final static String JSON_LOAN_PRODUCTNAME_PARAM = "name" ;
    
    public final static String JSON_LOAN_PRODUCT_BORROWERCYCLE_PARAM = "includeInBorrowerCycle" ;
    
    public final static String JSON_PROVISIONING_DEFINITIONS_PARAM = "definitions";
    
    public final static String JSON_CATEOGRYID_PARAM = "categoryId";

    public final static String JSON_CATEOGRYNAME_PARAM = "categoryName";
    
    public final static String JSON_MINIMUM_AGE_PARAM = "minAge";

    public final static String JSON_MAXIMUM_AGE_PARAM = "maxAge";

    public final static String JSON_PROVISIONING_PERCENTAGE_PARAM = "provisioningPercentage";
    
    public final static String JSON_LIABILITY_ACCOUNT_PARAM = "liabilityAccount";

    public final static String JSON_EXPENSE_ACCOUNT_PARAM = "expenseAccount";
    
    Set<String> supportedParametersForCreate = new HashSet<>(Arrays.asList(JSON_LOCALE_PARAM, JSON_CRITERIANAME_PARAM,
            JSON_LOANPRODUCTS_PARAM, JSON_PROVISIONING_DEFINITIONS_PARAM));

    Set<String> supportedParametersForUpdate = new HashSet<>(Arrays.asList(JSON_CRITERIAID_PARAM, JSON_LOCALE_PARAM, JSON_CRITERIANAME_PARAM,
            JSON_LOANPRODUCTS_PARAM, JSON_PROVISIONING_DEFINITIONS_PARAM));

    Set<String> loanProductSupportedParams = new HashSet<>(Arrays.asList(JSON_LOAN_PRODUCT_ID_PARAM,
            JSON_LOAN_PRODUCTNAME_PARAM, JSON_LOAN_PRODUCT_BORROWERCYCLE_PARAM)) ;
    
    Set<String> provisioningcriteriaSupportedParams = new HashSet<>(Arrays.asList(JSON_CATEOGRYID_PARAM,
            JSON_CATEOGRYNAME_PARAM, JSON_MINIMUM_AGE_PARAM, JSON_MAXIMUM_AGE_PARAM, JSON_MINIMUM_AGE_PARAM, JSON_PROVISIONING_PERCENTAGE_PARAM, JSON_EXPENSE_ACCOUNT_PARAM, JSON_LIABILITY_ACCOUNT_PARAM)) ;
    
    
    Set<String> PROVISIONING_CRITERIA_TEMPLATE_PARAMETER = new HashSet<>(Arrays.asList("definitions", "loanProducts",
            "glAccounts"));

    Set<String> PROVISIONING_CRITERIA_PARAMETERS = new HashSet<>(Arrays.asList("criteriaName", "loanProducts",
            "definitions"));
    
    Set<String> ALL_PROVISIONING_CRITERIA_PARAMETERS = new HashSet<>(Arrays.asList("criteriaId", "criterianame",
            "createdby"));
    
}
