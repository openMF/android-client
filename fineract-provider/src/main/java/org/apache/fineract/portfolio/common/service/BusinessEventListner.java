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
package org.apache.fineract.portfolio.common.service;

import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;

/**
 * The interface to be implemented by classes that want to be informed when a
 * Business Event executes. example: on completion of loan approval event need
 * to block guarantor funds
 * 
 */
public interface BusinessEventListner {

    /**
     * Implement this method for notifications before executing Business Event
     */
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity);

    /**
     * Implement this method for notifications after executing Business Event
     */
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity);

}
