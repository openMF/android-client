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
package org.apache.fineract.infrastructure.codes.data;

import java.io.Serializable;

/**
 * Immutable data object represent code-value data in system.
 */
public class CodeValueData implements Serializable {

    private final Long id;

    private final String name;

    @SuppressWarnings("unused")
    private final Integer position;

    @SuppressWarnings("unused")
    private final String description;
    private final boolean isActive;

    public static CodeValueData instance(final Long id, final String name, final Integer position, final boolean isActive) {
        String description = null;
        return new CodeValueData(id, name, position, description,isActive);
    }

    public static CodeValueData instance(final Long id, final String name, final String description, final boolean isActive) {
        Integer position = null;
        return new CodeValueData(id, name, position, description,isActive);
    }

    public static CodeValueData instance(final Long id, final String name) {
        String description = null;
        Integer position = null;
        boolean isActive = false;
        return new CodeValueData(id, name, position, description, isActive);
    }

    public static CodeValueData instance(final Long id, final String name, final Integer position, final String description, final boolean isActive) {
        return new CodeValueData(id, name, position, description,isActive);
    }

    private CodeValueData(final Long id, final String name, final Integer position, final String description, final boolean isActive) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.description = description;
        this.isActive = isActive;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}