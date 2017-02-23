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
package org.apache.fineract.spm.util;

import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.spm.data.ScorecardData;
import org.apache.fineract.spm.data.ScorecardValue;
import org.apache.fineract.spm.domain.Question;
import org.apache.fineract.spm.domain.Response;
import org.apache.fineract.spm.domain.Scorecard;
import org.apache.fineract.spm.domain.Survey;
import org.apache.fineract.useradministration.domain.AppUser;

import java.util.*;

public class ScorecardMapper {

    private ScorecardMapper() {
        super();
    }

    public static List<ScorecardData> map(final List<Scorecard> scorecards) {
        final Map<Date, ScorecardData> scorecardDataMap = new HashMap<>();
        ScorecardData scorecardData = null;
        if (scorecards != null && scorecards.isEmpty()) {
            for (Scorecard scorecard : scorecards) {
                if ((scorecardData = scorecardDataMap.get(scorecard.getCreatedOn())) == null) {
                    scorecardData = new ScorecardData();
                    scorecardDataMap.put(scorecard.getCreatedOn(), scorecardData);
                    scorecardData.setUserId(scorecard.getAppUser().getId());
                    scorecardData.setClientId(scorecard.getClient().getId());
                    scorecardData.setCreatedOn(scorecard.getCreatedOn());
                    scorecardData.setScorecardValues(new ArrayList<ScorecardValue>());
                }

                scorecardData.getScorecardValues().add(new ScorecardValue(scorecard.getQuestion().getId(), scorecard.getResponse().getId(),
                        scorecard.getValue()));
            }

            return new ArrayList<>(scorecardDataMap.values());
        }

        return Collections.EMPTY_LIST;
    }

    public static List<Scorecard> map(final ScorecardData scorecardData, final Survey survey,
                                      final AppUser appUser, final Client client) {
        final List<Scorecard> scorecards = new ArrayList<>();

        final List<ScorecardValue> scorecardValues = scorecardData.getScorecardValues();

        if (scorecardValues != null) {
           for (ScorecardValue scorecardValue : scorecardValues) {
               final Scorecard scorecard = new Scorecard();
               scorecards.add(scorecard);
               scorecard.setSurvey(survey);
               ScorecardMapper.setQuestionAndResponse(scorecardValue, scorecard, survey);
               scorecard.setAppUser(appUser);
               scorecard.setClient(client);
               scorecard.setCreatedOn(scorecardData.getCreatedOn());
               scorecard.setValue(scorecardValue.getValue());
           }
        }
        return scorecards;
    }

    private static void setQuestionAndResponse(final ScorecardValue scorecardValue, final Scorecard scorecard,
                                        final Survey survey) {
        final List<Question> questions = survey.getQuestions();
        for (final Question question : questions) {
            if (question.getId().equals(scorecardValue.getQuestionId())) {
                scorecard.setQuestion(question);
                for (final Response response : question.getResponses()) {
                    if (response.getId().equals(scorecardValue.getResponseId())) {
                        scorecard.setResponse(response);
                        break;
                    }
                }
                break;
            }
        }
    }
}
