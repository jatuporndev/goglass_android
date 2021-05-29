package com.example.goglasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var textCartItemCount: TextView? = null
    var userID: String? = null
    val localMain:String="th"
    val appPreference:String = "appPrefer"
    override fun onCreate(savedInstanceState: Bundle?) {
        var bottomNavigationView: BottomNavigationView

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val sharedPrefer = getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.checkItem(R.id.nav_home)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, HomeFragment())
        transaction.commit()

        val callback = onBackPressedDispatcher.addCallback(this) {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Do you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> finish() })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            val alert: AlertDialog = builder.create()
            alert.show()
        }
        callback.isEnabled

        navView.setOnNavigationItemSelectedListener {
            var fm: Fragment = HomeFragment()
            when (it.itemId) {
                R.id.nav_home -> fm = HomeFragment()
                R.id.nav_history -> fm = HistoryFragment()
                R.id.nav_shop -> fm = ShopFragment()
               // R.id.nav_review -> fm = ReviewFragment()
                R.id.nav_user -> fm = UserFragment()

            }
            //this.supportActionBar!!.title = "Home"

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fm)
            transaction.commit()
            changeToolbarhome()
            return@setOnNavigationItemSelectedListener true
        }


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu!!.findItem(R.id.nav_cart)

        val actionView = menuItem.actionView
        textCartItemCount = actionView.findViewById<View>(R.id.cart_badge) as TextView
        setupBadge(countcart())
        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        return when (item.itemId) {

            R.id.nav_cart -> {

                transaction.replace(R.id.nav_host_fragment, CartFragment())
                transaction.commit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



    internal fun BottomNavigationView.checkItem(actionId: Int) {
        menu.findItem(actionId)?.isChecked = true
    }

     fun setupBadge(mCartItemCount:Int ) {
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount!!.visibility != View.GONE) {
                    textCartItemCount!!.visibility = View.GONE
                }
            } else {
                textCartItemCount!!.text = Math.min(mCartItemCount, 99).toString()
                if (textCartItemCount!!.visibility != View.VISIBLE) {
                    textCartItemCount!!.visibility = View.VISIBLE
                }
            }
        }
    }

    fun setl(local:String){
        val res :Resources=resources
        val dm:DisplayMetrics = res.displayMetrics
        val conf : Configuration = res.configuration
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(Locale(local.toLowerCase()))
        }else {
            conf.locale = Locale(local.toLowerCase())
        }
        res.updateConfiguration(conf,dm)
        val refresh = Intent(this,MainActivity::class.java)
        //Create shared preference to store local data
        val sharedPrefer: SharedPreferences =
            getSharedPreferences(appPreference, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefer.edit()
        var la = local
        editor.putString(localMain, la)

        editor.commit()
        startActivity(refresh)
        finish()
    }

// use
    fun changeToolbar(){
    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
    mTitle.visibility = View.VISIBLE
    mTitle.text=getString(R.string.edituser)
    supportActionBar?.title = mTitle.toString()
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

    toolbar.setNavigationOnClickListener {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, UserFragment())
        transaction.commit()
    }

}
    fun changeToolbarcomment(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
        mTitle.text=getString(R.string.userComment)
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, ReviewFragment())
            transaction.commit()
        }

    }
    fun changeToolbarLike(productID:String){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
            mTitle.text="ถูกใจ"
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {

            val sharedPrefer = getSharedPreferences(
                    LoginActivity().appPreference, Context.MODE_PRIVATE)
            val bundle = Bundle()
            bundle.putString("productID", productID)
            val fm = ShopPage2Fragment()
            fm.arguments = bundle;
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }

    }
    fun changeToolbarResetPas(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
        mTitle.text=getString(R.string.Newpassword)
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {
            val sharedPrefer = getSharedPreferences(
                    LoginActivity().appPreference, Context.MODE_PRIVATE)
           var userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
            val bundle = Bundle()
            bundle.putString("userID", userID)
            val fm = UserUpdateFragment()
            fm.arguments = bundle;
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }

    }
    fun changeToolbarOrderdetal(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
        mTitle.text="รายระเอียดการซื้อ"
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, HistoryFragment())
            transaction.commit()
        }

    }
    fun changeToolbarShop2(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
        mTitle.text=getText(R.string.Product)
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, ShopFragment())
            transaction.commit()
        }

    }
    fun changeToolbarhome(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.INVISIBLE

    }
    fun changeToolbarhelp(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
        mTitle.text=getText(R.string.Help)
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, UserFragment())
            transaction.commit()
        }

    }
    fun changeToolbarsetting(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
        mTitle.text=getString(R.string.setting)
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, UserFragment())
            transaction.commit()
        }

    }
    private fun countcart():Int
    {
        var count = 0
        var url: String = getString(R.string.root_url) + getString(R.string.countCart_url) + userID

        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .get()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        count =data.getInt("COUNT(cartID)")

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return count
    }


}
