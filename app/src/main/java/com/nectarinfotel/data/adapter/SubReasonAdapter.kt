package com.nectarinfotel.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.nectarinfotel.R
import com.nectarinfotel.data.jsonmodel.DetailInfo

class SubReasonAdapter  (val items : MutableList<DetailInfo>, val context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int { return items.size }
    override fun getItem(position: Int): Int { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //var dataitem = dataList[position]

        val rowView = inflater.inflate(R.layout.caller_list_item, parent, false)
        rowView.findViewById<TextView>(R.id.caller_name).text = items.get(position).sub_reason
        rowView.tag = position
        return rowView
    }
}