package com.nectarinfotel.data.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nectarinfotel.R
import com.nectarinfotel.data.jsonmodel.AgenInfo
import com.nectarinfotel.data.model.OnItemClickInterface
import kotlinx.android.extensions.LayoutContainer








class AgentAdapter(list : MutableList<AgenInfo>, context: Context) : RecyclerView.Adapter<AgentAdapter.ItemViewHolder>() {

    private var mListener: OnItemClickInterface = context as OnItemClickInterface
    private var agentList : MutableList<AgenInfo> = list

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder {
        return  ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dashboard_recyclerview_item,parent,false))
    }

    override fun getItemCount(): Int {
        return agentList.size
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, pos: Int) {
        itemViewHolder.itemView.tag = agentList[pos]
        itemViewHolder.bind(agentList[pos])
    }


    inner class ItemViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!),
        LayoutContainer {

        fun bind(agentInfo: AgenInfo) {
            if(agentInfo.agent!=null)
            {
                containerView!!.findViewById<TextView>(com.nectarinfotel.R.id.valueTextView).text = agentInfo.agent.capitalize()
            }

           containerView!!.findViewById<TextView>(com.nectarinfotel.R.id.numberTextView).text = agentInfo.tickets
            val ticket_no= agentInfo.tickets;
            Log.d("list", ticket_no);
             val idAgent= agentInfo.agentId;

        }


        init {
            itemView.setOnClickListener {
                if (it.tag == null) return@setOnClickListener
                val agenInfo = it.tag as AgenInfo
                if (mListener != null)
                    mListener.OnClick(agenInfo.agentId,agenInfo.agent)

            }
        }

    }
}