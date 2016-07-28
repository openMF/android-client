package com.mifos.objects;

import java.util.List;

/**
 * Created by Rajan Maurya on 23/07/16.
 */
public class ErrorSyncServerMessage {

    String developerMessage;

    int httpStatusCode;

    String defaultUserMessage;

    String userMessageGlobalisationCode;

    List<Error> errors;

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getDefaultUserMessage() {
        return defaultUserMessage;
    }

    public void setDefaultUserMessage(String defaultUserMessage) {
        this.defaultUserMessage = defaultUserMessage;
    }

    public String getUserMessageGlobalisationCode() {
        return userMessageGlobalisationCode;
    }

    public void setUserMessageGlobalisationCode(String userMessageGlobalisationCode) {
        this.userMessageGlobalisationCode = userMessageGlobalisationCode;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public class Error {

        String developerMessage;

        String defaultUserMessage;

        String userMessageGlobalisationCode;

        String parameterName;

        public String getDeveloperMessage() {
            return developerMessage;
        }

        public void setDeveloperMessage(String developerMessage) {
            this.developerMessage = developerMessage;
        }

        public String getDefaultUserMessage() {
            return defaultUserMessage;
        }

        public void setDefaultUserMessage(String defaultUserMessage) {
            this.defaultUserMessage = defaultUserMessage;
        }

        public String getUserMessageGlobalisationCode() {
            return userMessageGlobalisationCode;
        }

        public void setUserMessageGlobalisationCode(String userMessageGlobalisationCode) {
            this.userMessageGlobalisationCode = userMessageGlobalisationCode;
        }

        public String getParameterName() {
            return parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }
    }
}
