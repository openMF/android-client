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
package org.apache.fineract.portfolio.interestratechart.incentive;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.common.domain.ConditionType;

public abstract class AttributeIncentiveCalculation {

    public abstract BigDecimal calculateIncentive(final IncentiveDTO incentiveDTO);

    public boolean applyIncentive(ConditionType conditionType, Long attributeValue, Long actualValue) {
        boolean applyIncentive = false;
        int compareVal = actualValue.compareTo(attributeValue);
        switch (conditionType) {
            case LESSTHAN:
                applyIncentive = compareVal < 0;
            break;
            case EQUAL:
                applyIncentive = compareVal == 0;
            break;
            case NOT_EQUAL:
                applyIncentive = compareVal != 0;
            break;
            case GRETERTHAN:
                applyIncentive = compareVal > 0;
            break;
            default:
                applyIncentive = false;
            break;
        }
        return applyIncentive;
    }
}
