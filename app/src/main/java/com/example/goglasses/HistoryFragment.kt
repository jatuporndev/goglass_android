package com.example.goglasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class HistoryFragment : Fragment() {
    var userstatus: String? = null
    var userID: String? = null
    var spinner: Spinner? = null
    var recyclerView: RecyclerView? = null
    var  datespin:String?=null
    var txtstatus:TextView?=null
    var imgstatus:ImageView?=null
    private var date = java.util.ArrayList<Date>()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_history, container, false)
        (activity as? MainActivity)?.changeToolbarhome()
        spinner = root.findViewById(R.id.spinnerHis)
        recyclerView = root.findViewById(R.id.recyclerViewHistory)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        txtstatus = root.findViewById(R.id.txtstatushis)
        imgstatus =root.findViewById(R.id.imgstatushis)

        if(userstatus=="0"){
            txtstatus?.visibility = View.VISIBLE
            imgstatus?.visibility = View.VISIBLE
            spinner?.visibility = View.INVISIBLE
        }else{
            txtstatus?.visibility = View.INVISIBLE
            imgstatus?.visibility = View.INVISIBLE
            spinner?.visibility = View.VISIBLE
        }



        date.add(Date("-ALL-"))
        listdate()
        val distinctLocations = date.distinctBy { it.date }
        val adapter= ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item, distinctLocations)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val date = spinner!!.selectedItem as Date
                datespin =date.date
                showDataList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        return root
    }
    private fun showDataList() {
        val data = ArrayList<Data>()
        val url:String = getString(R.string.root_url) + getString(R.string.Order_url)+userID+"?order_date="+datespin
        //Log.d("txt",url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(Data(
                                    item.getString("orderID"),
                                    item.getString("userID"),
                                    item.getString("order_date"),
                                    item.getString("order_ship"),
                                    item.getString("status"),
                                    item.getString("totalPrice")
                            )
                            )
                            recyclerView!!.adapter = DataAdapter(data)

                        }
                    } else {
                        if(userstatus=="0") {

                        }else {
                            Toast.makeText(context, getString(R.string.Noorder),
                                    Toast.LENGTH_LONG).show()
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
    }



    internal class Data(
            var orderID: String, var userID: String, var order_date: String,
            var order_ship: String,var status: String,var totalprice: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_history,
                    parent, false
            )
            return ViewHolder(view)
        }
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val status = arrayOf(getString(R.string.wait), getString(R.string.Confirmed), getString(R.string.Shipping), getString(R.string.Delivered))
            val data = list[position]
            holder.data = data
            holder.order_date.text=data.order_date
            holder.orderid.text=data.orderID
            holder.priceorder.text=data.totalprice+"฿"
            holder.statusorder.text=status[Integer.valueOf(data.status)-1]
            val content = SpannableString(getString(R.string.Check))
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            holder.detail.text = content
            holder.detail.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("orderID", data.orderID)
                val fm = Order_detailFragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().
                supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
            }


        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var orderid: TextView = itemView.findViewById(R.id.txtorderid)
            var order_date: TextView = itemView.findViewById(R.id.txtorderdatetime)
            var priceorder : TextView = itemView.findViewById(R.id.txtpriceorder)
            var statusorder: TextView = itemView.findViewById(R.id.txtorderstatus)
            var detail: TextView= itemView.findViewById(R.id.txtvieworderDetail)

        }
    }
    private fun listdate() {

        val url:String = getString(R.string.root_url) + getString(R.string.Order_url)+userID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            date.add(
                                    Date(
                                            item.getString("order_date")
                                    )
                            )
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
    }

    class Date(var date: String) {
        override fun toString(): String {
            return date
        }
    }

}