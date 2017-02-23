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

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonSyntaxException;

/**
 * An {@link ExceptionMapper} to map {@link JsonSyntaxException} thrown by
 * platform into a HTTP API friendly format.
 */
@Provider
@Component
@Scope("singleton")
public class JsonSyntaxExceptionMapper implements ExceptionMapper<JsonSyntaxException> {

    @Override
    public Response toResponse(final JsonSyntaxException exception) {

        final String globalisationMessageCode = "error.msg.invalid.request.body";
        final String defaultUserMessage = "The JSON syntax provided in the body of the request is invalid: " + exception.getMessage();

        final ApiParameterError error = ApiParameterError.generalError(globalisationMessageCode, defaultUserMessage);

        return Response.status(Status.BAD_REQUEST).entity(error).type(MediaType.APPLICATION_JSON).build();
    }
}