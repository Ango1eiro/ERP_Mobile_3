package com.rogoznyak.erp_mobile_3.search.searchForUser

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.domain.User


class SearchForUserAdapter(
    context: Context?
) : BaseAdapter() {

    val inflater = LayoutInflater.from(context)
    var userList = listOf<User>()

    override fun getCount(): Int = userList.size

    override fun getItem(p0: Int): User = userList.get(p0)

    override fun getItemId(p0: Int): Long = userList.get(p0).hashCode().toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val item: User = getItem(position)
        if (convertView == null) {
            // If convertView is null we have to inflate a new layout
            convertView = inflater.inflate(R.layout.search_for_user_item, parent, false)
            val viewHolder = ViewHolder()
            viewHolder.tvUserName =
                convertView!!.findViewById<View>(R.id.tvUserName) as TextView

            // We set the view holder as tag of the convertView so we can access the view holder later on.
            convertView.tag = viewHolder
        }

        // Retrieve the view holder from the convertView
        val viewHolder = convertView!!.tag as ViewHolder

        // Bind the values to the views
        viewHolder.tvUserName!!.text = item.name
        return convertView
    }


}

class SearchForUserClickListener() {

}

class ViewHolder {
    var tvUserName: TextView? = null
}