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
package org.apache.fineract.infrastructure.entityaccess.service;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface FineractEntityAccessWriteService {

    CommandProcessingResult createEntityAccess(final JsonCommand command);

    CommandProcessingResult createEntityToEntityMapping(final Long relId,final JsonCommand command);

    CommandProcessingResult updateEntityToEntityMapping(final Long mapId, final JsonCommand command);

    CommandProcessingResult deleteEntityToEntityMapping(final Long mapId);

    void addNewEntityAccess(final String entityType, final Long entityId, final CodeValue accessType, final String secondEntityType,
            final Long secondEntityId);

    /*
     * CommandProcessingResult updateEntityAccess ( final Long entityAccessId,
     * final JsonCommand command);
     * 
     * CommandProcessingResult removeEntityAccess ( final String entityType,
     * final Long entityId, final Long accessType, final String
     * secondEntityType, final Long secondEntityId);
     */
}