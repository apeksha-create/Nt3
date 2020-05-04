package com.nectarinfotel.data.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nectarinfotel.R
import com.nectarinfotel.data.jsonmodel.DepartmentInfo
import com.nectarinfotel.data.model.OnItemClickInterface
import kotlinx.android.extensions.LayoutContainer

class DepartmentAdapter(list : MutableList<DepartmentInfo>, context: Context) : RecyclerView.Adapter<DepartmentAdapter.ItemViewHolder>() {

    private var mListener: OnItemClickInterface = context as OnItemClickInterface
    private var departmentList : MutableList<DepartmentInfo> = list
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder {
        return  ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dashboard_recyclerview_item,parent,false))
    }

    override fun getItemCount(): Int {
        return departmentList.size
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, pos: Int) {
        itemViewHolder.itemView.tag = departmentList[pos]
        itemViewHolder.bind(departmentList[pos])
    }

    inner class ItemViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!),
        LayoutContainer {

        fun bind(departmentInfo : DepartmentInfo) {
            if(departmentInfo.department!=null)
            {
                containerView!!.findViewById<TextView>(R.id.valueTextView).text = departmentInfo.department.capitalize()
            }

            containerView!!.findViewById<TextView>(R.id.numberTextView).text = departmentInfo.tickets
             val idOrg = departmentInfo.orgId
        }

        init {
            itemView.setOnClickListener {
                if (it.tag == null) return@setOnClickListener
                val departmentInfo = it.tag as DepartmentInfo
                if (mListener != null)
                    mListener.OnClick(departmentInfo.orgId,departmentInfo.department)
            }
        }

    }
}