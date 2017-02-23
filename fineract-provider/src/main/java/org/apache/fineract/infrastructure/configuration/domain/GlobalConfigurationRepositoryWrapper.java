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
package org.apache.fineract.infrastructure.configuration.domain;

import org.apache.fineract.infrastructure.configuration.exception.GlobalConfigurationPropertyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link GlobalConfigurationRepository} that adds NULL checking and
 * Error handling capabilities
 * </p>
 */
@Service
public class GlobalConfigurationRepositoryWrapper {

    private final GlobalConfigurationRepository repository;

    @Autowired
    public GlobalConfigurationRepositoryWrapper(final GlobalConfigurationRepository repository) {
        this.repository = repository;
    }

    public GlobalConfigurationProperty findOneByNameWithNotFoundDetection(final String propertyName) {
        final GlobalConfigurationProperty property = this.repository.findOneByName(propertyName);
        if (property == null) { throw new GlobalConfigurationPropertyNotFoundException(propertyName); }
        return property;
    }

    public GlobalConfigurationProperty findOneWithNotFoundDetection(final Long configId) {
        final GlobalConfigurationProperty property = this.repository.findOne(configId);
        if (property == null) { throw new GlobalConfigurationPropertyNotFoundException(configId); }
        return property;
    }

    public void save(final GlobalConfigurationProperty globalConfigurationProperty) {
        this.repository.save(globalConfigurationProperty);
    }

    public void saveAndFlush(final GlobalConfigurationProperty globalConfigurationProperty) {
        this.repository.saveAndFlush(globalConfigurationProperty);
    }

    public void delete(final GlobalConfigurationProperty globalConfigurationProperty) {
        this.repository.delete(globalConfigurationProperty);
    }

}