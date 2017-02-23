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
package org.apache.fineract.infrastructure.core.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.fineract.infrastructure.core.data.ApiGlobalErrorResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * An {@link ExceptionMapper} to map {@link AccessDeniedException} thrown by
 * platform into a HTTP API friendly format.
 * 
 * The {@link AccessDeniedException} is thrown by spring security on platform
 * when an attempt is made to use functionality for which the user does have
 * sufficient privileges.
 */
@Provider
@Component
@Scope("singleton")
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(final AccessDeniedException exception) {
        // Status code 403 really reads as:
        // "Authenticated - but not authorized":
        final String defaultUserMessage = exception.getMessage();
        return Response.status(Status.FORBIDDEN).entity(ApiGlobalErrorResponse.unAuthorized(defaultUserMessage))
                .type(MediaType.APPLICATION_JSON).build();
    }
}