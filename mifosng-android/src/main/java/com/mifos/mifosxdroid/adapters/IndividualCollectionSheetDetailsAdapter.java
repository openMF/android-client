package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.injection.ActivityContext;
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.OnRetrieveSheetItemData;
import com.mifos.objects.accounts.loan.PaymentTypeOptions;
import com.mifos.objects.collectionsheet.LoanAndClientName;
import com.mifos.objects.collectionsheet.LoanCollectionSheet;
import com.mifos.utils.ImageLoaderUtils;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aksh on 21/6/18.
 */

public class IndividualCollectionSheetDetailsAdapter extends
        RecyclerView.Adapter<IndividualCollectionSheetDetailsAdapter.ViewHolder> {

    private List<String> paymentTypeList;
    private List<LoanAndClientName> loanAndClientNames;
    private List<PaymentTypeOptions> paymentTypeOptionsList;
    private Context c;

    private OnRetrieveSheetItemData sheetItemClickListener;

    private ListAdapterListener mListener;

    @Inject
    public IndividualCollectionSheetDetailsAdapter(@ActivityContext Context context,
                                                   ListAdapterListener mListener) {
        c = context;
        this.mListener = mListener;
    }

    public void setSheetItemClickListener(OnRetrieveSheetItemData sheetItemClickListener) {
        this.sheetItemClickListener = sheetItemClickListener;
    }

    public void setPaymentTypeOptionsList(List<PaymentTypeOptions> paymentTypeOptionsList) {
        this.paymentTypeOptionsList = paymentTypeOptionsList;
    }

    public void setPaymentTypeList(List<String> paymentTypeList) {
        this.paymentTypeList = paymentTypeList;
        this.paymentTypeList.add(c.getString(R.string.payment_type));
    }

    public void setLoans(List<LoanAndClientName> loanAndClientNameList) {
        this.loanAndClientNames = loanAndClientNameList;
    }

    @Override
    public IndividualCollectionSheetDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                 int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_individual_collection_sheet, parent, false);

        return new IndividualCollectionSheetDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IndividualCollectionSheetDetailsAdapter.ViewHolder holder,
                                 int position) {
        if (holder != null) {

            LoanAndClientName loanAndClientNameItem = loanAndClientNames.get(position);
            final LoanCollectionSheet loanCollectionSheetItem = loanAndClientNameItem.getLoan();
            holder.tvClientName.setText(loanAndClientNameItem.getClientName());

            holder.tvProductCode.setText(concatProductWithAccount(loanCollectionSheetItem
                    .getProductShortName(), loanCollectionSheetItem.getAccountId()));

            if (loanCollectionSheetItem.getChargesDue() != null) {
                holder.etCharges.setText(
                        String.format(Locale.getDefault(), "%f",
                                loanCollectionSheetItem.getChargesDue()));
            }

            if (loanCollectionSheetItem.getTotalDue() != null) {
                holder.etTotalDues.setText(
                        String.format(Locale.getDefault(), "%f",
                                loanCollectionSheetItem.getTotalDue()));
            }

            ImageLoaderUtils.loadImage(c, loanAndClientNameItem.getId(),
                    holder.iv_userPicture);

            //Add default value of transaction irrespective of they are 'saved' or 'cancelled'
            // manually by the user.
            BulkRepaymentTransactions defaultBulkRepaymentTransaction = new
                    BulkRepaymentTransactions();
            defaultBulkRepaymentTransaction.setLoanId(loanCollectionSheetItem.getLoanId());
            defaultBulkRepaymentTransaction.setTransactionAmount(
                    loanCollectionSheetItem.getChargesDue() != null ?
                            loanCollectionSheetItem.getChargesDue() +
                                    loanCollectionSheetItem.getTotalDue() :
                            loanCollectionSheetItem.getTotalDue());

        }
    }

    private String concatProductWithAccount(String productCode, String accountNo) {
        return productCode + " (#" + accountNo + ")";
    }

    @Override
    public int getItemCount() {
        return loanAndClientNames.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public interface ListAdapterListener {
        void listItemPosition(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int position;
        @BindView(R.id.tv_client_name)
        TextView tvClientName;

        @BindView(R.id.tv_product_code)
        TextView tvProductCode;

        @BindView(R.id.et_charges)
        TextView etCharges;

        @BindView(R.id.tv_total_due)
        TextView etTotalDues;

        @BindView(R.id.btn_additional_details)
        ImageView btnAdditional;

        @BindView(R.id.iv_user_picture)
        ImageView iv_userPicture;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
            mListener.listItemPosition(position);
        }

    }
}
