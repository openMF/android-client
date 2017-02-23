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
package org.apache.fineract.integrationtests.common;

import java.util.HashMap;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class OfficeHelper {

	private static final String OFFICE_URL = "/fineract-provider/api/v1/offices";
	private final RequestSpecification requestSpec;
	private final ResponseSpecification responseSpec;

	public OfficeHelper(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec) {
		this.requestSpec = requestSpec;
		this.responseSpec = responseSpec;
	}

	public OfficeDomain retrieveOfficeByID(int id) {
		final String json = new Gson().toJson(Utils.performServerGet(
				requestSpec, responseSpec, OFFICE_URL + "/" + id + "?"
						+ Utils.TENANT_IDENTIFIER, ""));
		return new Gson().fromJson(json, new TypeToken<OfficeDomain>() {
		}.getType());
	}

	public Integer createOffice(final String openingDate) {
		String json = getAsJSON(openingDate);
		return Utils.performServerPost(this.requestSpec, this.responseSpec,
				OFFICE_URL + "?" + Utils.TENANT_IDENTIFIER, json,
				CommonConstants.RESPONSE_RESOURCE_ID);
	}

	public Integer updateOffice(int id, String name, String openingDate) {
		final HashMap map = new HashMap<>();
		map.put("name", name);
		map.put("dateFormat", "dd MMMM yyyy");
		map.put("locale", "en");
		map.put("openingDate", openingDate);

		System.out.println("map : " + map);

		return Utils.performServerPut(requestSpec, responseSpec, OFFICE_URL
				+ "/" + id + "?" + Utils.TENANT_IDENTIFIER,
				new Gson().toJson(map), "resourceId");
	}

	public static String getAsJSON(final String openingDate) {
		final HashMap<String, String> map = new HashMap<>();
		map.put("parentId", "1");
		map.put("name", Utils.randomNameGenerator("Office_", 4));
		map.put("dateFormat", "dd MMMM yyyy");
		map.put("locale", "en");
		map.put("openingDate", openingDate);
		System.out.println("map : " + map);
		return new Gson().toJson(map);
	}
}
