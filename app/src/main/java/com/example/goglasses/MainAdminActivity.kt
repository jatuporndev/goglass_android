package com.example.goglasses

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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

class MainAdminActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var textCartItemCount: TextView? = null
    var userID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        var bottomNavigationView: BottomNavigationView

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)
        val sharedPrefer = getSharedPreferences(
            LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.checkItem(R.id.nav_home)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, AdminOrderFragment())
        transaction.commit()

        val callback = onBackPressedDispatcher.addCallback(this) {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainAdminActivity)
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
                R.id.nav_home -> fm = AdminOrderFragment()
              //  R.id.nav_history -> fm = AdminOrderFragment()
                //R.id.nav_shop -> fm = ShopFragment()
                R.id.nav_report -> fm = ReportFragment()
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

    // use
    fun changeToolbar(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
        mTitle.text="แก้ไขข้อมูล"
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
        mTitle.text="แสดงความคิดเห็น"
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, ReviewFragment())
            transaction.commit()
        }

    }
    fun changeToolbarLike(){
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

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, ReviewFragment())
            transaction.commit()
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
        mTitle.text="รับรหัสผ่านใหม่"
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
            transaction.replace(R.id.nav_host_fragment, AdminOrderFragment())
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
        mTitle.text="ช่วยเหลือ"
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, UserFragment())
            transaction.commit()
        }

    }
    fun changeToolbarbill(orderID:String){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_titlemain) as TextView
        mTitle.visibility = View.VISIBLE
        mTitle.text="ใบเสร็จ"
        supportActionBar?.title = mTitle.toString()
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24_b);

        toolbar.setNavigationOnClickListener {

            val bundle = Bundle()
            bundle.putString("orderID", orderID)
            val fm = OrderDetailAdminFragment()
            fm.arguments = bundle;
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
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
