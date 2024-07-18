#!/usr/bin/env bash

export STAGING_PACKAGE_NAME="com.mifos.mifosxdroid"

export STAGING_KEYSTORE_FILE="/default_key_store.jks"
export STAGING_KEYSTORE_PASSWORD="mifos1234"
export STAGING_KEY_ALIAS="mifos"
export STAGING_KEY_PASSWORD="mifos1234"

export GPLAY_SERVICE_ACCOUNT_KEY="/path/to/service_account_key.json"

fastlane deploy