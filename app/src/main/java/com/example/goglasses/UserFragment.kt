package com.example.goglasses

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class UserFragment : Fragment() {

    var userstatus: String? = null
    var userID: String? = null
    var txtinfo: TextView? = null
    var btnlogout: Button? = null
    var btnedituser: Button? = null
    var btnhelp: Button? = null
    var btnSetting: Button? = null
    var txtFullname: TextView? = null
    var txtcountordertail: TextView? = null
    var userimage: ImageView? = null
    var txtEmail: TextView? = null
    var txtAddress: TextView? = null
    var txtPhone: TextView? = null
    var txtGender: TextView? = null
    var txtusername: TextView? = null
    var txttel: TextView? = null
    var btnuserlogin: Button? = null
    var btnuserregis: Button? = null
    var txtcommemtcount:TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)

        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        (activity as? MainActivity)?.changeToolbarhome()
        (activity as? MainAdminActivity)?.changeToolbarhome()


        val root = inflater.inflate(R.layout.fragment_user, container, false)
        txtcountordertail = root.findViewById<TextView>(R.id.txtcountcrderdetail)
        txtinfo = root.findViewById<TextView>(R.id.txtinfo)
        btnlogout = root.findViewById<Button>(R.id.btnlogout)
        btnedituser = root.findViewById<Button>(R.id.btnedituser)
        txtFullname = root.findViewById(R.id.txtFullname)
        txtPhone = root.findViewById(R.id.txtphone)
        txtEmail = root.findViewById(R.id.txtemail)
        txtusername = root.findViewById(R.id.txtusername)
        userimage =  root.findViewById(R.id.updateimage)
        btnSetting = root.findViewById(R.id.btnsetting)
        btnuserlogin =  root.findViewById(R.id.btnuserlogin)
        txttel =  root.findViewById(R.id.txttel)
        txtcommemtcount =root.findViewById(R.id.txtcountcomment)
        btnuserregis=root.findViewById(R.id.btnuserregis)
        btnhelp=root.findViewById(R.id.btnhelp)

        btnSetting?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment,SettingFragment())
            fragmentTransaction.commit()
        }

        btnhelp?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment,HelpFragment())
            fragmentTransaction.commit()
        }

        val content = SpannableString(getString(R.string.detail))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        txtinfo?.text = content

        if (userstatus == "0") {
            btnuserlogin?.visibility = View.VISIBLE
            btnuserregis?.visibility = View.VISIBLE
            txttel?.visibility = View.INVISIBLE
            txtinfo?.visibility = View.INVISIBLE
            btnedituser?.visibility = View.GONE

        }else {
            btnuserlogin?.visibility = View.INVISIBLE
            btnuserregis?.visibility = View.INVISIBLE
            txttel?.visibility = View.VISIBLE
            txtinfo?.visibility = View.VISIBLE
            btnedituser?.visibility = View.VISIBLE
        }
        txtcommemtcount?.text =countcomment(userID)
        txtcountordertail?.text =countbbay(userID)
        viewUser(userID)
        return root

    }


    private fun viewUser(userID: String?) {


        var url: String = getString(R.string.root_url) + getString(R.string.user_url) + userID
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

                        var imgUrl = getString(R.string.root_url) +
                                getString(R.string.user_image_url) +
                                data.getString("image")

                        //Picasso.get().load(imgUrl).into(userimage)
                        Picasso.get().load(imgUrl).fit().centerCrop().into(userimage);
                        var txtFirstName = data.getString("firstname")
                        var txtLastName = data.getString("lastname")
                        txtEmail?.text = data.getString("email")
                        txtusername?.text = data.getString("username")
                        txtPhone?.text = Html.fromHtml("<u> " + data.getString("phone") + "</u>")
                        //txtGender?.text = if(data.getString("gender") == "0") "ชาย" else "หญิง"
                        txtFullname?.text = "$txtFirstName $txtLastName"
                        var address = data.getString("address")

                        txtinfo?.setOnClickListener {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle(getString(R.string.personinfo))
                            ///////////////////
                            builder.setMessage(getString(R.string.adress)+"\n$address")

                            builder.setPositiveButton("OK") { dialog, which ->
                            }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }


                        btnedituser?.setOnClickListener {

                            val bundle = Bundle()
                            bundle.putString("userID", userID)

                            val fm = UserUpdateFragment()
                            fm.arguments = bundle;
                            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                            fragmentTransaction.commit()
                        }

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
        btnuserlogin?.setOnClickListener {

            val sharePrefer = requireContext().getSharedPreferences(
                    LoginActivity().appPreference,
                    Context.MODE_PRIVATE
            )
            val editor = sharePrefer.edit()
            editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences

            editor.commit() // ยืนยันการแก้ไข preferences
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        btnuserregis?.setOnClickListener {
            val sharePrefer = requireContext().getSharedPreferences(
                    LoginActivity().appPreference,
                    Context.MODE_PRIVATE
            )
            val editor = sharePrefer.edit()
            editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences

            editor.commit() // ยืนยันการแก้ไข preferences
            val intent = Intent(context, RegisterActivity::class.java)
            startActivity(intent)
        }


        btnlogout?.setOnClickListener {

            val sharePrefer = requireContext().getSharedPreferences(
                    LoginActivity().appPreference,
                    Context.MODE_PRIVATE
            )
            val editor = sharePrefer.edit()
            editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences

            editor.commit() // ยืนยันการแก้ไข preferences

            //return to login page
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    private fun countcomment(userID: String?) :String {

        var comment:String ="0"
        var url: String = getString(R.string.root_url) + getString(R.string.countComment_url) + userID
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

                        comment = data.getString("countCommnet")


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
                comment  = "0"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return comment
    }
    private fun countbbay(userID: String?) :String {

        var comment:String ="0"
        var url: String = getString(R.string.root_url) + getString(R.string.countorderdrtail_url) + userID
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

                        comment = data.getString("countorderdetail")


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
                comment  = "0"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return comment
    }
}