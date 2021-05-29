package com.example.goglasses

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BillFragment : Fragment() {
    var img:ImageView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val root =inflater.inflate(R.layout.fragment_bill, container, false)
        val bundle = this.arguments
        img = root.findViewById(R.id.imageView8)
        viewpayment(bundle?.get("orderID").toString())

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment,ReviewFragment())
            fragmentTransaction.commit()
            (activity as? MainAdminActivity)?.changeToolbarbill(bundle?.get("orderID").toString())

        }
        callback.isEnabled

        (activity as? MainAdminActivity)?.changeToolbarbill(bundle?.get("orderID").toString())
        return root
    }

    private fun viewpayment(orderID: String?) {


        var url: String = getString(R.string.root_url) + getString(R.string.paymentall_url) + orderID
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
                                getString(R.string.payment_image_url) +
                                data.getString("image")

                        Picasso.get().load(imgUrl).into(img)


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