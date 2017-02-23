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
package org.apache.fineract.infrastructure.configuration.data;

/**
 * Immutable data object for global configuration property.
 */
public class GlobalConfigurationPropertyData {

    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final boolean enabled;
    @SuppressWarnings("unused")
    private final Long value;
    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String description;
    @SuppressWarnings("unused")
    private final boolean trapDoor;

    public GlobalConfigurationPropertyData(final String name, final boolean enabled, final Long value, final String description, final boolean trapDoor) {
        this.name = name;
        this.enabled = enabled;
        this.value = value;
        this.id = null;
        this.description = description;
        this.trapDoor = trapDoor;
    }

    public GlobalConfigurationPropertyData(final String name, final boolean enabled, final Long value, final Long id,
            final String description, final boolean isTrapDoor) {
        this.name = name;
        this.enabled = enabled;
        this.value = value;
        this.id = id;
        this.description = description;
        this.trapDoor = isTrapDoor;
    }
}