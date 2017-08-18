package com.mifos.mifosxdroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.injection.ActivityContext;
import com.mifos.mifosxdroid.online.sharingaccount.ShareChargeAdpaterListener;
import com.mifos.objects.templates.clients.ChargeOptions;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayankjindal on 21/08/17.
 */

public class ShareChargesAdapter extends
        RecyclerView.Adapter<ShareChargesAdapter.ViewHolder> {

    @NonNull
    private Context context;

    @NonNull
    private List<ChargeOptions> chargeOptionList;

    @NonNull
    private ShareChargeAdpaterListener clickAddListener;

    @Inject
    public ShareChargesAdapter(@NonNull @ActivityContext Context context) {
        this.context = context;
    }

    public void setChargeOptionList(@NonNull List<ChargeOptions> chargeOptionList) {
        this.chargeOptionList = chargeOptionList;
    }

    public void setAddItemClickListener(@NonNull ShareChargeAdpaterListener clickAddListener) {
        this.clickAddListener = clickAddListener;
    }

    @NonNull
    @Override
    public ShareChargesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                          int viewType) {
        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_share_charge, parent, false);

        return new ShareChargesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShareChargesAdapter.ViewHolder holder,
                                 final int position) {
        if (holder != null) {
            holder.tvChargeName.setText(chargeOptionList.get(position).getName());

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickAddListener.removeChargeItem(position);
                }
            });

            holder.etChargeAmount.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        clickAddListener.editAmount(position, Double.parseDouble(s.toString()));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chargeOptionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @NonNull
        @BindView(R.id.tv_charge_name)
        TextView tvChargeName;

        @NonNull
        @BindView(R.id.et_charge_amount)
        EditText etChargeAmount;

        @NonNull
        @BindView(R.id.btn_remove)
        Button btnRemove;
    }
}
