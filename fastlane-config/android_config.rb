module FastlaneConfig
  module AndroidConfig
    STORE_CONFIG = {
      default_store_file: "release_keystore.keystore",
      default_store_password: "mifos1234",
      default_key_alias: "mifos",
      default_key_password: "mifos1234"
    }

    FIREBASE_CONFIG = {
      firebase_prod_app_id: "1:728434912738:android:ecdb5b96f0e735661a1dbb",
      firebase_demo_app_id: "1:728434912738:android:53d0930e402622611a1dbb",
      firebase_service_creds_file: "secrets/firebaseAppDistributionServiceCredentialsFile.json",
      firebase_groups: "mifos-mobile-testers"
    }

    BUILD_PATHS = {
      prod_apk_path: "mifosng-android/build/outputs/apk/prod/release/mifosng-android-prod-release.apk",
      demo_apk_path: "mifosng-android/build/outputs/apk/demo/release/mifosng-android-demo-release.apk",
      prod_aab_path: "mifosng-android/build/outputs/bundle/prodRelease/mifosng-android-prod-release.aab"
    }
  end
end