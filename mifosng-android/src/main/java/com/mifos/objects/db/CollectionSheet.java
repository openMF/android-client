package com.mifos.objects.db;

import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class CollectionSheet extends SugarRecord<CollectionSheet> {

    public int[] dueDate;

    @Ignore
    public List<MifosGroup> groups;

//	     public LoanProduct loanProducts[];
//	     public AttendanceTypeOptions attendanceTypeOptions[];

    public void saveData(){

        for (MifosGroup group : groups) {

            if (group.isNew()) {
                group.save();
            }

            List<Client> clients = group.getClients();

            for (Client client : clients) {

                if (client.isNew()) {

                    client.setMifosGroup(group);
                    client.save();

                    AttendanceType attendanceType = client.getAttendanceType();
                    attendanceType.setClient(client);
                    attendanceType.save();

                    List<Loan> loans = client.getLoans();
                    for (Loan loan : loans) {
                        loan.setClient(client);
                        loan.setIsPaymentChanged("no");
                        loan.save();

                        Currency currency = loan.getCurrency();
                        currency.setLoan(loan);
                        currency.save();
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
