package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.api.model.BulkRepaymentTransactions
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.injection.ActivityContext
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.OnRetrieveSheetItemData
import com.mifos.objects.accounts.loan.PaymentTypeOptions
import com.mifos.objects.collectionsheet.LoanAndClientName
import com.mifos.utils.ImageLoaderUtils
import java.util.Locale
import javax.inject.Inject

/**
 * Created by aksh on 21/6/18.
 */
class IndividualCollectionSheetDetailsAdapter @Inject constructor(
    @param:ActivityContext private val c: Context,
    private val mListener: ListAdapterListener
) : RecyclerView.Adapter<IndividualCollectionSheetDetailsAdapter.ViewHolder?>() {
    private var paymentTypeList: MutableList<String> = mutableListOf()
    private var loanAndClientNames: List<LoanAndClientName> = emptyList()
    private var paymentTypeOptionsList: List<PaymentTypeOptions> = emptyList()
    private var sheetItemClickListener: OnRetrieveSheetItemData? = null
    fun setSheetItemClickListener(sheetItemClickListener: OnRetrieveSheetItemData?) {
        this.sheetItemClickListener = sheetItemClickListener
    }

    fun setPaymentTypeOptionsList(paymentTypeOptionsList: List<PaymentTypeOptions>?) {
        if (paymentTypeOptionsList != null) {
            this.paymentTypeOptionsList = paymentTypeOptionsList
        }
    }

    fun setPaymentTypeList(paymentTypeList: List<String>?) {
        this.paymentTypeList = paymentTypeList as MutableList<String>
        this.paymentTypeList.add(c.getString(R.string.payment_type))
    }

    fun setLoans(loanAndClientNameList: List<LoanAndClientName>) {
        loanAndClientNames = loanAndClientNameList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_individual_collection_sheet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val loanAndClientNameItem = loanAndClientNames[position]
        val loanCollectionSheetItem = loanAndClientNameItem.loan
        holder.tvClientName!!.text = loanAndClientNameItem.clientName
        if (loanCollectionSheetItem != null) {
            holder.tvProductCode!!.text = loanCollectionSheetItem
                .productShortName?.let {
                    loanCollectionSheetItem.accountId?.let { it1 ->
                        concatProductWithAccount(
                            it, it1
                        )
                    }
                }
        }
        if (loanCollectionSheetItem?.chargesDue != null) {
            holder.etCharges!!.text = String.format(
                Locale.getDefault(), "%f",
                loanCollectionSheetItem.chargesDue
            )
        }
        if (loanCollectionSheetItem?.totalDue != null) {
            holder.etTotalDues!!.text = String.format(
                Locale.getDefault(), "%f",
                loanCollectionSheetItem.totalDue
            )
        }
        ImageLoaderUtils.loadImage(
            c, loanAndClientNameItem.id,
            holder.iv_userPicture
        )

        //Add default value of transaction irrespective of they are 'saved' or 'cancelled'
        // manually by the user.
        val defaultBulkRepaymentTransaction = BulkRepaymentTransactions()
        if (loanCollectionSheetItem != null) {
            defaultBulkRepaymentTransaction.loanId = loanCollectionSheetItem.loanId
        }
        if (loanCollectionSheetItem != null) {
            defaultBulkRepaymentTransaction.transactionAmount =
                loanCollectionSheetItem.chargesDue +
                        loanCollectionSheetItem.totalDue
        }
    }

    private fun concatProductWithAccount(productCode: String, accountNo: String): String {
        return "$productCode (#$accountNo)"
    }

    override fun getItemCount(): Int {
        return loanAndClientNames.size
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    interface ListAdapterListener {
        fun listItemPosition(position: Int)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var itemPosition = 0

        @JvmField
        @BindView(R.id.tv_client_name)
        var tvClientName: TextView? = null

        @JvmField
        @BindView(R.id.tv_product_code)
        var tvProductCode: TextView? = null

        @JvmField
        @BindView(R.id.et_charges)
        var etCharges: TextView? = null

        @JvmField
        @BindView(R.id.tv_total_due)
        var etTotalDues: TextView? = null

        @JvmField
        @BindView(R.id.btn_additional_details)
        var btnAdditional: ImageView? = null

        @JvmField
        @BindView(R.id.iv_user_picture)
        var iv_userPicture: ImageView? = null

        init {
            ButterKnife.bind(this, v)
            v.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            itemPosition = adapterPosition
            mListener.listItemPosition(itemPosition)
        }
    }
}