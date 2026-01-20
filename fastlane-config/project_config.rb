# ==============================================================================
# Project Configuration - Update these values when setting up a new project
# ==============================================================================
# This is the SINGLE SOURCE OF TRUTH for all project-specific configurations.
# Update these values once, and they will be applied across all fastlane lanes.
# ==============================================================================

module FastlaneConfig
  module ProjectConfig
    # ============================================================================
    # Core Project Information
    # ============================================================================
    PROJECT_NAME = "android-client"
    ORGANIZATION_NAME = "Mifos"

    # ============================================================================
    # Android Configuration
    # ============================================================================
    ANDROID = {
      # Package name for Android app
      package_name: "cmp.android.app",

      # Play Store credentials file path
      play_store_json_key: "secrets/playStorePublishServiceCredentialsFile.json",

      # Build output paths (relative to project root)
      apk_paths: {
        prod: "cmp-android/build/outputs/apk/prod/release/cmp-android-prod-release.apk",
        demo: "cmp-android/build/outputs/apk/demo/release/cmp-android-demo-release.apk"
      },
      aab_path: "cmp-android/build/outputs/bundle/prodRelease/cmp-android-prod-release.aab",

      # Keystore configuration
      keystore: {
        file: "release_keystore.keystore",
        password: "mifos1234",
        key_alias: "mifos",
        key_password: "mifos1234"
      },

      # Firebase App Distribution
      firebase: {
        prod_app_id: "1:728434912738:android:ecdb5b96f0e735661a1dbb",
        demo_app_id: "1:728434912738:android:53d0930e402622611a1dbb",
        groups: "mifos-mobile-apps"
      }
    }

    # ============================================================================
    # iOS Configuration
    # ============================================================================
    IOS = {
      # Bundle identifier
      app_identifier: "org.mifos.kmp.template",

      # Team and Developer Account
      team_id: "L432S2FZP5",

      # CI/CD Configuration
      ci_provider: "circleci",  # Options: circleci, travis, jenkins, gitlab_ci, etc.

      # Project paths (relative to project root)
      project_path: "cmp-ios/iosApp.xcodeproj",
      workspace_path: "cmp-ios/iosApp.xcworkspace",
      plist_path: "cmp-ios/iosApp/Info.plist",

      # Build configuration
      scheme: "iosApp",
      output_name: "iosApp.ipa",
      output_directory: "cmp-ios/build",

      # App Store Connect API
      app_store_connect: {
        key_id: "HA469T6757",
        issuer_id: "8er9e361-9603-4c3e-b147-be3b1o816099",
        key_filepath: "./secrets/Auth_key.p8"
      },

      # Code Signing & Provisioning
      code_signing: {
        match_type: "adhoc",
        match_git_url: "git@github.com:openMF/ios-provisioning-profile.git",
        match_git_branch: "master",
        match_git_private_key: "./secrets/match_ci_key",
        provisioning_profiles: {
          adhoc: "match AdHoc org.mifos.kmp.template",
          appstore: "match AppStore org.mifos.kmp.template"
        }
      },

      # Firebase App Distribution
      firebase: {
        app_id: "1:728434912738:ios:shjhsa78392shja",
        groups: "mifos-mobile-apps"
      },

      # App Store metadata paths
      metadata_path: "./fastlane/metadata",
      age_rating_config_path: "./fastlane/age_rating.json",

      # Version configuration
      version_number: "1.0.0"
    }

    # ============================================================================
    # Shared Configuration (Both Android & iOS)
    # ============================================================================
    SHARED = {
      # Firebase service credentials (used by both platforms)
      firebase_service_credentials: "secrets/firebaseAppDistributionServiceCredentialsFile.json"
    }

    # ============================================================================
    # Helper Methods
    # ============================================================================

    # Get Android package name
    def self.android_package_name
      ANDROID[:package_name]
    end

    # Get iOS bundle identifier
    def self.ios_bundle_identifier
      IOS[:app_identifier]
    end

    # Get Firebase credentials file
    def self.firebase_credentials_file
      SHARED[:firebase_service_credentials]
    end

    # Validate that all required files exist
    def self.validate_config
      required_files = [
        ANDROID[:play_store_json_key],
        SHARED[:firebase_service_credentials],
        IOS[:app_store_connect][:key_filepath],
        IOS[:code_signing][:match_git_private_key]
      ]

      missing_files = required_files.reject { |file| File.exist?(File.join(Dir.pwd, '..', file)) }

      unless missing_files.empty?
        UI.important("⚠️  Warning: The following required files are missing:")
        missing_files.each { |file| UI.important("   - #{file}") }
        UI.important("\nPlease ensure these files are in place before running deployment lanes.")
      end
    end
  end
end
