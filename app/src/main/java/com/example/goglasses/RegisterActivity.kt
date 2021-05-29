package com.example.goglasses

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class RegisterActivity : AppCompatActivity() {

    var editRegisUsername: EditText? =null
    var editRegisPassword: EditText? =null
    var editRegisFirstname: EditText? =null
    var editRegisLastname: EditText? =null
    var editRegisPhone: EditText? =null
    var editRegisEmail: EditText? =null
    var btnRegis: Button? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val textView = findViewById<TextView>(R.id.txtregister)
        editRegisUsername = findViewById(R.id.editupdateUsername)
        editRegisPassword = findViewById(R.id.editupdatePassword)
        editRegisFirstname = findViewById(R.id.editupdateFirstname)
        editRegisLastname = findViewById(R.id.editupdateLastname)
        editRegisPhone = findViewById(R.id.editupdatePhone)
        editRegisEmail = findViewById(R.id.editRegisEmail)
        btnRegis = findViewById(R.id.btnRegis)

        val toolbar: Toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mTitle = toolbar.findViewById<View>(R.id.toolbar_title) as TextView
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);

        toolbar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    LoginActivity::class.java
                )
            )
        }
        //////

        // load the animation
        val img = findViewById<View>(R.id.imglogo) as ImageView
        val aniSlide2 = AnimationUtils.loadAnimation(applicationContext,R.anim.zoom_out)
        img.startAnimation(aniSlide2)


        btnRegis?.setOnClickListener {
            register()
        }

    }
    private fun register()
    {
        var url: String = getString(R.string.root_url) + getString(R.string.register_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("firstName", editRegisFirstname?.text.toString())
                .add("lastName", editRegisLastname?.text.toString())
                .add("email", editRegisEmail?.text.toString())
                .add("username", editRegisUsername?.text.toString())
                .add("password", editRegisPassword?.text.toString())
                //.add("gender", if (radioWoman!!.isChecked) "1" else "0")
                .add("Phone", editRegisPhone?.text.toString())
               
                //.add("userTypeID", "1")
                .build()

        val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        Toast.makeText(this, "สมัครสมาชิกเรียบร้อยแล้ว", Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
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
    }

}