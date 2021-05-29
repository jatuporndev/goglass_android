package com.example.goglasses

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    val appPreference:String = "appPrefer"
    val userIdPreference:String = "userIdPref"
    val userstatus:String ="0"
    val usernamePreference:String = "usernamePref"
    val userTypePreference:String = "userTypePref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sharedPrefer: SharedPreferences =
            getSharedPreferences(appPreference, Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPrefer.edit()
        editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences
        editor.commit() // ยืนยันการแก้ไข preferences

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        val textView = findViewById<TextView>(R.id.txtregister)
        val content = SpannableString("No account? Create one")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = content

        val textView2 = findViewById<TextView>(R.id.txtskip)
        val content2 = SpannableString("Skip")
        content2.setSpan(UnderlineSpan(), 0, content2.length, 0)
        textView2.text = content2

        val btnloging = findViewById<Button>(R.id.btnRegis)
        val editUsername = findViewById<EditText>(R.id.editTextUsername)
        val editPassword = findViewById<EditText>(R.id.editTextPassword)
        val textViewSkip = findViewById<TextView>(R.id.txtskip)

        textViewSkip.setOnClickListener {

            val sharedPrefer: SharedPreferences =
                getSharedPreferences(appPreference, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPrefer.edit()
            editor.putString(userstatus,"0")
            editor.commit()


            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        textView.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnloging.setOnClickListener {
            val url = getString(R.string.root_url) + getString(R.string.login_url)
            val okHttpClient = OkHttpClient()

            val formBody: RequestBody = FormBody.Builder()
                    .add("username", editUsername.text.toString())
                    .add("password", editPassword.text.toString())
                    .build()
            val request: Request = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()
            try {
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    try {
                        val obj = JSONObject(response.body!!.string())
                        val userID = obj["userID"].toString()
                        val username = obj["username"].toString()
                        val userTypeID = obj["usertypeID"].toString()


                        //Create shared preference to store user data
                        val sharedPrefer: SharedPreferences =
                                getSharedPreferences(appPreference, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPrefer.edit()

                        editor.putString(userIdPreference, userID)
                        editor.putString(usernamePreference, username)
                        editor.putString(userTypePreference, userTypeID)
                        editor.putString(userstatus,"1")
                        editor.commit()
                        var intent = Intent(applicationContext, MainActivity::class.java)
                        //return to login page
                        when(userTypeID){
                            "1" -> intent = Intent(applicationContext, MainActivity::class.java)
                            "2" -> intent = Intent(applicationContext, MainAdminActivity::class.java)
                            "3" -> intent = Intent(applicationContext, MainAdminActivity::class.java)
                        }
                        startActivity(intent)
                        finish()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_LONG).show()
                    }
                } else {
                    response.code
                    Toast.makeText(applicationContext, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()


            }
        }


    }
}