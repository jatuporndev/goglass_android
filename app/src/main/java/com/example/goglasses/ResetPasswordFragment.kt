
package com.example.goglasses

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class ResetPasswordFragment : Fragment() {

    var btnok :Button?=null
    var txtoldpass :TextView?=null
    var  txtnewpass :TextView?=null
    var oldpass:String?=null
    var userID:String? = null
    var userstatus:String? = null
    var Oldpassinput:String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_reset_password, container, false)

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)

        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        txtnewpass =root.findViewById(R.id.edittxtnewpass)
        txtoldpass=root.findViewById(R.id.edittxtoldpass)
        btnok = root.findViewById(R.id.btnpassok)
        viewUser(userID)
        btnok?.setOnClickListener {
            Oldpassinput = txtoldpass?.text.toString()
            var newpass = txtnewpass?.text.toString()
            if(oldpass.equals(hashpass(Oldpassinput+"jatupron"))){
                    if(newpass.isBlank()){
                        Toast.makeText(context, "ระบุรหัสผ่านใหม่", Toast.LENGTH_LONG).show()
                    }else{updatepassUser(userID,newpass)}

            }else{
                Toast.makeText(context, "รหัสผ่านเดิมไม่ถูกต้อง", Toast.LENGTH_LONG).show()
        }
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            val bundle = Bundle()
            bundle.putString("userID", userID)

            val fm = UserUpdateFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
            (activity as? MainActivity)?.changeToolbarhome()
        }
        callback.isEnabled

        (activity as? MainActivity)?.changeToolbarResetPas()
        (activity as? MainAdminActivity)?.changeToolbarResetPas()
        return root
    }



    private fun viewUser(userID: String?)
    {
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

                        oldpass = data.getString("password")

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
    private fun hashpass(hash: String?) : String?
    {
        var hashpss:String?=null
        var url: String = getString(R.string.root_url) + getString(R.string.hashPassword_url) + hash
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

                        hashpss = data.getString("message")

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
          return  hashpss
    }
    private fun updatepassUser(userID: String?,newPass :String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.updatepass_url) + userID+"/?password="+newPass
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("password", newPass)
                .build()
        val request: Request = Request.Builder()
                .url(url)
                .put(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        Toast.makeText(context, "แก้ไข้รหัสผผ่านเสร็จสิ้น", Toast.LENGTH_LONG).show()

                        val bundle = Bundle()
                        bundle.putString("userID", userID)

                        val fm = UserUpdateFragment()
                        fm.arguments = bundle;
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                        fragmentTransaction.commit()
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