#Changelog


#Releases

5. 27 Nov 2014 - v0.3.0
4. 12 Aug 2014 - v0.2.0-alpha
3. 31 Jul 2014 - v0.1.1-alpha
2. 23 Jul 2014 - v0.1.0-alpha
1. 14 Jul 2014 - v0.0.1-alpha

##0.3.0
* Fixed #28 - Null Input on Savings Account Transaction was being processed
* Fixed #65 - IndexOutOfBounds Exception while inflating menu on GroupFragment
* Fixed #81 - PaymentTypeOptions List gave a IndexOutofBounds Exception SavingsAccountTransaction Fragment
* Fixed #82 - PaymentTypeOptions List gave a IndexOutofBounds Exception LoanRepaymentTransaction Fragment
* Fixed #83 - NullPointer Exception onSavedInstanceState
* Fixed #84 - Null Input on Loan Account Transaction was being processed
* Fixed #92 - Crash when Internal Server Error Occured
* Fixed #94 - Crash when Internal Server Error Occured

What's New?

* Recurring Deposit Accounts Enabled
* Multi Tenant Login
* Instance URL Now Supports IP Addresses
* User can Enter Custom PORT for the URL like 80, 443, 8080, 8888

For Developers :

*Espresso Integrated

##0.2.0-alpha
* Fixed #66 - App Crashed on Android 2.3 Due to setShowAsAction method.
* Fixed #65 - App Crashed on Android 2.3 due to a different lifecycle call.

What's New?

* Data Table Row Entry : Add and/or Delete Rows in Data Tables for Clients, Loans, Savings etc.

For Developers :

* Travis-CI Integrated
* Instrumentation Tests Configured (A Basic Test Written)

##0.1.1-alpha

What's new:
* Offline Collection Sheet
* Minor Bug Fixes
* Mifos Json Error Parser

##0.1.0-alpha

Bug Fixes :
[MIFOSX-1412] - Usability related issues with respect to mobile app android
[MIFOSX-1384] - Not able to view particular loan details results in unexpected close of application
[MIFOSX-1085] - Search for Clients by ID or Name on Mobile App

What's New :
* View Collection Sheet of a Center
* Update Collection Sheet of a Center and Save it Back to the Server


##0.0.1-alpha

This alpha preview release allows a user to login into a mifos instance and view details about Clients and their accounts. 
Users can add, update or delete a photo of a client. Document can be easily uploaded from the app. You can perform some 
basic transactions like deposits and withdrawals on savings accounts and repayments on loan accounts. 

The detailed functionality that is available in this release is mentoined below.

Allows login/logout functionality with manually configurable mifos-instances. 

Functionality available post login :-

* View list of clients  
* Search for clients 
* View Client Details
* Add/Update/Delete Client Image
* View Loan Accounts 
* View Loan Account Summary
* Approve Loan 
* Disburse Loan 
* Make a Repayment 
* View Repayment Schedule 
* View Loan Transactions 
* View Transaction Details 
* View Savings Accounts 
* View Savings Account Summary 
* View Transactions 
* Make a deposit 
* Make a withdrawal 
* View & Download Documents of Clients , Loan Accounts , Savings Accounts 
* Create & Upload Documents of Clients , Loan Accounts , Savings Accounts 
* View Client Identifiers 
* Remove / Delete Client Identifiers
* View Data Tables of Clients , Loan Accounts , Savings Accounts 
* View Centers
	* View Groups in a Center
		* View Clients in a Group

