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
import com.nectarinfotel.data.adapter.MainPagerAdapter
import com.nectarinfotel.data.adapter.SiteAdapter
import com.nectarinfotel.data.jsonmodel.DetailResponse
import com.nectarinfotel.ui.login.LoginActivity
import com.nectarinfotel.utils.NectarApplication
import com.nectarinfotel.utils.isColorLight
import com.nectarinfotel.utils.onPageSelected
import kotlinx.android.synthetic.main.activity_login_new.*
import kotlinx.android.synthetic.main.affacted_site_popup_layout.*
import kotlinx.android.synthetic.main.new_incident_layout.*
import kotlinx.android.synthetic.main.new_incident_layout.dots
import kotlinx.android.synthetic.main.new_incident_layout.pager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.min

class NewIncidentActivity : AppCompatActivity() , AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    val site: ArrayList<String> = ArrayList()
    private var siteValueList: MutableList<String> = mutableListOf()
    // Initialize a new array with elements
    val colors = arrayOf(
        "Nilesh","Apeksha","Rekha","Sonali","Rashmi",
        "Priya","GreenYellow"
    )
    val area_spinner = arrayOf(
        "Hadapsar","Magarpatta","Goa","India","Pune"
    )
    val services_spinner = arrayOf(
        "alarm service"
        )


    val affactedsite = arrayOf(
        "site1","sit2","site3","site4","site5",
        "site6","site7"
    )
    var province_value: String? =null
    var area: String? =null
    var services: String? =null
    var category: String? =null
    var event: String? =null
    var reason: String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nectarinfotel.R.layout.new_incident_layout)
        setSupportActionBar(toolbar_incident)



        //set data into adapter for services
        initserviceSpinnerResources()

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

        //call api for get categorylist
        GetALlCategoryList()

        //call api for get Eventlist
        GetALlEventList()


        //call api for get Eventlist
        GetALlReasonstList()

        //call api for get AffatcedSiteslist
        GetALlAffactedSitestList()

        // Initialize a new array adapter object
        val adapter = ArrayAdapter<String>(
            this, // Context
            android.R.layout.simple_dropdown_item_1line, // Layout
            colors // Array
        )




        // Set the AutoCompleteTextView adapter
        incident_reported_by.setAdapter(adapter)


        // Auto complete threshold
        // The minimum number of characters to type to show the drop down
        incident_reported_by.threshold = 1


        pager.adapter = MainPagerAdapter()
        pager.offscreenPageLimit = 3
        dots.attachViewPager(pager)
        //  pager.setCurrentItem(1, true);
        pager.onPageSelected {
            val colorRes = when (it) {
                0 -> com.nectarinfotel.R.color.white
                1 -> com.nectarinfotel.R.color.white
                else -> com.nectarinfotel.R.color.white
            }
            val color = ContextCompat.getColor(this, colorRes)
           // frame.setBackgroundColor(color)
            dots.setDotTintRes(if (color.isColorLight()) com.nectarinfotel.R.color.colorPrimaryDark else com.nectarinfotel.R.color.white)
        }
        dots.setDotTintRes( com.nectarinfotel.R.color.colorPrimaryDark)

        extra_site.setOnClickListener{
            showallsites()
        }
        next_layout.setOnClickListener{
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
            }
        }

        previous_layout.setOnClickListener{
            if(pager.currentItem==2)
            {
                next_layout.visibility=View.VISIBLE
                pager.setCurrentItem(1, true);
            } else if(pager.currentItem==1)
            {
                previous_layout.visibility=View.GONE
                next_layout.visibility=View.VISIBLE
                pager.setCurrentItem(0, true);
            }

        }

        // Set an item click listener for auto complete text view
        incident_reported_by.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Display the clicked item using toast
            Toast.makeText(applicationContext,"Selected : $selectedItem", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(applicationContext,"caller closed.",Toast.LENGTH_SHORT).show()
        }


        // Set an item click listener for auto complete text view
        affacted_site.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Display the clicked item using toast
            siteValueList.add(selectedItem)
            Log.d("siteValueList","fgfhfg"+siteValueList.size)
            if(siteValueList.size==1)
            {
                site1.text=selectedItem
            }else if(siteValueList.size==2)
            {
                site1.text=siteValueList.get(0)
                site2.text=siteValueList.get(1)
            }else if(siteValueList.size>2)
            {
                Log.d("gfgf","fgfhfg")
                site1.text=siteValueList.get(0)
                site2.text=siteValueList.get(1)
                var count=siteValueList.size-2
                extra_site.text="+"+count+"More"
            }


            Toast.makeText(applicationContext,"Selected : $selectedItem", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(applicationContext,"Suggestion closed.",Toast.LENGTH_SHORT).show()
        }
        backImageView_incident.setOnClickListener {
            finish()
        }
        create_incident.setOnClickListener {
          if(incident_title.text.toString().length==0)
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
          {

          }
        }
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
    private fun GetALlAffactedSitestList() {
        var call = NectarApplication.mRetroClient?.callAffactedSitesListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    Log.d("jsonarray", ""+detailResponse.site_name)
                    //set data into adapter for area
                    initAffactedSitesResources(detailResponse.site_name)
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
                    //set data into adapter for area
                    initReasonsSpinnerResources(detailResponse.reason)
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
                Log.d("str_response", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    Log.d("jsonarray", ""+detailResponse.event)
                    //set data into adapter for area
                    initEventEventResources(detailResponse.event)
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
    private fun GetALlCategoryList() {
        var call = NectarApplication.mRetroClient?.callCategoriesListAPI(1)
            call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    Log.d("jsonarray", ""+detailResponse.category)
                    //set data into adapter for area
                    initCategorySpinnerResources(detailResponse.category)
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
    private fun GetALlAreaList() {
        var call = NectarApplication.mRetroClient?.callAreaListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                if(detailResponse.msg.equals("Data found"))
                {
                    Log.d("jsonarray", ""+detailResponse.name)
                    //set data into adapter for area
                    initAreaSpinnerResources(detailResponse.name)
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
    private fun GetALlProvinceList() {
        var call = NectarApplication.mRetroClient?.callProvinceListAPI(1)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("str_response", response.body().toString())
                val rsp: JsonObject? = response.body() ?: return
                var response=response.body().toString()
                val detailResponse = Gson().fromJson<DetailResponse>(rsp,DetailResponse::class.java)
                Log.d("message", ""+detailResponse.msg)
                Log.d("message", ""+detailResponse.msg)
                 if(detailResponse.msg.equals("Data found"))
                 {

                     Log.d("jsonarray", ""+detailResponse.province)
                     //set data into adapter for province
                     initProvinceSpinnerResources(detailResponse.province)
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
    private fun showallsites() {
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.affacted_site_popup_layout)
        site.add("site1")
        site.add("site1")
        site.add("site1")
        site.add("site1")
        site.add("site1")

        dialog .site_recyclerView
        dialog .site_recyclerView.layoutManager = LinearLayoutManager(this)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        dialog .site_recyclerView.adapter = SiteAdapter(site, this)

        dialog.deletesitedialog.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun initserviceSpinnerResources() {
        incident_services.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country list
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, area_spinner)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        incident_services.adapter = aa
    }
    private fun initAffactedSitesResources(sites: MutableList<String>) {

        //set adapter for affacted site
        val siteadapter = ArrayAdapter<String>(
            this, // Context
            android.R.layout.simple_spinner_dropdown_item, // Layout
            sites // Array
        )
        affacted_site.threshold = 1
        affacted_site.setAdapter(siteadapter)

    }
    private fun initReasonsSpinnerResources(reasons: MutableList<String>) {
        incident_reason.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country list
        reasons.add(0,resources.getString(R.string.select))
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, reasons)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        incident_reason.adapter = aa
    }
    private fun initCategorySpinnerResources(categories: MutableList<String>) {
        incident_categories.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country list
        categories.add(0,resources.getString(R.string.select))
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        incident_categories.adapter = aa
    }
    private fun initAreaSpinnerResources(area: MutableList<String>) {
        incident_area.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country list
        area.add(0,resources.getString(R.string.select))
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, area)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        incident_area.adapter = aa
    }

    private fun initEventEventResources(event: MutableList<String>) {
        incident_event.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country list
        event.add(0,resources.getString(R.string.select))
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, event)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        incident_event.adapter = aa
    }
    private fun initProvinceSpinnerResources(provincearray: MutableList<String>) {
       province.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country list
        provincearray.add(0,resources.getString(R.string.select))
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, provincearray)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        province.adapter = aa
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
           if(parent==province)
           {
               province_value=province.selectedItem.toString()
               Log.d("province_value",province_value)
           }else if(parent==incident_area)
           {
               area=incident_area.selectedItem.toString()
               Log.d("area",area)
           }
           else if(parent==incident_area)
           {
               category=incident_categories.selectedItem.toString()
               Log.d("category",category)
           }
           else if(parent==incident_event)
           {
               event=incident_event.selectedItem.toString()
               Log.d("event",event)
           }
           else if(parent==incident_reason)
           {
               reason=incident_reason.selectedItem.toString()
               Log.d("reason",reason)
           }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if(p0==g2_checkbox)
        {
            if(p1==true)
            {
                 Toast.makeText(applicationContext,"111111", Toast.LENGTH_SHORT).show()
            }else if(p1==false){
                 Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
            }

        } else   if(p0==g3_checkbox)
        {
            if(p1==true)
            {
                Toast.makeText(applicationContext,"3333", Toast.LENGTH_SHORT).show()
            }else if(p1==false){
                Toast.makeText(applicationContext,"44444", Toast.LENGTH_SHORT).show()
            }
        }

        else   if(p0==g4_checkbox)
        {
            if(p1==true)
            {
                Toast.makeText(applicationContext,"3333", Toast.LENGTH_SHORT).show()
            }else if(p1==false){
                Toast.makeText(applicationContext,"44444", Toast.LENGTH_SHORT).show()
            }
        }
        else   if(p0==internationcall_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==voicemail_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==dice_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==face_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==prepaid_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==sms_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==supervise_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==callcentre_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==international_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==ussd_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        } else   if(p0==supervise_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==you_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==sales_checkbox)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }
        else   if(p0==voice_text)
        {
            Toast.makeText(applicationContext,"22222", Toast.LENGTH_SHORT).show()
        }

    }
}