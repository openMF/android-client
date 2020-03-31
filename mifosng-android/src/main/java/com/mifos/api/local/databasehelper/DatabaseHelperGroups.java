package com.mifos.api.local.databasehelper;

import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanAccount_Table;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.accounts.savings.SavingsAccount_Table;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupDate;
import com.mifos.objects.group.GroupPayload;
import com.mifos.objects.group.GroupPayload_Table;
import com.mifos.objects.group.Group_Table;
import com.mifos.objects.response.SaveResponse;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func0;

/**
 * This DatabaseHelper Managing all Database logic and staff (Saving, Update, Delete).
 * Whenever DataManager send response to save or request to read from Database then this class
 * save the response or read the all values from database and return as accordingly.
 * Created by Rajan Maurya on 28/06/16.
 */
@Singleton
public class DatabaseHelperGroups {

    @Inject
    public DatabaseHelperGroups() {
    }


    /**
     * This Method Saving the Single Group in the Database
     *
     * @param group
     * @return Observable.just(Group)
     */
    public Observable<Group> saveGroup(final Group group) {
        return Observable.defer(new Func0<Observable<Group>>() {
            @Override
            public Observable<Group> call() {

                if (group.getActivationDate().size() != 0) {
                    GroupDate groupDate = new GroupDate(group.getId(), 0,
                            group.getActivationDate().get(0),
                            group.getActivationDate().get(1),
                            group.getActivationDate().get(2));
                    group.setGroupDate(groupDate);
                }
                group.save();
                return Observable.just(group);
            }
        });
    }

    /**
     * Reading All groups from Database table of Group and return the GroupList
     *
     * @return List Of Groups
     */
    public Observable<Page<Group>> readAllGroups() {
        return Observable.defer(new Func0<Observable<Page<Group>>>() {
            @Override
            public Observable<Page<Group>> call() {
                Page<Group> groupPage = new Page<>();
                groupPage.setPageItems(SQLite.select()
                        .from(Group.class)
                        .queryList());
                return Observable.just(groupPage);
            }
        });
    }

    /**
     * This Method Retrieving the Group from the Local Database.
     *
     * @param groupId Group Id
     * @return Group
     */
    public Observable<Group> getGroup(final int groupId) {
        return Observable.defer(new Func0<Observable<Group>>() {
            @Override
            public Observable<Group> call() {

                Group group = SQLite.select()
                        .from(Group.class)
                        .where(Group_Table.id.eq(groupId))
                        .querySingle();

                if (group != null) {
                    group.setActivationDate(Arrays.asList(group.getGroupDate().getDay(),
                            group.getGroupDate().getMonth(), group.getGroupDate().getYear()));
                }

                return Observable.just(group);
            }
        });
    }

    /**
     * This Method  write the GroupAccounts in tho DB. According to Schema Defined in Model
     *
     * @param groupAccounts Model of List of LoanAccount and SavingAccount
     * @param groupId       Group Id
     * @return GroupAccounts
     */
    public Observable<GroupAccounts> saveGroupAccounts(final GroupAccounts groupAccounts,
                                                        final int groupId) {

        return Observable.defer(new Func0<Observable<GroupAccounts>>() {
            @Override
            public Observable<GroupAccounts> call() {

                List<LoanAccount> loanAccounts = groupAccounts.getLoanAccounts();
                List<SavingsAccount> savingsAccounts = groupAccounts.getSavingsAccounts();

                for (LoanAccount loanAccount : loanAccounts) {
                    loanAccount.setGroupId(groupId);
                    loanAccount.save();
                }

                for (SavingsAccount savingsAccount : savingsAccounts) {
                    savingsAccount.setGroupId(groupId);
                    savingsAccount.save();
                }

                return Observable.just(groupAccounts);
            }
        });
    }


    /**
     * This Method Read the Table of LoanAccount and SavingAccount and return the List of
     * LoanAccount and SavingAccount according to groupId
     *
     * @param groupId Group Id
     * @return the GroupAccounts according to Group Id
     */
    public Observable<GroupAccounts> readGroupAccounts(final int groupId) {
        return Observable.defer(new Func0<Observable<GroupAccounts>>() {
            @Override
            public Observable<GroupAccounts> call() {

                List<LoanAccount> loanAccounts = SQLite.select()
                        .from(LoanAccount.class)
                        .where(LoanAccount_Table.groupId.eq(groupId))
                        .queryList();

                List<SavingsAccount> savingsAccounts = SQLite.select()
                        .from(SavingsAccount.class)
                        .where(SavingsAccount_Table.groupId.eq(groupId))
                        .queryList();

                GroupAccounts groupAccounts = new GroupAccounts();
                groupAccounts.setLoanAccounts(loanAccounts);
                groupAccounts.setSavingsAccounts(savingsAccounts);

                return Observable.just(groupAccounts);
            }
        });
    }

    public Observable<SaveResponse> saveGroupPayload(final GroupPayload groupPayload) {
        return Observable.defer(new Func0<Observable<SaveResponse>>() {
            @Override
            public Observable<SaveResponse> call() {
                groupPayload.save();
                return Observable.just(new SaveResponse());
            }
        });
    }


    public Observable<List<GroupPayload>> realAllGroupPayload() {
        return Observable.defer(new Func0<Observable<List<GroupPayload>>>() {
            @Override
            public Observable<List<GroupPayload>> call() {

                List<GroupPayload> groupPayloads = SQLite.select()
                        .from(GroupPayload.class)
                        .queryList();

                return Observable.just(groupPayloads);
            }
        });
    }

    /**
     * This Method for deleting the group payload from the Database according to Id and
     * again fetch the group List from the Database GroupPayload_Table
     * @param id is Id of the Client Payload in which reference client was saved into Database
     * @return List<ClientPayload></>
     */
    public Observable<List<GroupPayload>> deleteAndUpdateGroupPayloads(final int id) {
        return Observable.defer(new Func0<Observable<List<GroupPayload>>>() {
            @Override
            public Observable<List<GroupPayload>> call() {

                Delete.table(GroupPayload.class, GroupPayload_Table.id.eq(id));

                List<GroupPayload> groupPayloads = SQLite.select()
                        .from(GroupPayload.class)
                        .queryList();

                return Observable.just(groupPayloads);
            }
        });
    }


    public Observable<GroupPayload> updateDatabaseGroupPayload(final GroupPayload groupPayload) {
        return Observable.defer(new Func0<Observable<GroupPayload>>() {
            @Override
            public Observable<GroupPayload> call() {
                groupPayload.update();
                return Observable.just(groupPayload);
            }
        });
    }

}
