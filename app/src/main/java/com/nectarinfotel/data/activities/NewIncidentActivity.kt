package com.nectarinfotel.data.activities


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nectarinfotel.R
import com.nectarinfotel.data.adapter.*
import com.nectarinfotel.data.jsonmodel.DetailInfo
import com.nectarinfotel.data.jsonmodel.DetailResponse
import com.nectarinfotel.ui.login.LoginActivity
import com.nectarinfotel.utils.NectarApplication
import com.nectarinfotel.utils.isColorLight
import com.nectarinfotel.utils.onPageSelected
import kotlinx.android.synthetic.main.affacted_site_popup_layout.*
import kotlinx.android.synthetic.main.new_incident_layout.*
import kotlinx.android.synthetic.main.new_incident_layout.dots
import kotlinx.android.synthetic.main.new_incident_layout.pager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class NewIncidentActivity : AppCompatActivity() , AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    val site: ArrayList<String> = ArrayList()
    private var siteValueList: ArrayList<DetailInfo> = ArrayList()
    private var siteValueListid: ArrayList<DetailInfo> = ArrayList()
    private var sitelist: ArrayList<DetailInfo> = ArrayList()
    private var arealist: MutableList<DetailInfo> = mutableListOf()
    private var provincelist: MutableList<DetailInfo> = mutableListOf()
    private var categorylist: MutableList<DetailInfo> = mutableListOf()
    private var eventlist: MutableList<DetailInfo> = mutableListOf()
    private var reasonlist: MutableList<DetailInfo> = mutableListOf()
    private var subreasonlist: MutableList<DetailInfo> = mutableListOf()
    private var servicelist: MutableList<DetailInfo> = mutableListOf()
    private var callerList: ArrayList<DetailInfo> = ArrayList()
    private var callerList_filter: ArrayList<DetailInfo> = ArrayList()

    // Initialize a new array with elements
    val urgency = arrayOf(
        "Low","High","Medium","Critical"
    )

    var technology: String? =null
    var serviceaffacted: String? =null
    var urgency_value: String? =null
    var provinceid: String? =null
    var area: String? =null
    var areaid: String? =null
    var services: String? =null
    var servicesid: String? =null
    var categoryid: String? =null
    var eventid: String? =null
    var reason: String? =null
    var reasonid: String? ="0"
    var subreason: String? =null
    var sub_reasonid: String? ="0"
    var siteid: String? =null
    var callerid: String? =null
    internal lateinit var info: DetailInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nectarinfotel.R.layout.new_incident_layout)
        setSupportActionBar(toolbar_incident)



       //language conversion
        changelanguage()


        g2_checkbox.setOnCheckedChangeListener (this);
        g3_checkbox.setOnCheckedChangeListener (this);
        g4_checkbox.setOnCheckedChangeListener (this);

        internationcall_checkbox.setOnCheckedChangeListener (this);
        voicemail_checkbox.setOnCheckedChangeListener (this);
        dice_checkbox.setOnCheckedChangeListener (this);
        face_checkbox.setOnCheckedChangeListener (this);
        prepaid_checkbox.setOnCheckedChangeListener (this);
        sms_checkbox.setOnCheckedChangeListener (this);
        supervise_checkbox.setOnCheckedChangeListener (this);
        callcentre_checkbox.setOnCheckedChangeListener (this);
        international_checkbox.setOnCheckedChangeListener (this);
        ussd_checkbox.setOnCheckedChangeListener (this);
        you_checkbox.setOnCheckedChangeListener (this);
        sales_checkbox.setOnCheckedChangeListener (this);
        voice_text.setOnCheckedChangeListener (this);

        //call api for get provincelist
         GetALlProvinceList()

        //call api for get Arealist
         GetALlAreaList()

        //call api for get callerlist
        GetALlServiceList()

        //call api for get categorylist
        GetALlCategoryList()

        //call api for get Eventlist
        GetALlEventList()

        //call api for get ReasonList
        GetALlReasonstList()

        //call api for get ReasonList
        GetALlSubReasonstList()


        //call api for get AffatcedSiteslist
        GetALlAffactedSitestList()

        //call api for get callerlist
        GetALlCallerList()

        seturgency()

        pager.adapter = MainPagerAdapter()
        pager.offscreenPageLimit = 3
        dots.attachViewPager(pager)

        pager.onPageSelected {
            val colorRes = when (it) {
                0 -> com.nectarinfotel.R.color.white
                1 -> com.nectarinfotel.R.color.white
                else -> com.nectarinfotel.R.color.white
            }
            val color = ContextCompat.getColor(this, colorRes)
            dots.setDotTintRes(if (color.isColorLight()) com.nectarinfotel.R.color.colorPrimaryDark else com.nectarinfotel.R.color.white)
        }
        dots.setDotTintRes( com.nectarinfotel.R.color.colorPrimaryDark)

        extra_site.setOnClickListener{
            showallsites()
        }
        next_layout.setOnClickListener{

            if(incident_title.text.toString().length==0)
            {
                Toast.makeText(applicationContext,resources.getString(com.nectarinfotel.R.string.enter_title),Toast.LENGTH_SHORT).show()
            } else if (areaid.equals("0"))
            {
                Toast.makeText(applicationContext,resources.getString(com.nectarinfotel.R.string.enter_area),Toast.LENGTH_SHORT).show()
            }
            else if (servicesid.equals("0"))
            {
                Toast.makeText(applicationContext,resources.getString(com.nectarinfotel.R.string.enter_services),Toast.LENGTH_SHORT).show()
            }
            else if (incident_reported_by.text.toString().length==0)
            {
                Toast.makeText(applicationContext,resources.getString(com.nectarinfotel.R.string.enter_caller),Toast.LENGTH_SHORT).show()
            }
            else
            {
            previous_layout.visibility=View.VISIBLE

            if(pager.currentItem==0)
            {

                pager.setCurrentItem(1, true);
                affacted_site.setText("")
                // Set the AutoCompleteTextView for site adapter
                GetALlAffactedSitestList()
            } else if(pager.currentItem==1)
            {
                next_layout.visibility=View.GONE
                pager.setCurrentItem(2, true);
            }}
        }

        previous_layout.setOnClickListener{
            if(pager.currentItem==2)
            {
                next_layout.visibility=View.VISIBLE
                pager.setCurrentItem(1, true);
                affacted_site.setText("")
            } else if(pager.currentItem==1)
            {
                previous_layout.visibility=View.GONE
                next_layout.visibility=View.VISIBLE
                pager.setCurrentItem(0, true);
            }
        }

        affactedsite_delete1.setOnClickListener(View.OnClickListener {
            site1_layout.visibility=View.GONE
        })

        affactedsite_delete2.setOnClickListener(View.OnClickListener {
            site2_layout.visibility=View.GONE
        })

        // Set an item click listener for auto complete text view
        incident_reported_by.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Display the clicked item using toast
            Log.d("callerListcallerList",""+AutoCompleteAdapter.callerlist.size);
            var name=AutoCompleteAdapter.callerlist.get(position).name
            callerid=AutoCompleteAdapter.callerlist.get(position).id
            Log.d("name",""+name);
            Log.d("id",""+callerid);
            //Toast.makeText(applicationContext,"Selected : $name"+id+position+selectedItem, Toast.LENGTH_SHORT).show()
        }
        incident_reported_by.onFocusChangeListener = View.OnFocusChangeListener{
                view, b ->
            if(b){
                // Display the suggestion dropdown on focus
                // Set a focus change listener for auto complete text view
                incident_reported_by.showDropDown()
            }
        }
        // Set a dismiss listener for auto complete text view
        incident_reported_by.setOnDismissListener {

        }

        // Set an item click listener for auto complete text view
        affacted_site.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()

            Log.d("sitelistfilter",""+SiteAutoCompleteAdapter.Sitelist.size);
            var name=SiteAutoCompleteAdapter.Sitelist.get(position).site_name
            siteid=SiteAutoCompleteAdapter.Sitelist.get(position).site_id
            Log.d("name",""+name);
            Log.d("id",""+siteid);
            info= DetailInfo()
            info.site_name=SiteAutoCompleteAdapter.Sitelist.get(position).site_name
            info.site_code=SiteAutoCompleteAdapter.Sitelist.get(position).site_code
            info.site_id=SiteAutoCompleteAdapter.Sitelist.get(position).site_id
            info.province=SiteAutoCompleteAdapter.Sitelist.get(position).province
            siteValueList.add(info)
            siteValueListid.add(info)

            if(siteValueList.size>0)
            {
                var count=siteValueList.size
                extra_site.visibility=View.VISIBLE
                extra_site.text=""+count+" site Added"
            }

           // Toast.makeText(applicationContext,"Selected : $selectedItem", Toast.LENGTH_SHORT).show()
        }
        // Set a focus change listener for auto complete text view
        affacted_site.onFocusChangeListener = View.OnFocusChangeListener{
                view, b ->
            if(b){
                // Display the suggestion dropdown on focus
                affacted_site.showDropDown()
            }
        }
        // Set a dismiss listener for auto complete text view
        affacted_site.setOnDismissListener {

        }
        backImageView_incident.setOnClickListener {
            finish()
        }
        create_incident_button.setOnClickListener {
         /* if(incident_title.text.toString().length==0)
          {
              Toast.makeText(applicationContext,resources.getString(com.nectarinfotel.R.string.enter_title),Toast.LENGTH_SHORT).show()
          }else if (area==null)
          {
              Toast.makeText(applicationContext,resources.getString(com.nectarinfotel.R.string.enter_area),Toast.LENGTH_SHORT).show()
          }
          else if (services==null)
          {
              Toast.makeText(applicationContext,resources.getString(com.nectarinfotel.R.string.enter_services),Toast.LENGTH_SHORT).show()
          }
          else if (incident_reported_by.text.toString().length==0)
          {
              Toast.makeText(applicationContext,resources.getString(com.nectarinfotel.R.string.enter_caller),Toast.LENGTH_SHORT).show()
          }
            else
          {*/

              val siteid = siteValueList.joinToString { it -> "\'${it.site_id}\'" }
             /* Log.d("commaSeperatedString",siteid);


              Log.d("area",areaid)
              Log.d("reportedby",callerid);
              Log.d("title",incident_title.text.toString());
              Log.d("desc","desc");
              Log.d("description_format","text");
              Log.d("province",provinceid);
              Log.d("reasonid",reasonid);
              Log.d("subreason",sub_reasonid);
              Log.d("event",eventid);
              Log.d("category",categoryid);
              Log.d("services",servicesid);
              Log.d("urgency","4");
              Log.d("service_aftd_id",serviceaffacted);
              Log.d("network",technology);
              Log.d("userid",NectarApplication.userID);
              Log.d("siteid",siteid);*/

              IncidentCreateAPI(areaid,callerid,incident_title.text.toString(),incident_description_text.text.toString(),"text",provinceid,reasonid,sub_reasonid,eventid,categoryid,servicesid,urgency_value,serviceaffacted,technology,NectarApplication.userID,siteid,this)

          //}
        }
    }

    private fun seturgency() {
        incident_qualifications.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country list
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, urgency)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        incident_qualifications.adapter = aa
    }
    private fun IncidentCreateAPI(

        areaid: String?,
        callerid: String?,
        title: String,
        desc: String,
        descformat: String,
        provinceid: String?,
        reasonid: String?,
        subReasonid: String?,
        eventid: String?,
        categoryid: String?,
        servicesid: String?,
        urgency: String?,
        serviceaffacted: String?,
        technology: String?,
        userID: String,
        siteid: String,
        newIncidentActivity: NewIncidentActivity
    ) {
        var call = NectarApplication.mRetroClient?.callIncidentCretaeAPI(areaid,callerid,title,desc,descformat,provinceid,reasonid,subReasonid,eventid,categoryid,urgency,serviceaffacted,siteid,technology,userID,servicesid)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("incidentcreate_response", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data Added Successfully"))
                {
                    DetailActivity.getStartIntent(newIncidentActivity, "status", 1, "new", "new")

                }else{

                    Toast.makeText(applicationContext, ""+detailResponse.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("incidentcreate_fail", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun changelanguage() {
        if(LoginActivity.language.equals("Portuguese"))
        {
            val config =resources.configuration
            val locale = Locale("pt")
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
            incident_title_text.setText(resources.getString(com.nectarinfotel.R.string.title))
            incident_area_text.setText(resources.getString(com.nectarinfotel.R.string.select_area))
            incident_services_text.setText(resources.getString(com.nectarinfotel.R.string.select_services))
            incident_reported_by_text.setText(resources.getString(com.nectarinfotel.R.string.Caller))
            create_incident_button.setText(resources.getString(com.nectarinfotel.R.string.create))
            General_info.setText(resources.getString(com.nectarinfotel.R.string.general_info))
            incident_next_text.setText(resources.getString(com.nectarinfotel.R.string.next))
            incident_previous_text.setText(resources.getString(com.nectarinfotel.R.string.previous))
            incident_description_text.setText(resources.getString(com.nectarinfotel.R.string.description))

            network.setText(resources.getString(com.nectarinfotel.R.string.network))
            technologies.setText(resources.getString(com.nectarinfotel.R.string.technology))
            service_affacted.setText(resources.getString(com.nectarinfotel.R.string.service_affacted))
            internationcall.setText(resources.getString(com.nectarinfotel.R.string.internation_calls))
            voicemail.setText(resources.getString(com.nectarinfotel.R.string.voice_mails))
            dice.setText(resources.getString(com.nectarinfotel.R.string.dice))
            face.setText(resources.getString(com.nectarinfotel.R.string.fact))
            prepaid.setText(resources.getString(com.nectarinfotel.R.string.prepaid))
            sms.setText(resources.getString(com.nectarinfotel.R.string.sms))
            supervise.setText(resources.getString(com.nectarinfotel.R.string.supervise))
            callcentre.setText(resources.getString(com.nectarinfotel.R.string.support_callcentre))
            international.setText(resources.getString(com.nectarinfotel.R.string.inter_operators))
            ussd.setText(resources.getString(com.nectarinfotel.R.string.ussd))
            you.setText(resources.getString(com.nectarinfotel.R.string.you))
            sales.setText(resources.getString(com.nectarinfotel.R.string.sales))
            voice.setText(resources.getString(com.nectarinfotel.R.string.voice))
            province_text.setText(resources.getString(com.nectarinfotel.R.string.province))

            reason_qualifications.setText(resources.getString(com.nectarinfotel.R.string.reason_qualifications))
            incident_reason_text.setText(resources.getString(com.nectarinfotel.R.string.reason))
            incident_subreason_text.setText(resources.getString(com.nectarinfotel.R.string.subreason))
            incident_event_text.setText(resources.getString(com.nectarinfotel.R.string.event))
            incident_categories_text.setText(resources.getString(com.nectarinfotel.R.string.categories))
            incident_qualifications_text.setText(resources.getString(com.nectarinfotel.R.string.qualification))
            affacted_site_text.setText(resources.getString(com.nectarinfotel.R.string.affacted_site))
        }
        else
        {
            val config = resources.configuration
            val locale = Locale("en")
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
            incident_title_text.setText(resources.getString(com.nectarinfotel.R.string.title))
            incident_area_text.setText(resources.getString(com.nectarinfotel.R.string.select_area))
            incident_services_text.setText(resources.getString(com.nectarinfotel.R.string.select_services))
            incident_reported_by_text.setText(resources.getString(com.nectarinfotel.R.string.Caller))
            create_incident_button.setText(resources.getString(com.nectarinfotel.R.string.create))
            General_info.setText(resources.getString(com.nectarinfotel.R.string.general_info))
            incident_next_text.setText(resources.getString(com.nectarinfotel.R.string.next))
            incident_previous_text.setText(resources.getString(com.nectarinfotel.R.string.previous))
            incident_description_text.setText(resources.getString(com.nectarinfotel.R.string.description))

            network.setText(resources.getString(com.nectarinfotel.R.string.network))
            technologies.setText(resources.getString(com.nectarinfotel.R.string.technology))
            service_affacted.setText(resources.getString(com.nectarinfotel.R.string.service_affacted))
            internationcall.setText(resources.getString(com.nectarinfotel.R.string.internation_calls))
            voicemail.setText(resources.getString(com.nectarinfotel.R.string.voice_mails))
            dice.setText(resources.getString(com.nectarinfotel.R.string.dice))
            face.setText(resources.getString(com.nectarinfotel.R.string.fact))
            prepaid.setText(resources.getString(com.nectarinfotel.R.string.prepaid))
            sms.setText(resources.getString(com.nectarinfotel.R.string.sms))
            supervise.setText(resources.getString(com.nectarinfotel.R.string.supervise))
            callcentre.setText(resources.getString(com.nectarinfotel.R.string.support_callcentre))
            international.setText(resources.getString(com.nectarinfotel.R.string.inter_operators))
            ussd.setText(resources.getString(com.nectarinfotel.R.string.ussd))
            you.setText(resources.getString(com.nectarinfotel.R.string.you))
            sales.setText(resources.getString(com.nectarinfotel.R.string.sales))
            voice.setText(resources.getString(com.nectarinfotel.R.string.voice))
            province_text.setText(resources.getString(com.nectarinfotel.R.string.province))
            reason_qualifications.setText(resources.getString(com.nectarinfotel.R.string.reason_qualifications))
            incident_reason_text.setText(resources.getString(com.nectarinfotel.R.string.reason))
            incident_subreason_text.setText(resources.getString(com.nectarinfotel.R.string.subreason))
            incident_event_text.setText(resources.getString(com.nectarinfotel.R.string.event))
            incident_categories_text.setText(resources.getString(com.nectarinfotel.R.string.categories))
            incident_qualifications_text.setText(resources.getString(com.nectarinfotel.R.string.qualification))
            affacted_site_text.setText(resources.getString(com.nectarinfotel.R.string.affacted_site))
        }
    }
    private fun GetALlServiceList() {
        var call = NectarApplication.mRetroClient?.callServiceListAPI(1,areaid)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response_service", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    // Log.d("jsonarray", ""+detailResponse.site_name)
                    //set data into adapter for area
                    servicelist = mutableListOf()

                    servicelist.addAll(detailResponse.info)
                    initServiceResources(servicelist)
                }else{
                    servicelist = mutableListOf()
                    info=DetailInfo()
                    info.id="0"
                    info.name="Select One"
                    servicelist.add(info)
                    initServiceResources(servicelist)
                    // Toast.makeText(applicationContext, ""+detailResponse.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail_sites", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GetALlSubReasonstList() {
        var call = NectarApplication.mRetroClient?.callSubReasonListAPI(1,reasonid)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response_sites", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    // Log.d("jsonarray", ""+detailResponse.site_name)
                    //set data into adapter for area
                    subreasonlist = mutableListOf()
                    subreasonlist.addAll(detailResponse.info)
                    initsubReasonsSpinnerResources(subreasonlist)
                }else{
                    subreasonlist = mutableListOf()
                    info=DetailInfo()
                    info.sub_reason_id="0"
                    info.sub_reason="Select One"
                    subreasonlist.add(info)
                    initsubReasonsSpinnerResources(subreasonlist)
                   // Toast.makeText(applicationContext, ""+detailResponse.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail_sites", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GetALlAffactedSitestList() {
        var call = NectarApplication.mRetroClient?.callAffactedSitesListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response_sites", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                   // Log.d("jsonarray", ""+detailResponse.site_name)
                    //set data into adapter for area
                    sitelist.addAll(detailResponse.info)
                    initAffactedSitesResources(sitelist)
                }else{
                    Toast.makeText(applicationContext, ""+detailResponse.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail_sites", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GetALlCallerList() {
        var call = NectarApplication.mRetroClient?.callCallerListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response_caller", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    Log.d("jsonarray", ""+detailResponse.reason)
                    //set data into adapter for area
                 //   initCallerResources(detailResponse.info)
                    callerList.addAll(detailResponse.info)
                    initCallerResources(callerList)
                }else{
                    Toast.makeText(applicationContext, ""+detailResponse.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GetALlReasonstList() {
        var call = NectarApplication.mRetroClient?.callReasonsListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    Log.d("jsonarray", ""+detailResponse.reason)
                    //set data into adapter for reason
                    info=DetailInfo()
                    info.reason_id="0"
                    info.reason="Select One"
                    reasonlist.add(info)
                    reasonlist.addAll(detailResponse.info)
                    initReasonsSpinnerResources(reasonlist)
                }else{
                    Toast.makeText(applicationContext, ""+detailResponse.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GetALlEventList() {
        var call = NectarApplication.mRetroClient?.callEventListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response_event", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    eventlist = mutableListOf()
                    info=DetailInfo()
                    info.id="0"
                    info.name="Select One"
                    eventlist.add(info)
                    eventlist.addAll(detailResponse.info)
                    //set data into adapter for event
                    initEventEventResources(eventlist)
                }else{
                    eventlist = mutableListOf()
                    info=DetailInfo()
                    info.id="0"
                    info.name="Select One"
                    eventlist.add(info)
                    initEventEventResources(eventlist)
                    //Toast.makeText(applicationContext, ""+detailResponse.msg, Toast.LENGTH_SHORT).show()
                }


            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GetALlCategoryList() {
        var call = NectarApplication.mRetroClient?.callCategoriesListAPI(1)
            call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response_category", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    categorylist = mutableListOf()
                    info=DetailInfo()
                    info.id="0"
                    info.name="Select One"
                    categorylist.add(info)
                    categorylist.addAll(detailResponse.info)
                    //set data into adapter for category
                    initCategorySpinnerResources(categorylist)
                }else{
                    categorylist = mutableListOf()
                    info=DetailInfo()
                    info.id="0"
                    info.name="Select One"
                    categorylist.add(info)
                    initCategorySpinnerResources(categorylist)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GetALlAreaList() {
        var call = NectarApplication.mRetroClient?.callAreaListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response_area", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                   // Log.d("jsonarray", ""+detailResponse.name)
                    //set data into adapter for area
                    arealist = mutableListOf()
                    info=DetailInfo()
                    info.orgnid="0"
                    info.name="Select One"
                    arealist.add(info)
                    arealist.addAll(detailResponse.info)
                    initAreaSpinnerResources(arealist)
                }else{
                    arealist = mutableListOf()
                    info=DetailInfo()
                    info.orgnid="0"
                    info.name="Select One"
                    categorylist.add(info)
                    initAreaSpinnerResources(arealist)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GetALlProvinceList() {
        var call = NectarApplication.mRetroClient?.callProvinceListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response_province", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)

                 if(detailResponse.msg.equals("Data found"))
                 {
                     provincelist = mutableListOf()
                     info=DetailInfo()
                     info.id="0"
                     info.name="Select One"
                     provincelist.add(info)
                     provincelist.addAll(detailResponse.info)
                     //set data into adapter for province
                     initProvinceSpinnerResources(provincelist)
                 }else{
                     provincelist = mutableListOf()
                     info=DetailInfo()
                     info.id="0"
                     info.name="Select One"
                     provincelist.add(info)
                     initProvinceSpinnerResources(provincelist)
                 }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.d("str_responsefail", "str_responsefail"+t)
                Toast.makeText(applicationContext, "please try again"+t, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showallsites() {
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.affacted_site_popup_layout)

        dialog .site_recyclerView
        dialog .site_recyclerView.layoutManager = LinearLayoutManager(this)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)
       /* siteValueList.removeAt(0)
        siteValueList.removeAt(1)*/
        // Access the RecyclerView Adapter and load the data into it
        dialog .site_recyclerView.adapter = SiteAdapter(siteValueList,siteValueListid, this,extra_site)

        dialog.deletesitedialog.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initAffactedSitesResources(sites: ArrayList<DetailInfo>) {
        Log.d("sitessites",""+sites.size)
        //set adapter for affacted site
        val adapter= SiteAutoCompleteAdapter( this,sites)
        affacted_site.threshold = 1
        affacted_site.setAdapter(adapter)
    }

    private fun initCallerResources(callerlist: ArrayList<DetailInfo>) {

        //set adapter for affacted site
        val adapter= AutoCompleteAdapter( this,callerlist,callerList_filter)
        incident_reported_by.threshold = 1
        incident_reported_by.setAdapter(adapter)

    }
    private fun initReasonsSpinnerResources(reasons: MutableList<DetailInfo>) {
        incident_reason.onItemSelectedListener = this
        val adapter= ReasonAdapter( reasons,this)
        //Setting the ArrayAdapter data on the Spinner
        incident_reason.adapter = adapter
    }

    private fun initsubReasonsSpinnerResources(reasons: MutableList<DetailInfo>) {
        incident_subreason.onItemSelectedListener = this
        val adapter= SubReasonAdapter( reasons,this)
        //Setting the ArrayAdapter data on the Spinner
        incident_subreason.adapter = adapter
    }
    private fun initCategorySpinnerResources(categories: MutableList<DetailInfo>) {
        incident_categories.onItemSelectedListener = this

        val adapter= AreaAdapter( categories,this)
        //Setting the ArrayAdapter data on the Spinner
        incident_categories.adapter = adapter
    }
    private fun initAreaSpinnerResources(area: MutableList<DetailInfo>) {
        incident_area.onItemSelectedListener = this

        val adapter= AreaAdapter( area,this)
        //Setting the ArrayAdapter data on the Spinner
        incident_area.adapter = adapter
    }

    private fun initServiceResources(service: MutableList<DetailInfo>) {
        incident_services.onItemSelectedListener = this

        val adapter= AreaAdapter( service,this)
        //Setting the ArrayAdapter data on the Spinner
        incident_services.adapter = adapter
    }

    private fun initEventEventResources(event: MutableList<DetailInfo>) {
        incident_event.onItemSelectedListener = this

        incident_services.onItemSelectedListener = this

        val adapter= AreaAdapter( event,this)
        //Setting the ArrayAdapter data on the Spinner
        incident_event.adapter = adapter
    }
    private fun initProvinceSpinnerResources(provincearray: MutableList<DetailInfo>) {
       province.onItemSelectedListener = this

        incident_services.onItemSelectedListener = this

        val adapter= AreaAdapter( provincearray,this)
        //Setting the ArrayAdapter data on the Spinner
        province.adapter = adapter
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
           if(parent==province)
           {
              provinceid=provincelist.get(position).id
               Log.d("province_value",provinceid)
           }else if(parent==incident_area)
           {
               area=arealist.get(position).name
               areaid=arealist.get(position).orgnid
               Log.d("area",area)
               Log.d("areaid",areaid)
               //call api for get service list
               GetALlServiceList()
           }
           else if(parent==incident_categories)
           {
              categoryid=categorylist.get(position).id
               Log.d("category",categoryid)
           }
           else if(parent==incident_event)
           {
               eventid=eventlist.get(position).id
               Log.d("event",eventid)
           }
           else if(parent==incident_reason)
           {
               reason=reasonlist.get(position).reason
               reasonid=reasonlist.get(position).reason_id
               Log.d("reasonid",reasonid)
               Log.d("reason",reason)
               //call api for sub reason list
               GetALlSubReasonstList()
           }
           else if(parent==incident_subreason)
           {
               subreason=subreasonlist.get(position).sub_reason
               sub_reasonid=subreasonlist.get(position).sub_reason_id
               Log.d("subreason",subreason)
               Log.d("sub_reasonid",sub_reasonid)

           }
           else if(parent==incident_services)
           {
               services=servicelist.get(position).name
               servicesid=servicelist.get(position).id
               Log.d("services",services)
               Log.d("servicesid",servicesid)

           }
           else if(parent==incident_qualifications)
           {
               if(incident_qualifications.selectedItem.toString().equals("Low"))
               {
                   urgency_value="4"
               }  else  if(incident_qualifications.selectedItem.toString().equals("Medium"))
               {
                   urgency_value="3"
               }
               else  if(incident_qualifications.selectedItem.toString().equals("High"))
               {
                   urgency_value="2"
               }
               else  if(incident_qualifications.selectedItem.toString().equals("Critical"))
               {
                   urgency_value="1"
               }
               Log.d("urgency_value",urgency_value)

           }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if(p0==g2_checkbox)
        {
            if(p1==true)
            {
                technology="2G"

            }else if(p1==false){

            }

        } else   if(p0==g3_checkbox)
        {
            if(p1==true)
            {
                technology="3G"

            }else if(p1==false){

            }
        }

        else   if(p0==g4_checkbox)
        {
            if(p1==true)
            {
                technology="4G"

            }else if(p1==false){

            }
        }
        else   if(p0==internationcall_checkbox)
        {
            serviceaffacted="1"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==voicemail_checkbox)
        {
            serviceaffacted="2"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==dice_checkbox)
        {
            serviceaffacted="3"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==face_checkbox)
        {
            serviceaffacted="4"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==prepaid_checkbox)
        {
            serviceaffacted="5"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==sms_checkbox)
        {
            serviceaffacted="6"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==supervise_checkbox)
        {
            serviceaffacted="7"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==callcentre_checkbox)
        {
            serviceaffacted="8"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==international_checkbox)
        {
            serviceaffacted="9"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==ussd_checkbox)
        {
            serviceaffacted="10"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==you_checkbox)
        {
            serviceaffacted="11"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==sales_checkbox)
        {
            serviceaffacted="12"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }
        else   if(p0==voice_text)
        {
            serviceaffacted="13"
            Toast.makeText(applicationContext,serviceaffacted, Toast.LENGTH_SHORT).show()
        }

    }
}