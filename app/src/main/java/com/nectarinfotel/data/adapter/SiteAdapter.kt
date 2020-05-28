package com.nectarinfotel.data.adapter
import android.content.Context
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nectarinfotel.R
import com.nectarinfotel.data.jsonmodel.DetailInfo
import kotlinx.android.synthetic.main.affacted_site_item_layout.view.*
import kotlinx.android.synthetic.main.new_incident_layout.*

class SiteAdapter(
    val items: ArrayList<DetailInfo>,
    val itemsid: ArrayList<DetailInfo>,
    val context: Context,
    val extraSite: TextView
) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.affacted_site_item_layout, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.sitename?.text = items.get(position).site_name
        holder?.site_province?.text = items.get(position).province
        holder?.deletesiteIconImageView?.setOnClickListener(View.OnClickListener {
            items.removeAt(position)
            //itemsid.removeAt(position+2)
            Log.d("position",""+position)
            extraSite.text=""+items.size+" site Added"
           // Log.d("position1",""+position+2)
            notifyDataSetChanged()
        })
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val sitename = view.sitename
    val site_province = view.site_province
    val deletesiteIconImageView = view.deletesiteIconImageView
}