<img height='175' src="https://user-images.githubusercontent.com/37406965/51083189-d5dc3a80-173b-11e9-8ca0-28015e0893ac.png" align="left" hspace="20" vspace="1">

# Android Client for MifosX

This is an Android Application built on top of the [MifosX](https://mifosforge.jira.com/wiki/spaces/MIFOSX/overview) platform and written in Java and Kotlin. It is based on Mifos X - a robust core banking platform that is developed for field officers using which they process transactions, keep track of their client’s data, center records, group details, different types of accounts (loan, savings and recurring) of the client, run reports of clients, etc. Its sole purpose is to make field operations easier and effortless. This application also provides an offline feature that allows officers to connect with clients and provide them financial support in remote areas as well.

### Status

[![Join the chat at https://gitter.im/openMF/android-client](https://badges.gitter.im/openMF/android-client.svg)](https://gitter.im/openMF/android-client?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/openMF/android-client.svg?branch=master)](https://travis-ci.org/openMF/android-client)
[![Coverage Status](https://coveralls.io/repos/github/openMF/android-client/badge.svg?branch=master)](https://coveralls.io/github/openMF/android-client?branch=master)



Some **features** that are available in the app are:
- Search of Clients, Client Details Viewing
- Creating new Clients, Groups, and Centers
- Savings Accounts and Loan Accounts Viewing
- Savings Account Transactions (Withdrawal & Deposit)
- Loan Accounts (Approval, Disbursal, Repayments etc.)
- Identifiers and Documents (Creation, Upload, Download and View)
- Collection Sheet Access (Online), Datatables (View, Add/Remove Entries).
- Offline Sync (for Clients, Centers, and Groups) and Offline Dashboard.
- Cheker Inbox
- Path Tracker

## Screenshots

<p>
  <img src="https://i.imgur.com/PBVlVDK.png" />
  <img src="https://i.imgur.com/8Dca3wk.png" />
</p>

## How to Contribute

This is an OpenSource project and we like to see new contributors contibuting to the project. The issues should be raised via the GitHub issue tracker.
For Issue tracker guidelines please click <a href="https://github.com/openMF/android-client/blob/master/.github/ISSUE_TEMPLATE.md">here</a>. All fixes should be proposed via pull requests.
For pull request guidelines please click <a href="https://github.com/openMF/android-client/blob/master/.github/PULL_REQUEST_TEMPLATE.md">here</a>. For commit style guidelines please click <a href="https://github.com/openMF/android-client/wiki/Commit-Style-Guide">here</a>.


## Development Setup

Before you begin, you should have already downloaded the Android Studio SDK and set it up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio).

## Building the Code

1. Clone the repository using HTTP: git clone https://github.com/openMF/android-client.git

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
## Wiki

https://github.com/openMF/android-client/wiki

## Product Roadmap

https://mifosforge.jira.com/wiki/display/MIFOSX/Usability+and+Design - Product Mockup

## License

This project is licensed under the open source MPL V2. See
https://github.com/openMF/android-client/blob/master/LICENSE.md
