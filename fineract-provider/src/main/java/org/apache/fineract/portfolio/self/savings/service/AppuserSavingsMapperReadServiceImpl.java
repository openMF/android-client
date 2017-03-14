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
package org.apache.fineract.portfolio.self.savings.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AppuserSavingsMapperReadServiceImpl implements
		AppuserSavingsMapperReadService {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public AppuserSavingsMapperReadServiceImpl(
			final RoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Boolean isSavingsMappedToUser(Long savingsId, Long appUserId) {
		return this.jdbcTemplate
				.queryForObject(
						"select case when (count(*) > 0) then true else false end "
								+ " from m_selfservice_user_client_mapping as m "
								+ " left join m_savings_account as s on s.client_id = m.client_id "
								+ " where s.id = ? and m.appuser_id = ? ",
						new Object[] { savingsId, appUserId }, Boolean.class);
	}
}
