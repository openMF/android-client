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
package org.apache.fineract.organisation.teller.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.teller.domain.CashierTxnType;

public final class CashierTransactionData implements Serializable {

    private final Long id;
    private final Long cashierId;
    private final CashierTxnType txnType;
    private final BigDecimal txnAmount;
    private final Date txnDate;
    private final Long entityId;
    private final String entityType;
    private final String txnNote;
    private final Date createdDate;
    
    // Template fields
    private final Long officeId;
    private final String officeName;
    private final Long tellerId;
    private final String tellerName;
    private final String cashierName;
        
    private final CashierData cashierData;
    private final Date startDate;
    private final Date endDate;
    
    private final Collection<CurrencyData> currencyOptions;

    /*
     * Creates a new cashier.
     */
    private CashierTransactionData(final Long id, final Long cashierId, CashierTxnType txnType, 
    		final BigDecimal txnAmount, final Date txnDate, String txnNote, 
    		String entityType, Long entityId, Date createdDate, 
    		Long officeId, String officeName, Long tellerId, String tellerName, String cashierName,
    		CashierData cashierData, Date startDate, Date endDate, final Collection<CurrencyData> currencyOptions) {
        this.id = id;
        this.cashierId = cashierId;
        this.txnType = txnType;
        this.txnAmount = txnAmount;
        this.txnDate = txnDate;
        this.txnNote = txnNote;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdDate = createdDate;
        
        this.officeId = officeId;
        this.officeName = officeName;
        this.tellerId = tellerId;
        this.tellerName = tellerName;
        this.cashierName = cashierName;
        this.cashierData = cashierData;
        
        this.startDate = startDate;
        this.endDate = endDate;
        
        this.currencyOptions = currencyOptions;
    }

    public static CashierTransactionData instance(final Long id, final Long cashierId, CashierTxnType txnType,
    		final BigDecimal txnAmount, final Date txnDate, final String txnNote,
    		final String entityType, final Long entityId, final Date createdDate,
    		final Long officeId, final String officeName, final Long tellerId,
    		final String tellerName, final String cashierName, final CashierData cashierData,
    		Date startDate, Date endDate) {
        return new CashierTransactionData(id, cashierId, txnType, txnAmount, txnDate, txnNote, entityType, 
        		entityId, createdDate, officeId, officeName, tellerId,
        		tellerName, cashierName, cashierData, startDate, endDate, null);
    }
    
    public static CashierTransactionData template (final Long cashierId,  
    		final Long tellerId, final String tellerName,
    		final Long officeId, final String officeName, final String cashierName,
    		final CashierData cashierData, Date startDate, Date endDate, final Collection<CurrencyData> currencyOptions) {
        return new CashierTransactionData(null, cashierId, null, null, null, null, null, 
        		null, null, officeId, officeName, tellerId, tellerName, cashierName, cashierData,
        		startDate, endDate, currencyOptions);
    }

    public Long getId() {
        return id;
    }

    public Long getCashierId() {
        return cashierId;
    }
    
    public CashierTxnType getTxnType() {
    	return txnType;
    }
    
    public BigDecimal getTxnAmount() {
    	return txnAmount;
    }
    
    public Date getTxnDate() {
    	return txnDate;
    }
    
    public String getTxnNote() {
    	return txnNote;
    }
    
    public String getEntityType() {
    	return entityType;
    }
    
    public Long getEntityId() {
    	return entityId;
    }

    public Date getCreatedDate() {
    	return createdDate;
    }

    public Long getOfficeId() {
        return officeId;
    }
    
    public String getOfficeName() {
    	return officeName;
    }

    public Long getTellerId() {
        return tellerId;
    }
    
    public String getTellerName() {
    	return tellerName;
    }

    public String getCashierName() {
    	return cashierName;
    }
    
    public Date getStartDate() {
    	return startDate;
    }
    
    public Date getEndDate() {
    	return endDate;
    }

    public CashierData getCashierData() {
    	return cashierData;
    }

}
