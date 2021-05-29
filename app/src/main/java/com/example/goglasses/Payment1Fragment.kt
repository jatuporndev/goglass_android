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
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Payment1Fragment : Fragment() {

    var userID: String? = null
    var userstatus: String? = null
    var updatephone: TextView? =null
    var updateaddress: TextView? =null
    var btnnext:Button?=null
    var txtprice:TextView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_payment1, container, false)
        val bundle2 = this.arguments
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        updatephone= root.findViewById(R.id.editTextPhone)
        updateaddress= root.findViewById(R.id.txtadress)
        txtprice= root.findViewById(R.id.txtsumPrice)
        btnnext= root.findViewById(R.id.btnnext1)
        viewUser(userID)

        btnnext?.setOnClickListener {
            updateUser(userID)
            val bundle = Bundle()
            bundle.putString("productprice", bundle2?.get("productprice").toString())
            val fm = Paymment2Fragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        txtprice?.text=bundle2?.get("productprice").toString()+" บาท"


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

                        updateaddress?.text = data.getString("address")
                        updatephone?.text = data.getString("phone")
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
    private fun updateUser(userID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.useradress_url) + userID
        val okHttpClient = OkHttpClient()
        var request: Request

            val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("phone", updatephone?.text.toString())
                .addFormDataPart("address", updateaddress?.text.toString())
                .build()
            request= Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                       // Toast.makeText(context, "แก้ไขข้อมูลสมาชิกเรียบร้อยแล้ว", Toast.LENGTH_LONG).show()
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.replace(R.id.nav_host_fragment,UserFragment())
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