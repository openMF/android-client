package com.mifos.mifosxdroid.adapters

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.databinding.ItemCheckerTaskBinding
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask

class CheckerTaskListAdapter : ListAdapter<CheckerTask,
        CheckerTaskListAdapter.ViewHolder>(TaskDiffCallback()) {

    private lateinit var mListener: OnItemClickListener
    private lateinit var mBadgeProcessMode: CheckerTaskBadgeProcessMode

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onApproveClick(position: Int)
        fun onRejectClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onItemLongPress(position: Int)
    }

    interface CheckerTaskBadgeProcessMode {
        fun isInBadgeProcessingMode(): Boolean
        fun onItemSelectedOrDeselcted(view: View, position: Int)
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<CheckerTask>() {
        override fun areItemsTheSame(oldItem: CheckerTask, newItem: CheckerTask): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CheckerTask, newItem: CheckerTask): Boolean {
            return oldItem.resourceId == newItem.resourceId
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCheckerTaskBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mListener = onItemClickListener
    }

    fun setBadgeProcessMode(badgeProcessMode: CheckerTaskBadgeProcessMode) {
        mBadgeProcessMode = badgeProcessMode
    }

    override fun submitList(list: MutableList<CheckerTask>?) {
        list?.let {
            val mList = mutableListOf<CheckerTask>()
            mList.addAll(list)
            super.submitList(mList)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.tvCheckerTaskId.text = currentItem.id.toString()
        holder.tvCheckerTaskDate.text = currentItem.getDate()
        holder.tvCheckerTaskStatus.text = currentItem.status
        holder.tvCheckerTaskMaker.text = currentItem.maker
        holder.tvCheckerTaskAction.text = currentItem.action
        holder.tvCheckerTaskEntity.text = currentItem.entity
        holder.tvCheckerTaskOptionsEntity.text = currentItem.entity
        holder.tvCheckerTaskOptionsDate.text = currentItem.getDate()

        if (mBadgeProcessMode.isInBadgeProcessingMode()) {
            holder.cbCheckerTask.isChecked = false
            holder.cbCheckerTask.visibility = View.VISIBLE
        } else {
            holder.cbCheckerTask.visibility = View.INVISIBLE
        }

        if (currentItem.selectedFlag) {
            holder.cbCheckerTask.isChecked = true
        }
    }

    inner class ViewHolder(private val binding: ItemCheckerTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvCheckerTaskId: TextView = binding.tvCheckerTaskId
        val tvCheckerTaskDate: TextView = binding.tvCheckerTaskDate
        val tvCheckerTaskStatus: TextView = binding.tvCheckerTaskStatus
        val tvCheckerTaskMaker: TextView = binding.tvCheckerTaskMaker
        val tvCheckerTaskAction: TextView = binding.tvCheckerTaskAction
        val tvCheckerTaskEntity: TextView = binding.tvCheckerTaskEntity
        val tvCheckerTaskOptionsEntity: TextView = binding.tvCheckerTaskOptionsEntity
        val tvCheckerTaskOptionsDate: TextView = binding.tvCheckerTaskOptionsDate
        val cbCheckerTask: CheckBox = binding.cbCheckerTask
        private val ivApproveIcon: ImageView = binding.ivApproveIcon
        private val ivRejectIcon: ImageView = binding.ivRejectIcon
        private val ivDeleteIcon: ImageView = binding.ivDeleteIcon

        init {
            binding.root.setOnClickListener {
                mListener.let {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        mListener.onItemClick(position)
                }
                if (binding.llCheckerTaskOptions.visibility == View.GONE) {
                    binding.llCheckerTaskOptions.visibility = View.VISIBLE
                } else {
                    binding.llCheckerTaskOptions.visibility = View.GONE
                }

            }

            binding.root.setOnLongClickListener {
                mListener.onItemLongPress(adapterPosition)
                true
            }

            ivApproveIcon.setOnClickListener {
                mListener.let {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        mListener.onApproveClick(position)
                }
            }

            ivRejectIcon.setOnClickListener {
                mListener.let {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        mListener.onRejectClick(position)
                }
            }

            ivDeleteIcon.setOnClickListener {
                mListener.let {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        mListener.onDeleteClick(position)
                }

            }

            cbCheckerTask.setOnClickListener {
                mBadgeProcessMode.onItemSelectedOrDeselcted(it, adapterPosition)
            }
        }
    }
}
