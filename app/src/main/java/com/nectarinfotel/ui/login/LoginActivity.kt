package com.nectarinfotel.ui.login

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.nectarinfotel.R
import com.nectarinfotel.data.activities.DashboardActivity
import com.nectarinfotel.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login_new.*
import kotlinx.android.synthetic.main.activity_login_new.password
import kotlinx.android.synthetic.main.activity_login_new.username
import kotlinx.android.synthetic.main.activity_login_new.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    public var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    public var userid: String? = null
    var regId = null
    var spinner: Spinner? =null
    private lateinit var loginViewModel: LoginViewModel
    private val sharedPrefFile = "kotlinsharedpreference"
    companion object {

        public var language: String? = null
       // public var BaseUrl: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login_new)

         spinner = findViewById<Spinner>(R.id.spinner)
        var list_of_items = arrayOf("English", "Portuguese")
        spinner!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner!!.setAdapter(aa)

        websitelink.setText(Html.fromHtml("Powered by :" + "<a href='http://nectarinfotel.com/'> <font color='#FFFFFF'>Nectar Infotel Solution Pvt.Ltd.</font></a> "));
        websitelink.setMovementMethod(LinkMovementMethod.getInstance());// make it active
        registerfcmID()
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

        })
        setvalueforrememberme()
        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()

                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }



            login.setOnClickListener {
                loading.visibility = View.VISIBLE

                    var call = NectarApplication.mRetroClient!!.callLoginAPI(username.text.toString(), password.text.toString(),PrefUtils.getKey(this@LoginActivity,AppConstants.TokenID))
                    call.enqueue(object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            loading.visibility = View.GONE
                            var rsp: JsonObject? = response.body() ?: return
                            if (rsp!!.isJsonObject && rsp["info"] != null && rsp["info"].isJsonObject) {
                                var infoObj = rsp["info"]

                                var name = (rsp["info"] as JsonObject).get("name")
                                 userid = (rsp["info"] as JsonObject).get("userid").toString()


                                // call next activity
                                NectarApplication.userID=userid.toString()

                               var loggedInUserView = LoggedInUserView("$name")
                                updateUiWithUser(loggedInUserView)
                                //call method for remember me
                                rememberme()
                            }else
                            {
                                Toast.makeText(applicationContext, resources.getString(R.string.Invalid_credentials), Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            loading.visibility = View.GONE
                            Toast.makeText(applicationContext, resources.getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                        }
                    })
            }

        }



    }

    private fun registerfcmID() {
        mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                // checking for type intent filter
                if (intent.action == Config.REGISTRATION_COMPLETE) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL)
                    displayFirebaseRegId()

                } else if (intent.action == Config.PUSH_NOTIFICATION) {
                    // new push notification is received

                    val message = intent.getStringExtra("message")

                }
            }
        }

        displayFirebaseRegId()
    }

    override fun onResume() {
        super.onResume()
        // register GCM registration complete receiver
        mRegistrationBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                it,
                 IntentFilter(Config.REGISTRATION_COMPLETE)
            )
        };

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        mRegistrationBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                it,
                 IntentFilter(Config.PUSH_NOTIFICATION))
        };

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    override fun onPause() {
        mRegistrationBroadcastReceiver?.let { LocalBroadcastManager.getInstance(this).unregisterReceiver(it) };
        super.onPause()
    }

    private fun displayFirebaseRegId() {
        val pref = applicationContext.getSharedPreferences(Config.SHARED_PREF, 0)
        val regId = pref.getString("regId", null)
        PrefUtils.storeKey(this@LoginActivity, AppConstants.TokenID, regId)
    }

    private fun setvalueforrememberme() {
       val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
       val sharedNameValue = sharedPreferences.getString(AppConstants.Username,null)
       val sharedPasswordValue = sharedPreferences.getString(AppConstants.Password,null)

       if(sharedNameValue!=null&&sharedPasswordValue!=null)
       {
           username.setText(sharedNameValue).toString()
           password.setText(sharedPasswordValue).toString()
           checkbox.isChecked= true
       }
    else
       {
           checkbox.isChecked= false
       }
   }

    private fun rememberme() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        if(checkbox.isChecked)
        {
            editor.putString(AppConstants.Username,username.text.toString())
            editor.putString(AppConstants.Password,password.text.toString())
        }
        else
        {
            editor.putString(AppConstants.Username,null)
            editor.putString(AppConstants.Password,null)
        }

        editor.apply()
        editor.commit()
    }
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("ID",userid);
        startActivity(intent)
       // startActivity(DashboardActivity.startIntent(this as LoginActivity))
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
       var change = ""
        language= spinner?.selectedItem.toString()

          if(language.equals("Portuguese"))
          {
              val config = resources.configuration
              val locale = Locale("pt")
              Locale.setDefault(locale)
              config.locale = locale
              resources.updateConfiguration(config, resources.displayMetrics)
              username.setHint(resources.getText(R.string.prompt_username))
              password.setHint(resources.getText(R.string.prompt_password))
              remember_me.setText(R.string.remember_me)
              welcometext.setText(R.string.welcome)
              login.setText(R.string.login)
          }else {
              val config = resources.configuration
              val locale = Locale("en")
              Locale.setDefault(locale)
              config.locale = locale
              resources.updateConfiguration(config, resources.displayMetrics)
              username.setHint(resources.getText(R.string.prompt_username))
              password.setHint(resources.getText(R.string.prompt_password))
              remember_me.setText(R.string.remember_me)
              welcometext.setText(R.string.welcome)
              login.setText(R.string.login)
          }
    }
}


/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
