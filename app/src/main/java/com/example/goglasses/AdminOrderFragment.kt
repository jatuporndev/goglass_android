package com.example.goglasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
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
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class AdminOrderFragment : Fragment() {
    var userstatus: String? = null
    var userID: String? = null
    var spinner: Spinner? = null
    var spinnerstatus: Spinner? = null
    var recyclerView: RecyclerView? = null
    var  datespin:String?=null
    var  statusspin:String?=null
    var txtstatus:TextView?=null
    var imgstatus:ImageView?=null
    private var date = java.util.ArrayList<Date>()
    private var status = java.util.ArrayList<Status>()
    var recyclerViewState: Parcelable? =null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_admin_order, container, false)
        (activity as? MainAdminActivity)?.changeToolbarhome()
        spinner = root.findViewById(R.id.spinnerHis)
        recyclerView = root.findViewById(R.id.recyclerViewHistory)
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        txtstatus = root.findViewById(R.id.txtstatushis)
        spinnerstatus =root.findViewById(R.id.spinnerHis2)
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

        status.add(Status("-ALL-","-STATUS-"))
        status.add(Status("1","รอการยืนยัน"))
        status.add(Status("2","ยืนยันแล้ว"))
        status.add(Status("3","กำลังจัดส่ง"))
        status.add(Status("4","จัดส่งแล้ว"))


        date.add(Date("-DATE-"))
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
        val adapterstatus= ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, status)
        spinnerstatus?.adapter = adapterstatus
        spinnerstatus?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val date = spinnerstatus!!.selectedItem as Status
                statusspin =date.status
                showDataList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        return root
    }
    private fun showDataList() {
        recyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState);
        val data = ArrayList<Data>()
        val url:String = getString(R.string.root_url) + getString(R.string.adminorder_url)+"?order_date="+datespin+"&status="+statusspin
       // Log.d("txt1",datespin.toString()+" "+statusspin.toString())
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("txt1",datespin.toString()+" "+statusspin.toString())
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
                                item.getString("totalPrice"),
                                item.getString("firstname"),
                                item.getString("lastname"),
                                item.getString("address"),
                                item.getString("phone")
                            )
                            )
                            recyclerView!!.adapter = DataAdapter(data)

                        }
                    } else {
                        recyclerView!!.adapter = DataAdapter(data)
                        if(userstatus=="0") {

                        }else {

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
        var order_ship: String,var status: String,var totalprice: String,
        var firstname: String,var lastname: String,var address: String,var phone: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_order_admin,
                parent, false
            )
            return ViewHolder(view)
        }
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val status = arrayOf("รอการยืนยัน", "ยืนยันแล้ว", "กำลังจัดส่ง", "จัดส่งแล้ว")
            val data = list[position]
            holder.data = data
            holder.order_date.text=data.order_date
            holder.orderid.text=data.orderID
            holder.priceorder.text=data.totalprice+" บาท"
            holder.statusorder.text=status[Integer.valueOf(data.status)-1]
            val content = SpannableString("ตรวจสอบ")
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            holder.detail.text = content
            val content2 = SpannableString("ข้อมูลติดต่อ")
            content2.setSpan(UnderlineSpan(), 0, content2.length, 0)
            holder.txtadress.text=content2
            when(data.status){
                "2"-> {
                    holder.btncon.setBackgroundColor(resources.getColor(R.color.purple_700));
                    holder.btncon.text = "จัดส่ง"
                    }
                "3"-> {
                    holder.btncon.setBackgroundColor(resources.getColor(R.color.yellow));
                    holder.btncon.text = "จัดส่งเรียบร้อยแล้วแล้ว"
                }
                "4"-> {
                holder.btncon.setBackgroundColor(resources.getColor(R.color.gray));
                holder.btncon.text = "สำเร็จ" }

            }
            holder.detail.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("orderID", data.orderID)
                val fm = OrderDetailAdminFragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().
                supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
            }
            holder.txtname.text="ชื่อ: "+data.firstname+" "+data.lastname
            holder.btncon.setOnClickListener {
                recyclerViewState = recyclerView?.layoutManager?.onSaveInstanceState()!!
                when(data.status){
                    "1"-> {updatestatus(data.orderID, "2")}
                    "2"-> {updatestatus(data.orderID, "3")
                        addship(data.orderID,getString(R.string.updateship_url))}
                    "3"-> {updatestatus(data.orderID, "4")
                        addship(data.orderID,getString(R.string.updateshiped_url))}
                }

            }
            holder.txtadress.setOnClickListener {

                val builder = android.app.AlertDialog.Builder(requireContext())
                builder.setTitle("ข้อมูลติดต่อ")
                ///////////////////
                builder.setMessage("เบอร์: \n"+ data.phone+"\n"+"ที่อยู่ : \n"+ data.address)

                builder.setPositiveButton("OK") { dialog, which ->
                }
                val dialog: android.app.AlertDialog = builder.create()
                dialog.show()
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
            var btncon:Button= itemView.findViewById(R.id.button2)
            var txtname: TextView = itemView.findViewById(R.id.txtuser)
            var txtadress :TextView=itemView.findViewById(R.id.txtbtnadress)

        }
    }
    private fun updatestatus(orderID: String?,status: String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.updatestatus_url) + orderID
        val okHttpClient = OkHttpClient()
        var request: Request

        val formBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("status", status)
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
                    Toast.makeText(context, "ยืนยันแล้ว", Toast.LENGTH_LONG).show()
                    showDataList()

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
    private fun addship(orderID: String,url:String)
    {
        val bundle = this.arguments
        var url: String = getString(R.string.root_url) + url+orderID
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
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
                        showDataList()

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
    private fun listdate() {

        val url:String = getString(R.string.root_url) + getString(R.string.Orderdate_url)

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
    class Status(var status: String,var statusdetail: String) {
        override fun toString(): String {
            return statusdetail
        }
    }

}