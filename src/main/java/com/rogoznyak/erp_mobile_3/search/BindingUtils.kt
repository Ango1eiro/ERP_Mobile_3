package com.rogoznyak.erp_mobile_3.search

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.rogoznyak.erp_mobile_3.domain.Counterpart


@BindingAdapter("sleepQualityString")
fun TextView.setSleepQualityString(item: Counterpart?) {
    item?.let {
        text = it.name
    }
}