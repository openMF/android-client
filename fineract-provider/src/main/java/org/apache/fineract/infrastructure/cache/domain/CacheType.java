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
package org.apache.fineract.infrastructure.cache.domain;

import java.util.HashMap;
import java.util.Map;

public enum CacheType {

    INVALID(0, "cacheType.invalid"), //
    NO_CACHE(1, "cacheType.noCache"), //
    SINGLE_NODE(2, "cacheType.singleNode"), //
    MULTI_NODE(3, "cacheType.multiNode");

    private final Integer value;
    private final String code;

    private static final Map<Integer, CacheType> intToEnumMap = new HashMap<>();

    static {
        for (final CacheType type : CacheType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static CacheType fromInt(final Integer value) {
        CacheType type = intToEnumMap.get(value);
        if (type == null) {
            type = INVALID;
        }
        return type;
    }

    private CacheType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    @Override
    public String toString() {
        return name().toString().replaceAll("_", " ");
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isNoCache() {
        return NO_CACHE.getValue().equals(this.value);
    }

    public boolean isEhcache() {
        return SINGLE_NODE.getValue().equals(this.value);
    }

    public boolean isDistributedCache() {
        return MULTI_NODE.getValue().equals(this.value);
    }
}