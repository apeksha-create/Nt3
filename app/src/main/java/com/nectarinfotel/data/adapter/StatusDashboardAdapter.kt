package com.nectarinfotel.data.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nectarinfotel.R
import com.nectarinfotel.data.jsonmodel.StatusInfo
import com.nectarinfotel.data.model.OnItemClickInterface
import kotlinx.android.extensions.LayoutContainer

class StatusDashboardAdapter(list: MutableList<StatusInfo>, context: Context) :
    RecyclerView.Adapter<StatusDashboardAdapter.ItemViewHolder>() {

    private var mStatusListener: OnItemClickInterface = context as OnItemClickInterface
    private var statusList: MutableList<StatusInfo> = list

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.dashboard_recyclerview_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, pos: Int) {
        itemViewHolder.itemView.tag = statusList[pos]
        itemViewHolder.bind(statusList[pos])
    }

    inner class ItemViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!),
        LayoutContainer {

        fun bind(statusInfo: StatusInfo) {
            if(statusInfo.status!=null)
            {
                containerView!!.findViewById<TextView>(R.id.valueTextView).text = statusInfo.status.capitalize()
            }

            containerView!!.findViewById<TextView>(R.id.numberTextView).text = statusInfo.total
        }

        init {
            itemView.setOnClickListener {
                if (it.tag == null) return@setOnClickListener
                val statusInfo = it.tag as StatusInfo
                if (mStatusListener != null)
                    mStatusListener.OnClick(statusInfo.status,statusInfo.status)
            }
        }

    }
}