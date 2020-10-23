package com.rogoznyak.erp_mobile_3.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.databinding.ListItemSleepNightBinding
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private val ITEM_VIEW_TYPE_ITEM = 1

class SleepNightAdapter(val clickListener: SleepNightListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private var unfilteredList = listOf<DataItem.SleepNightItem>()

    fun addHeaderAndSubmitList(list: List<Counterpart>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.SleepNightItem(it) }
            unfilteredList = items!!
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    fun filter(query: CharSequence?) {
        val list = mutableListOf<DataItem.SleepNightItem>()

        // perform the data filtering
        if(!query.isNullOrEmpty()) {
            list.addAll(unfilteredList.filter {
                it.sleepNight.name.toLowerCase(Locale.getDefault()).contains(query.toString().toLowerCase(Locale.getDefault())) })
        } else {
            list.addAll(unfilteredList)
        }

        submitList(list as List<DataItem>?)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(clickListener, nightItem.sleepNight)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class ViewHolder private constructor(val binding: ListItemSleepNightBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: SleepNightListener, item: Counterpart) {
            binding.counterpart = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class SleepNightListener(val clickListener: (guid: String) -> Unit) {
    fun onClick(counterpart: Counterpart) = clickListener(counterpart.guid)
}

sealed class DataItem {
    data class SleepNightItem(val sleepNight: Counterpart): DataItem() {
        override val id = sleepNight.guid
    }

    abstract val id: String
}