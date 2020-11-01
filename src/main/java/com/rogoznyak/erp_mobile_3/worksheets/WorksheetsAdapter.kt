package com.rogoznyak.erp_mobile_3.worksheets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rogoznyak.erp_mobile_3.databinding.ListItemSleepNightBinding
import com.rogoznyak.erp_mobile_3.databinding.ListItemWorksheetBinding
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import com.rogoznyak.erp_mobile_3.search.SleepNightAdapter
import com.rogoznyak.erp_mobile_3.search.SleepNightListener
import com.rogoznyak.erp_mobile_3.worksheets.DataItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val ITEM_VIEW_TYPE_ITEM = 1

class WorksheetsAdapter(val clickListener: WorksheetsListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(WorksheetsDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<Worksheet>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.WorksheetItem(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WorksheetsAdapter.ViewHolder -> {
                val worksheetItem = getItem(position) as DataItem.WorksheetItem
                holder.bind(clickListener, worksheetItem.worksheet)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> WorksheetsAdapter.ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.WorksheetItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class ViewHolder private constructor(val binding: ListItemWorksheetBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: WorksheetsListener, item: Worksheet) {
            binding.worksheet = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemWorksheetBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}

class WorksheetsDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class WorksheetsListener(val clickListener: (worksheet: Worksheet) -> Unit) {
    fun onClick(worksheet: Worksheet) = clickListener(worksheet)
}

sealed class DataItem {
    data class WorksheetItem(val worksheet: Worksheet): DataItem() {
        override val id = worksheet.guid
    }

    abstract val id: Long
}