package com.nectarinfotel.data.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nectarinfotel.data.adapter.AgentAdapter
import com.nectarinfotel.data.adapter.DepartmentAdapter
import com.nectarinfotel.data.adapter.StatusDashboardAdapter
import com.nectarinfotel.data.jsonmodel.*
import com.nectarinfotel.utils.NectarApplication
import kotlinx.android.synthetic.main.activity_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.github.mikephil.charting.utils.ColorTemplate
import com.nectarinfotel.data.model.OnItemClickInterface
import com.nectarinfotel.ui.login.LoginActivity
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.nectarinfotel.R
import kotlinx.android.synthetic.main.activity_dashboard.downloadIconImageView
import kotlinx.android.synthetic.main.activity_dashboard.toolbar
import kotlinx.android.synthetic.main.new_incident_layout.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DashboardActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    OnItemClickInterface,OnChartValueSelectedListener {
    override fun onNothingSelected() {

    }
    private var bitmap: Bitmap? = null
    private var isexception: Boolean? = false

    override fun onValueSelected(e: Entry?, dataSetIndex: Int, h: Highlight?) {
        Log.i("Entry selected", e.toString())

        val x:Int= e!!.xIndex
        if(rightSpinnerValue.equals("status"))
        {
            var id=statusValueList.get(x)
            var title=statusValueList.get(x)
            DetailActivity.getStartIntent(this, rightSpinnerValue, leftSpinnerValue, id, title)
        }
         else  if(rightSpinnerValue.equals("agent")) {

             var id = agentValueList1.get(x).agentId
             var title=agentValueList1.get(x).agent
            DetailActivity.getStartIntent(this, rightSpinnerValue, leftSpinnerValue, id, title)
        }
        else  if(rightSpinnerValue.equals("department")) {

            var id = departmentValueList1.get(x).orgId
            var title=departmentValueList1.get(x).department
            DetailActivity.getStartIntent(this, rightSpinnerValue, leftSpinnerValue, id, title)
        }
        barChart.highlightValue(h)
    }

    override fun OnClick(status: String, title: String) {

        DetailActivity.getStartIntent(this,rightSpinnerValue,leftSpinnerValue,status,title)
    }

    private var id: String?= null;
    private var TAG: String = DashboardActivity::class.java.simpleName

    private var statusValueList: MutableList<String> = mutableListOf()
    private var statusTotalList: MutableList<BarEntry> = mutableListOf()
    private var agentValueList: MutableList<String> = mutableListOf()
    private var agentValueList1: MutableList<AgenInfo> = mutableListOf()
    private var agentTotalList: MutableList<BarEntry> = mutableListOf()
    private var departmentValueList: MutableList<String> = mutableListOf()
    private var departmentValueList1: MutableList<DepartmentInfo> = mutableListOf()
    private var departmentTotalList: MutableList<BarEntry> = mutableListOf()
    private var leftSpinnerValue: Int = 1
    private var changeSpinnerValue: Int = 1
    private var changeSpinnerValue1: String = "ffff"
    private var rightSpinnerValue: String = "status"

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent == leftSpinner) {
            if (position == 0) {
                changeSpinner.visibility=View.GONE
                leftSpinnerValue = 1
                initializeRightSpinnerAdapter( resources.getStringArray(R.array.statusAgentDepartmentSpinner))
            } else  if (position == 1){
                changeSpinner.visibility=View.GONE
                leftSpinnerValue = 2
                initializeRightSpinnerAdapter( resources.getStringArray(R.array.statusAgentSpinner))
            }
            else
            {
                leftSpinnerValue = 3
                initializeRightSpinnerAdapter( resources.getStringArray(R.array.statusAgentDepartmentSpinner))
            }

            } else if (parent == changeSpinner)
           {
               if(position==0)
               {
                   changeSpinnerValue=1
               } else  if(position==1)
               {
                   changeSpinnerValue=2
               } else  if(position==2)
               {
                   changeSpinnerValue=3
               }

           }

        else {

                if(leftSpinnerValue == 1){
                    rightSpinnerValue =  resources.getStringArray(R.array.statusAgentDepartmentSpinner)[position]
                }else if(leftSpinnerValue == 3){
                    rightSpinnerValue = resources.getStringArray(R.array.statusAgentDepartmentSpinner)[position]
            }
            else
                {
                    rightSpinnerValue = resources.getStringArray(R.array.statusAgentSpinner)[position]
                }

        }

        //call api for fetch data according to categories
        apiCall()
    }

    companion object {
        fun startIntent(context: Context): Intent {
            return Intent(context, DashboardActivity::class.java)
        }

        fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
            val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.draw(c)
            return b
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nectarinfotel.R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

        //change language validation
       if(LoginActivity.language.equals("Portuguese"))
       {
           val config = resources.configuration
           val locale = Locale("pt")
           Locale.setDefault(locale)
           config.locale = locale
           resources.updateConfiguration(config, resources.displayMetrics)
           newTextView.setText(R.string.new_incident)
       }
        else
       {
           val config = resources.configuration
           val locale = Locale("en")
           Locale.setDefault(locale)
           config.locale = locale
           resources.updateConfiguration(config, resources.displayMetrics)
           newTextView.setText(R.string.new_incident)
       }
        addincident_layout.setOnClickListener {
            val intent = Intent(this, NewIncidentActivity::class.java)
            intent.putExtra("category",leftSpinnerValue)
            startActivity(intent)
        }
        downloadImageView_layout.setOnClickListener {
            //we need to handle runtime permission for devices with marshmallow and above
            isexception=false
            requestReadPermissions()
            Log.d("size", " " + pdfview!!.width + "  " + pdfview!!.width)
            bitmap = loadBitmapFromView(pdfview!!, pdfview!!.width, pdfview!!.height)
            //export pdf using this method
            createPdf()
        }

        val bundle: Bundle? = intent.extras;
        id = bundle!!.getString("ID");
        // add swipe refresh functionality
           swipeRefresh.setOnRefreshListener {
               apiCall()
            swipeRefresh.isRefreshing = false
        }

        downloadIconImageView.setOnClickListener{
            var intent =Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //set data in recylerview accroding to status
        initializeRecyclerView()

        //set data into adapter for categories
        initLeftSpinnerResources()

        //set data into adapter for subcategories
        initRightSpinnerResources()

        //set data into adapter for change managment
        initChangeSpinnerResources()

    }

    private fun initializeRecyclerView() {
        recyclerView.hasFixedSize()
        recyclerView.layoutManager =
            LinearLayoutManager(this)
    }

    private fun initLeftSpinnerResources() {
        leftSpinner.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country list

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.openIncidentsSpinner))
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        leftSpinner.adapter = aa

    }


    private fun initRightSpinnerResources() {
        rightSpinner.onItemSelectedListener = this
        initializeRightSpinnerAdapter( resources.getStringArray(R.array.statusAgentDepartmentSpinner))
    }
    private fun initChangeSpinnerResources() {
        changeSpinner.onItemSelectedListener = this
        initializeChangeSpinnerAdapter( resources.getStringArray(R.array.ChangesSpinner))
    }
    private fun initializeRightSpinnerAdapter(statusAgentDepartmentSpinner: Array<String>) {
        //Creating the ArrayAdapter instance having the country list
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusAgentDepartmentSpinner)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        rightSpinner.adapter = aa

    }
    private fun initializeChangeSpinnerAdapter(ChangesSpinner: Array<String>) {
        //Creating the ArrayAdapter instance having the country list
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, ChangesSpinner)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        changeSpinner.adapter = aa
    }
    fun apiCall() {
        if(rightSpinnerValue.equals("Area"))
        {
            rightSpinnerValue="department"
        } else if(rightSpinnerValue.equals("estado"))
        {
            rightSpinnerValue="status"
        }
        else if(rightSpinnerValue.equals("Agente"))
        {
            rightSpinnerValue="agent"
        }
        else if(rightSpinnerValue.equals("Área"))
        {
            rightSpinnerValue="department"
        }
        val call = NectarApplication.mRetroClient!!.callDashboardAPI(leftSpinnerValue.toString(), rightSpinnerValue, id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                val rsp: JsonObject? = response.body() ?: return
                Log.d("responbse",""+response.body().toString())
                if (rightSpinnerValue.equals("agent", ignoreCase = true)) {
                    val agentResponse = Gson().fromJson<AgentResponse>(rsp, AgentResponse::class.java)

                    agentResponse.info.reverse()
                    initializeAgentAdapter(agentResponse.info)
                    setBarValuesForAgents(agentResponse.info)
                    setBarChart(agentTotalList as ArrayList<BarEntry>, agentValueList as ArrayList<String>)


                } else if (rightSpinnerValue.equals("agente", ignoreCase = true)) {
                    val agentResponse = Gson().fromJson<AgentResponse>(rsp, AgentResponse::class.java)

                    agentResponse.info.reverse()
                    initializeAgentAdapter(agentResponse.info)
                    setBarValuesForAgents(agentResponse.info)
                    setBarChart(agentTotalList as ArrayList<BarEntry>, agentValueList as ArrayList<String>)
                }

                else if (rightSpinnerValue.equals("status", ignoreCase = true)) {
                    val statusResponse = Gson().fromJson<StatusResponse>(rsp, StatusResponse::class.java)

                    if(statusResponse.info!=null) {
                        initializeStatusAdapter(statusResponse.info)
                        //set value in barchart for status
                        setBarValuesForStatus(statusResponse.info)
                        setBarChart(
                            statusTotalList as ArrayList<BarEntry>,
                            statusValueList as ArrayList<String>
                        )
                    }

                } else if (rightSpinnerValue.equals("department", ignoreCase = true)) {
                    val departmentResponse = Gson().fromJson<DepartmentResponse>(rsp, DepartmentResponse::class.java)

                    departmentResponse.info.reverse()
                    initializeDepartmentAdapter(departmentResponse.info)
                    //set value in barchart for department
                    setBarValuesForDepartment(departmentResponse.info)
                    setBarChart(departmentTotalList as ArrayList<BarEntry>, departmentValueList as ArrayList<String>)
                }
                else if (rightSpinnerValue.equals("departamento", ignoreCase = true)) {
                    val departmentResponse = Gson().fromJson<DepartmentResponse>(rsp, DepartmentResponse::class.java)

                    departmentResponse.info.reverse()
                    initializeDepartmentAdapter(departmentResponse.info)
                    //set value in barchart for department
                    setBarValuesForDepartment(departmentResponse.info)
                    setBarChart(departmentTotalList as ArrayList<BarEntry>, departmentValueList as ArrayList<String>)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(applicationContext, resources.getString(R.string.no_data), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setBarValuesForStatus(info: List<StatusInfo>) {
        if(info.isEmpty()) return
        var totalAgentTicket : Int = 0
        statusValueList.clear()
        statusTotalList.clear()
        for((start, statusObj) in info.withIndex()) {

            if (statusObj.status != null) {
                statusValueList.add(statusObj.status)

                val total = statusObj.total.toFloat()
                statusTotalList.add(BarEntry(total, start))
                totalAgentTicket += total.toInt()

                TotalTextView.setText(resources.getString(R.string.total_count) + totalAgentTicket)
            }
        }
    }

    private fun setBarValuesForDepartment(info: List<DepartmentInfo>) {
        if(info.isEmpty()) return
        var totalAgentTicket : Int = 0
        departmentTotalList.clear()
        departmentValueList.clear()
        for((start, departmentObj) in info.withIndex()){
            departmentValueList.add(departmentObj.department)

            val total = departmentObj.tickets.toFloat()
            departmentTotalList.add(BarEntry(total,start))
            totalAgentTicket += total.toInt()
            TotalTextView.setText(resources.getString(R.string.total_count) + totalAgentTicket)

        }
    }

    private fun setBarValuesForAgents(info: List<AgenInfo>) {
        if(info.isEmpty()) return
        var totalAgentTicket : Int = 0
        agentTotalList.clear()
        agentValueList.clear()


        for((start, agentObj) in info.withIndex()){
            agentValueList.add(agentObj.agent)

            val total = agentObj.tickets.toFloat()
            agentTotalList.add(BarEntry(total,start))
            totalAgentTicket += total.toInt()

            TotalTextView.setText(resources.getString(R.string.total_count) + totalAgentTicket)
        }
    }


    private fun initializeStatusAdapter(info: MutableList<StatusInfo>) {
        recyclerView.adapter = StatusDashboardAdapter(info,this)
        (recyclerView.adapter as StatusDashboardAdapter).notifyDataSetChanged()

    }

    private fun initializeDepartmentAdapter(info: MutableList<DepartmentInfo>) {
        departmentValueList1.addAll(info.filterNotNull())
        recyclerView.adapter = DepartmentAdapter(info,this)
        (recyclerView.adapter as DepartmentAdapter).notifyDataSetChanged()

    }

    private fun initializeAgentAdapter(info: MutableList<AgenInfo>) {
        agentValueList1.addAll(info.filterNotNull())
        recyclerView.adapter = AgentAdapter(info,this)
        (recyclerView.adapter as AgentAdapter).notifyDataSetChanged()

    }


    private fun setBarChart(entries : ArrayList<BarEntry>,labels : ArrayList<String>) {

        val barDataSet = BarDataSet(entries, "")
        val data = BarData(labels, barDataSet)
        barChart.data = data // set the data and list of lables into chart

        barChart.setDescription("")  // set the description

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
       // barDataSet.color = Color.BLUE

        barChart.animateY(5000)
        var xaxis = XAxis()
        xaxis=barChart.xAxis
        xaxis.setLabelRotationAngle(90F);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
       // barChart.data.setValueFormatter( PercentFormatter());
        xaxis.spaceBetweenLabels=1
        barChart.getLegend().setEnabled(false);
        barChart.setOnChartValueSelectedListener(this)
       // barChart.setPinchZoom(true)
    }

    private fun requestReadPermissions() {

        Dexter.withActivity(this)
            .withPermissions( Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {

                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings

                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError) {
                    Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT).show()
                }
            })
            .onSameThread()
            .check()
    }
    private fun createPdf() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displaymetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displaymetrics)
        val hight = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()

        val convertHighet = hight.toInt()
        val convertWidth = width.toInt()

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas

        val paint = Paint()
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint)

        bitmap = Bitmap.createScaledBitmap(bitmap!!, convertWidth, convertHighet, true)

        paint.color = Color.WHITE
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        document.finishPage(page)

        // write the document content
       val sdf = SimpleDateFormat("dd_M_yyyy_hh:mm:ss")
        val currentDate = sdf.format(Date())
        System.out.println(" C DATE is  "+currentDate)
        // write the document content

        var targetPdf: String = "/storage/emulated/0//"+currentDate+"nt3.pdf"
        Log.d("target",targetPdf)
        val filePath: File
        filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))

        } catch (e: IOException) {
            e.printStackTrace()
            isexception=true
            Toast.makeText(this, resources.getText(R.string.permission), Toast.LENGTH_LONG).show()
        }

        // close the document
        document.close()
        if(isexception==false) {
            val snack =
                Snackbar.make(snackbar_view, resources.getText(R.string.download), Snackbar.LENGTH_INDEFINITE)
            snack.setActionTextColor(Color.YELLOW)
            snack.setAction(resources.getText(R.string.Share), View.OnClickListener {
                // executed when DISMISS is clicked
                System.out.println("Snackbar Set Action - OnClick.")
                sharefile(targetPdf)
            })

            snack.setDuration(8000)
            snack.show()

        }
    }

    fun sharefile(targetPdf: String) {
       var pdf =  File(targetPdf);
         val intent =  Intent();
       intent.setAction(Intent.ACTION_SEND);
        val contentUri = FileProvider.getUriForFile(this, "com.nectarinfotel" + ".provider", pdf)
       intent.putExtra(Intent.EXTRA_STREAM, contentUri);
       intent.setType("application/pdf");
       startActivity(intent);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.english -> {
                LoginActivity.language="english"
                val config = resources.configuration
                val locale = Locale("en")
                Locale.setDefault(locale)
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
                newTextView.setText(R.string.new_incident)
                initializeRecyclerView()
                initLeftSpinnerResources()
                initRightSpinnerResources()
                initChangeSpinnerResources()
                return true
            }
            R.id.pourtuguese -> {
               LoginActivity.language="Portuguese"
                val config = resources.configuration
                val locale = Locale("pt")
                Locale.setDefault(locale)
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
                newTextView.setText(R.string.new_incident)
                initializeRecyclerView()
                initLeftSpinnerResources()
                initRightSpinnerResources()
                initChangeSpinnerResources()
                return true
            }
        }
    return super.onOptionsItemSelected(item)
}
    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}