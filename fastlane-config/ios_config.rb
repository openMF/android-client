require_relative 'project_config'

module FastlaneConfig
  module IosConfig
    FIREBASE_CONFIG = {
      firebase_app_id: ProjectConfig::IOS[:firebase][:app_id],
      firebase_service_creds_file: ProjectConfig.firebase_credentials_file,
      firebase_groups: ProjectConfig::IOS[:firebase][:groups]
    }

    BUILD_CONFIG = {
      project_path: ProjectConfig::IOS[:project_path],
      workspace_path: ProjectConfig::IOS[:workspace_path],
      plist_path: ProjectConfig::IOS[:plist_path],
      scheme: ProjectConfig::IOS[:scheme],
      output_name: ProjectConfig::IOS[:output_name],
      output_directory: ProjectConfig::IOS[:output_directory],
      match_git_private_key: ProjectConfig::IOS[:code_signing][:match_git_private_key],
      match_type: ProjectConfig::IOS[:code_signing][:match_type],
      app_identifier: ProjectConfig::IOS[:app_identifier],
      team_id: ProjectConfig::IOS[:team_id],
      ci_provider: ProjectConfig::IOS[:ci_provider],
      provisioning_profile_name: ProjectConfig::IOS[:code_signing][:provisioning_profiles][:adhoc],
      provisioning_profile_appstore: ProjectConfig::IOS[:code_signing][:provisioning_profiles][:appstore],
      git_url: ProjectConfig::IOS[:code_signing][:match_git_url],
      git_branch: ProjectConfig::IOS[:code_signing][:match_git_branch],
      key_id: ProjectConfig::IOS[:app_store_connect][:key_id],
      issuer_id: ProjectConfig::IOS[:app_store_connect][:issuer_id],
      key_filepath: ProjectConfig::IOS[:app_store_connect][:key_filepath],
      version_number: ProjectConfig::IOS[:version_number],
      metadata_path: ProjectConfig::IOS[:metadata_path],
      app_rating_config_path: ProjectConfig::IOS[:age_rating_config_path]
    }
  end
end