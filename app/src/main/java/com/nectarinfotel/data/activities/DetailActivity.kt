package com.nectarinfotel.data.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nectarinfotel.R
import com.nectarinfotel.data.adapter.DetailAdapter
import com.nectarinfotel.data.jsonmodel.DetailResponse
import com.nectarinfotel.ui.login.LoginActivity
import com.nectarinfotel.utils.NectarApplication
import kotlinx.android.synthetic.main.activity_detail_page.*
import kotlinx.android.synthetic.main.new_incident_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var mDetailAdapter: DetailAdapter
    private var mStatus: String? = ""
    private var mAgent: String? = ""
    private var mDepartment: String? = ""
    private var subCategory: String? = ""
    private var title: String? = ""

    private var category: Int = -1
    var arrayList = ArrayList<DetailAdapter>()
    internal lateinit var detailslist: ArrayList<DetailAdapter>
    companion object{
        private val INTENT_STATUS_INFO: String? = "INTENT_STATUS_INFO"
        private val INTENT_CATEGORY: String? = "INTENT_CATEGORY"
        private val INTENT_SUB_CATEGORY: String? = "INTENT_SUB_CATEGORY"
        private val INTENT_SUB_TITILE: String? = "INTENT_TITLE"

        @JvmStatic
        fun getStartIntent(
            context: Context,
            status: String,
            category: Int,
            subCategory: String,
            title: String
        ){
            var intent = Intent(context,DetailActivity::class.java)
            intent.putExtra(INTENT_STATUS_INFO,status)
            intent.putExtra(INTENT_CATEGORY,category)
            intent.putExtra(INTENT_SUB_CATEGORY,subCategory)
            intent.putExtra(INTENT_SUB_TITILE,title)
            Log.d("status",status)
            Log.d("category",""+category)
            Log.d("subCategory",subCategory)
            Log.d("title",title)
            Log.d("userid", NectarApplication.userID)
            context.startActivity(intent)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_page)
       //Add validation for change language
        if(LoginActivity.language.equals("Portuguese"))
        {
            val config =resources.configuration
            val locale = Locale("pt")
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
            headerTextView.setText(resources.getString(R.string.status))
        }
        else
        {
            val config = resources.configuration
            val locale = Locale("en")
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
            headerTextView.setText(resources.getString(R.string.status))
        }
        getIntentData()
        initXMLResources()

        val extras = getIntent().getExtras()
        if (null != extras) {
            val value = extras.getString("pushnotification")
            //The key argument here must match that used in the other activity
            if(value!=null) {
                title=extras.getString("title")
                subCategory=extras.getString("status")
                category=extras.getString("category").toInt()
                headerTextView.text = resources.getString(R.string.status).plus(" : ").plus(subCategory?.capitalize())
                statusAPICallNotification()
            }
        }
        //call api for getting ticket details
        if(mStatus.equals("status",ignoreCase = true)) statusAPICall()
        if(mStatus.equals("agent",ignoreCase = true)) agentAPICall()
        if(mStatus.equals("department",ignoreCase = true)) departmentAPICall()

        notification.setOnClickListener {
            var intent = Intent(applicationContext,DetailActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onNewIntent(intent : Intent){
        setIntent(intent)
        //handle notification
        val extras = getIntent().getExtras()
        if (null != extras) {
            val value = extras.getString("pushnotification")
            //The key argument here must match that used in the other activity

            if(value!=null) {
                title=extras.getString("title")
                subCategory=extras.getString("status")
                category=extras.getString("category").toInt()
                headerTextView.text = resources.getString(R.string.status).plus(" : ").plus(subCategory?.capitalize())
                statusAPICallNotification()
            }
        }


    }
    private fun initXMLResources() {
        backImageView.setOnClickListener { finish() }
        if(LoginActivity.language.equals("Portuguese"))

        {
            if(mStatus.equals("status"))
            {
                headerTextView.text = "estado".plus(" : ").plus(title?.capitalize())
            }
            else  if(mStatus.equals("agent"))
            {
                headerTextView.text = "Agente".plus(" : ").plus(title?.capitalize())
            }
            else  if(mStatus.equals("department"))
            {
                headerTextView.text = "Área".plus(" : ").plus(title?.capitalize())
            }
        }
    else
    {
        if(mStatus.equals("department"))
        {
            headerTextView.text = "Area".plus(" : ").plus(title?.capitalize())
        }
        else {
        headerTextView.text = "$mStatus".plus(" : ").plus(title?.capitalize())
    }
    }
        recyclerView.hasFixedSize()
        recyclerView.layoutManager =
            LinearLayoutManager(this)
        mDetailAdapter = DetailAdapter()
        recyclerView.adapter = mDetailAdapter


    }

    private fun statusAPICall() {
        detail_loading.visibility= View.VISIBLE
        var call = NectarApplication.mRetroClient?.callStatusDetailAPI(category,mStatus,subCategory,
            NectarApplication.userID,1)

        requestCall(call)
    }

    private fun statusAPICallNotification() {
        detail_loading.visibility= View.VISIBLE
        var call = NectarApplication.mRetroClient?.callStatusDetailAPI(category,"status",subCategory,
            NectarApplication.userID,1)

        requestCall(call)
    }
    private fun agentAPICall() {
        detail_loading.visibility= View.VISIBLE
        var call = NectarApplication.mRetroClient?.callAgentDetailAPI(category,mStatus,subCategory,
            NectarApplication.userID,1)

        requestCall(call)
    }


    private fun departmentAPICall() {
        detail_loading.visibility= View.VISIBLE
        var call = NectarApplication.mRetroClient?.callDepartmentDetailAPI(category,mStatus,subCategory,
            NectarApplication.userID,1)

        requestCall(call)
    }

    private fun requestCall(call: Call<JsonObject>?) {
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                detail_loading.visibility= View.GONE
                val rsp: JsonObject? = response.body() ?: return
                Log.d("response",""+response.body().toString())
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                 var flag=detailResponse.msg
                Log.d("flag",""+flag)
                if(flag.equals("Data found"))
               {
                    Log.d("flag",""+flag)
                    if(detailResponse != null){
                        if( detailResponse.info != null) {
                            detailResponse.info.reverse()
                            mDetailAdapter.updateList(detailResponse.info)
                        }

                    }else{
                        Toast.makeText(applicationContext, (detailResponse as DetailResponse).msg, Toast.LENGTH_SHORT).show()
                    }
                }
            else {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.no_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                detail_loading.visibility= View.GONE
                Toast.makeText(applicationContext, resources.getString(R.string.no_data), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getIntentData(){
        mStatus = intent.getStringExtra(INTENT_STATUS_INFO)
        category = intent.getIntExtra(INTENT_CATEGORY,-1)
        subCategory = intent.getStringExtra(INTENT_SUB_CATEGORY)
        title = intent.getStringExtra(INTENT_SUB_TITILE)

    }
}