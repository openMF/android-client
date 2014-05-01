package com.mifos.mifosxdroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mifos.mifosxdroid.ClientActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupListAdapter;
import com.mifos.objects.db.*;
import com.mifos.objects.db.AttendanceType;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.Currency;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.MifosGroup;
import com.mifos.utils.services.data.*;
import com.mifos.utils.services.API;
import com.orm.query.Select;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment implements AdapterView.OnItemClickListener
{
    @InjectView(R.id.lv_group)
    ListView lv_group;
    GroupListAdapter adapter = null;
    private final List<MifosGroup> groupList = new ArrayList<MifosGroup>();
    String tag = getClass().getSimpleName();
    View view ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_group,null);
        ButterKnife.inject(this,view);
        getData();
        return view;
    }

    private void setAdapter()
    {
        if(adapter==null)
            adapter = new GroupListAdapter(getActivity(),groupList);
        if(lv_group==null)
        {
            lv_group = (ListView) view.findViewById(R.id.lv_group);
        }
        lv_group.setAdapter(adapter);
        lv_group.setOnItemClickListener(this);

    }
    private void getData()
    {
        API.centerService.getCenter(new Payload(), new Callback<com.mifos.utils.services.data.Center>() {

            @Override
            public void success(com.mifos.utils.services.data.Center center, Response arg1) {
                if(center!=null)
                {
                    Log.i(tag, "Center\n"+center.toString());
                    insertGroupData(center);
                    setAdapter();
                }
                else
                    Log.i(tag, "null is null null null null null null");
            }

            @Override
            public void failure(RetrofitError arg0) {
                Toast.makeText(getActivity(), "There was some error fetching data.", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private List<MifosGroup> insertGroupData(com.mifos.utils.services.data.Center center)
    {

        final List<Loan> listLoans = new ArrayList<Loan>();
        final List<Client> listClients = new ArrayList<Client>();

        for(int i=0;i<center.groups.length;i++)//For Number of groups
        {
            MifosGroup group = center.groups[i].getData();

            com.mifos.utils.services.data.Client[] clients = center.groups[i].clients;
            for(int j=0;j< clients.length;j++) //Number of clients in the group
            {
                com.mifos.utils.services.data.Client client1 = clients[j];
                Client client = new Client();
                client.setClientId(client1.clientId);
                client.setClientName(client1.clientName);

                Log.i(tag,"Client ID:"+client1.clientId+", Client Name:"+client1.clientName+", Group Id:"+ center.groups[i].groupId);

                com.mifos.utils.services.data.AttendanceType attendanceType1 = client1.attendanceType;
                AttendanceType attendanceType = attendanceType1.getData();
                attendanceType.setClientId(client.getClientId());

                client.setAttendanceType(attendanceType);
                client.setGroupId(center.groups[i].groupId);

                if(isNewClient(client.getClientId()))
                {
                        attendanceType.save();
                        client.save();
                }
                for(int k=0;k<client1.loans.length;k++)//Number of loans each client have
                {
                    com.mifos.utils.services.data.Loan loan1 = client1.loans[k];
                    Loan loan = new Loan();
                    loan.setLoanId(loan1.loanId);
                    loan.setDisbursementAmount(loan1.disbursementAmount);
                    loan.setClientId(client1.clientId);

                    Account account = new Account();
                    account.setAccountId(loan1.accountId);
                    account.setAccountStatusId(loan1.accountStatusId);
                    account.setLoanId(loan1.loanId);
                    loan.setAccount(account);

                    Currency currency = new Currency();
                    currency.setCode(loan1.currency.code);
                    currency.setDecimalPlaces(loan1.currency.decimalPlaces);
                    currency.setDisplayLabel(loan1.currency.displayLabel);
                    currency.setDisplaySymbol(loan1.currency.displaySymbol);
                    currency.setInMultiplesOf(loan1.currency.inMultiplesOf);
                    currency.setName(loan1.currency.name);
                    currency.setNameCode(loan1.currency.nameCode);
                    currency.setLoanId(loan.getLoanId());
                    loan.setCurrency(currency);

                    Interest interest = new Interest();
                    interest.setInterestDue(loan1.interestDue);
                    interest.setInterestPaid(loan1.interestPaid);
                    interest.setLoanId(loan.getLoanId());
                    loan.setInterest(interest);

                    Principal principal = new Principal();
                    principal.setPrincipalDue(loan1.principalDue);
                    principal.setPrincipalPaid(loan1.principalPaid);
                    principal.setLoanId(loan.getLoanId());
                    loan.setPrincipal(principal);

                    Product product = new Product();
                    product.setProductId(loan1.productId);
                    product.setProductShortName(loan1.productShortName);
                    product.setLoanId(loan.getLoanId());
                    loan.setProduct(product);

                    Due due = new Due();
                    due.setLoanId(loan.getLoanId());
                    due.setChargesDue(loan1.chargesDue);
                    due.setTotalDue(loan1.totalDue);
                    loan.setDue(due);

                    listLoans.add(loan);
                    if(isNewLoan(loan.getLoanId(),loan.getClientId()))
                    {
                        account.save();
                        currency.save();
                        interest.save();
                        principal.save();
                        product.save();
                        due.save();
                        loan.save();
                    }
                }
                client.setLoans(listLoans);
                listLoans.clear();
            }
            group.setClients(listClients);
            groupList.add(group);
        }

        return groupList;
    }
    private boolean isNewGroup(int groupId)
    {
        boolean isNewGroup = true;
        List<MifosGroup> groups = Select.from(MifosGroup.class).list();
        for(MifosGroup group:groups)
        {
            if(group.getGroupId()==groupId)
            {
                isNewGroup = false;
                break;
            }
        }
        return isNewGroup;

    }
    private boolean isNewLoan(int loanId,int clientId)
    {
        boolean isNewLoan = true;
        List<Loan> loans = Select.from(Loan.class).list();
        for(Loan loan:loans)
        {
            if(loan.getLoanId()==loanId && loan.getClientId()== clientId)
            {
                isNewLoan = false;
                break;
            }
        }
        return isNewLoan;

    }
    private boolean isNewClient(int clientId)
    {
        boolean isNewClient = true;
        List<Client> clients = Select.from(Client.class).list();
        for(Client client:clients)
        {
            if(client.getClientId()== clientId)
            {
                isNewClient = false;
                break;
            }
        }
        return isNewClient;

    }
    private void viewSavedDataInDatabase()
    {

        List<MifosGroup> groups = Select.from(MifosGroup.class).list();
        MifosGroup group = Select.from(MifosGroup.class).first();
        Log.i(getTag(), "Groups in DB:" + group.toString());
        for(MifosGroup mifosGroup:groups)
        {
            Log.i(getClass().getSimpleName(),"Groups:"+mifosGroup.toString());
        }
        List<Client> listClients = Select.from(Client.class).list();
        for(Client client:listClients)
        {
            Log.i(getClass().getSimpleName(),"Groups:"+client.toString());
        }
        List<Loan> listLoans = Select.from(Loan.class).list();
        for(Loan loan:listLoans)
        {
            Log.i(getClass().getSimpleName(),"Groups:"+loan.toString());
        }
        Staff staff = Staff.findById(Staff.class,1L);
        Log.i(tag,"Staff:Name:="+staff.getTableFields().equals("staffName"));
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(getActivity(), ClientActivity.class);
        Log.i(tag,"Group Id:"+groupList.get(i).getGroupId());
        intent.putExtra("group_id",groupList.get(i).getGroupId());
        startActivity(intent);
    }
}
