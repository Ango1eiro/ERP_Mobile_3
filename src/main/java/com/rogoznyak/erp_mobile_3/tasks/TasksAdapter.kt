package com.rogoznyak.erp_mobile_3.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rogoznyak.erp_mobile_3.databinding.ListItemSleepNightBinding
import com.rogoznyak.erp_mobile_3.databinding.ListItemTaskBinding
import com.rogoznyak.erp_mobile_3.databinding.ListItemWorksheetBinding
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.Task
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import com.rogoznyak.erp_mobile_3.search.SleepNightAdapter
import com.rogoznyak.erp_mobile_3.search.SleepNightListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val ITEM_VIEW_TYPE_ITEM = 1

class TasksAdapter(val clickListener: TasksListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(TasksDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<Task>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.TaskItem(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TasksAdapter.ViewHolder -> {
                val taskItem = getItem(position) as DataItem.TaskItem
                holder.bind(clickListener, taskItem.task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> TasksAdapter.ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.TaskItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class ViewHolder private constructor(val binding: ListItemTaskBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: TasksListener, item: Task) {
            binding.task = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTaskBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}

class TasksDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class TasksListener(val clickListener: (task: Task) -> Unit) {
    fun onClick(task: Task) = clickListener(task)
}

sealed class DataItem {
    data class TaskItem(val task: Task): DataItem() {
        override val id = task.guid
    }

    abstract val id: Long
}