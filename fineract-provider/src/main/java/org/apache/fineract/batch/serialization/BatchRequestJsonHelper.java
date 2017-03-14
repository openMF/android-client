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
package org.apache.fineract.batch.serialization;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.fineract.batch.domain.BatchRequest;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

/**
 * Extends
 * {@link org.apache.fineract.infrastructure.core.serialization.FromJsonHelper} to
 * de-serialize the incoming String into a JSON List of type
 * {@link org.apache.fineract.batch.domain.BatchRequest}
 * 
 * @author Rishabh Shukla
 * 
 * @see org.apache.fineract.batch.domain.BatchRequest
 * @see org.apache.fineract.infrastructure.core.serialization.FromJsonHelper
 */
@Component
public class BatchRequestJsonHelper extends FromJsonHelper {

    /**
     * Returns a list of batchRequests after de-serializing it from the input
     * JSON string.
     * 
     * @param json
     * @return List<BatchRequest>
     */
    public List<BatchRequest> extractList(final String json) {
        final Type listType = new TypeToken<List<BatchRequest>>() {}.getType();
        final List<BatchRequest> requests = super.getGsonConverter().fromJson(json, listType);
        return requests;
    }
}
