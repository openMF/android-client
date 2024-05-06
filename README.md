![cover](https://user-images.githubusercontent.com/94394661/278370464-4248f4eb-3601-42da-af40-0cc1531661ff.png)



<img height='175' src="https://user-images.githubusercontent.com/37406965/51083189-d5dc3a80-173b-11e9-8ca0-28015e0893ac.png" align="left" hspace="20" vspace="1">

# Android Client for MifosX

This is an Android Application built on top of the [MifosX](https://mifosforge.jira.com/wiki/spaces/MIFOSX/overview) platform and written in Kotlin. It is based on Mifos X - a robust core banking platform that is developed for field officers using which they process transactions, keep track of their client’s data, center records, group details, different types of accounts (loan, savings and recurring) of the client, run reports of clients, etc. Its sole purpose is to make field operations easier and effortless. This application also provides an offline feature that allows officers to connect with clients and provide them financial support in remote areas as well.

### Features

- Search of Clients, Client Details Viewing.
- Creating new Clients, Groups, and Centers.
- Savings Accounts and Loan Accounts Viewing.
- Savings Account Transactions (Withdrawal & Deposit).
- Loan Accounts (Approval, Disbursal, Repayments etc).
- Identifiers and Documents (Creation, Upload, Download and View).
- Collection Sheet Access (Online), Datatables (View, Add/Remove Entries).
- Offline Sync (for Clients, Centers, and Groups) and Offline Dashboard.
- Checker Inbox.
- Path Tracker.

### Status

<a href="https://github.com/openMF/android-client/releases"><img src="https://img.shields.io/github/v/release/openMF/android-client" alt="release"/></a>
<a href="https://mifos.slack.com/"><img src="https://img.shields.io/badge/Join%20Our%20Community-Slack-blue" alt="Chat"/></a>
<a href="https://github.com/openMF/android-client/actions"><img src="https://img.shields.io/github/checks-status/openMF/android-client/master?label=build" alt="build"/></a>
<a href="https://github.com/openMF/android-client/issues"><img src="https://img.shields.io/github/commit-activity/m/openMF/android-client" alt="commit-activity"/></a>
<a href="https://github.com/openMF/android-client/blob/main/"><img src="https://img.shields.io/github/license/openMF/android-client" alt="license"/></a>

## Notice

:warning: We are fully committed to implementing [Jetpack Compose](https://developer.android.com/jetpack/compose) and moving ourselves to support `Kotlin multi-platform`. **If you are sending any PR regarding `XML changes` we will `not` consider at this moment but converting XML to Jetpack Compose is most welcome**. We would be pleased to receive any PR you may have regarding logical changes to an Activity/Fragment.

## Join Us on Slack

Mifos boasts an active and vibrant contributor community, Please join us on [slack](https://join.slack.com/t/mifos/shared_invite/zt-2f4nr6tk3-ZJlHMi1lc0R19FFEHxdvng). Once you've joined the mifos slack community, please join the `#android-client` channel to engage with android-client development.

## Demo credentials
Fineract Instance: gsoc.mifos.community

Username: `mifos`

Password: `password`

## How to Contribute

This is an OpenSource project and we like to see new contributors contibuting to the project. The issues should be raised via the GitHub issue tracker.

1. Fork the Project
2. Create Feature Branch 

    ```sh
    git checkout -b fix_#issue_no
    ```
3. Commit your Changes 

    ```sh
    git commit -m "feat/design:Add some message"
    ```

4. Push to the Branch 

    ```sh
    git push --set-upstream origin fix_#issue_no
    ```

5. Open a Pull Request

### Guidelines
- [Issue Tracker](https://github.com/openMF/android-client/blob/master/.github/ISSUE_TEMPLATE.md)
- [Commit Style](https://github.com/openMF/android-client/wiki/Commit-Style-Guide)
- [Pull Request](https://github.com/openMF/android-client/blob/master/.github/PULL_REQUEST_TEMPLATE.md)

## Development Setup

To start, ensure that you've successfully downloaded and properly configured the Android Studio SDK. You can refer to a guide detailing the setup process [here](http://developer.android.com/sdk/installing/index.html?pkg=studio).

## Building the Code

1. Clone the repository

    ```sh
    git clone https://github.com/openMF/android-client.git
    ```
2. Open Android Studio.

3. Click on 'Open an existing Android Studio project'

4. Browse to the directory where you cloned the android-client repo and click OK.

5. Let Android Studio import the project.

6. Build the application in your device by clicking run button.

## Travis CI
<a href="https://travis-ci.com">Travis CI</a> is a hosted continuous integration service used to build and test software projects hosted at GitHub. We use Travis CI for continous integration and clean maintainence of code. All your pull requests must pass the CI build only then, it will be allowed to merge. Sometimes,when the build doesn't pass you can use these commands in your local terminal and check for the errors,</br>

For Mac OS and Linux based, you can use the following commands:

* `./gradlew checkstyle` quality checks on your project’s code using Checkstyle and generates reports from these checks.</br>
* `./gradlew pmd` an check and apply formatting to any plain-text file.</br>
* `./gradlew findbugs`  a program which uses static analysis to look for bugs in Java code.</br>
* `./gradlew build`  provides a command line to execute build script.</br>


For Windows, you can use the following commands:

* `gradlew checkstyle` quality checks on your project’s code using Checkstyle and generates reports from these checks.</br>
* `gradlew pmd` an check and apply formatting to any plain-text file.</br>
* `gradlew findbugs`  a program which uses static analysis to look for bugs in Java code.</br>
* `gradlew build`  provides a command line to execute build script.</br>

## Contributors

Special thanks to the incredible code contributors who continue to drive this project forward.

<a href="https://github.com/openMF/android-client/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=openMF/android-client" />
</a>

## Wiki

View [Wiki](https://github.com/openMF/android-client/wiki)

## License

This project is licensed under the open source [MPL V2](https://github.com/openMF/android-client/blob/master/LICENSE.md).
