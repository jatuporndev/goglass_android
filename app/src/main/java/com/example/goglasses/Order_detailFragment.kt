package com.example.goglasses

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class Order_detailFragment : Fragment() {

    var recyclerView :RecyclerView?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root=   inflater.inflate(R.layout.fragment_order_detail, container, false)
        val bundle = this.arguments
        recyclerView = root.findViewById(R.id.recyclerViewOrderdetail)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, HistoryFragment())
            fragmentTransaction.commit()
            (activity as? MainActivity)?.changeToolbarhome()
        }
        (activity as? MainActivity)?.changeToolbarOrderdetal()
        (activity as? MainAdminActivity)?.changeToolbarOrderdetal()
        callback.isEnabled
        showorderdetail(bundle?.get("orderID").toString())



        return  root
    }

    private fun showorderdetail(orderID: String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.OrderDetail_url)+ orderID
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
                                    item.getString("productID"),
                                    item.getString("LensID"),
                                    item.getString("Quantity"),
                                    item.getString("price_total"),
                                    item.getString("lenstype"),
                                    item.getString("lensprice"),
                                    item.getString("image"),
                                    item.getString("totalPrice"),
                                    item.getString("productname"),
                                    item.getString("bandName"),
                                    item.getString("typename")
                                    )

                            )
                            recyclerView!!.adapter = DataAdapter(data)


                        }

                    } else {
                        //Toast.makeText(context, "ไม่มีความเห็น",
                        // Toast.LENGTH_LONG).show()

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
            var orderID: String, var productID: String,var LensID: String,var Quantity: String,
            var price_total: String, var lenstype: String, var lensprice: String,var image: String,var totalPriceall: String,
            var productname: String,var bandname: String,var protype: String

    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_orderdetail,
                    parent, false

            )
            return ViewHolder(view)
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.product_image_url) + data.image
            Picasso.get().load(url).into(holder.img)
            holder.namepro.text=data.productname
            holder.bandpro.text=data.bandname
            holder.lenspro.text=data.lenstype+" : "+data.lensprice+" ฿"
            holder.quty.text=getString(R.string.amount)+data.Quantity
            holder.totalprice.text=getString(R.string.Totalprice)+data.totalPriceall+" ฿"
            holder.typepro.text=data.protype



        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var namepro: TextView = itemView.findViewById(R.id.productname)
            var bandpro: TextView = itemView.findViewById(R.id.bandname)
            var typepro: TextView = itemView.findViewById(R.id.typepro)
            var lenspro: TextView = itemView.findViewById(R.id.lens)
            var quty: TextView = itemView.findViewById(R.id.qutyorderdetail)
            var totalprice: TextView = itemView.findViewById(R.id.totalpriceorderdetail)
            var img: ImageView= itemView.findViewById(R.id.imagorderdetail)

        }
    }

}