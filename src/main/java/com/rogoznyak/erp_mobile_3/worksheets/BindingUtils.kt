package com.rogoznyak.erp_mobile_3.worksheets

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.rogoznyak.erp_mobile_3.domain.Counterpart
import com.rogoznyak.erp_mobile_3.domain.User

@BindingAdapter("counterpart")
fun TextView.setCounterpart(item: Counterpart?) {
    item?.let {
        text = it.name
    }
}

@BindingAdapter("user")
fun TextView.setUser(item: User?) {
    item?.let {
        text = it.name
    }
}

@BindingAdapter("date")
fun TextView.setDate(item: String?) {
    item?.let {
        text = it
    }
}

@BindingAdapter("duration")
fun TextView.setDuration(item: String?) {
    item?.let {
        text = it
    }
}

@BindingAdapter("description")
fun TextView.setDescription(item: String?) {
    item?.let {
        text = it
    }
}