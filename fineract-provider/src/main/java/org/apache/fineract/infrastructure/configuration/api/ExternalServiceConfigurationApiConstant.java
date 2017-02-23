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
package org.apache.fineract.infrastructure.configuration.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExternalServiceConfigurationApiConstant {

    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String EXTERNAL_SERVICE_RESOURCE_NAME = "externalServiceConfiguration";

    public static final Set<String> EXTERNAL_SERVICE_CONFIGURATION_DATA_PARAMETERS = new HashSet<>(Arrays.asList(NAME, VALUE));
}
