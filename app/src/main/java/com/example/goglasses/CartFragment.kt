package com.example.goglasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class CartFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var userID: String? = null
    var userstatus: String? = null
    var btnnext:Button?=null
    var txtTotalprice:TextView?=null
    var totalprice = 0
    var recyclerViewState: Parcelable? =null
    var  datacartC= ArrayList<DataC>()
    var productName:String?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val root = inflater.inflate(R.layout.fragment_cart, container, false)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        recyclerView = root.findViewById(R.id.recyclerViewCart)
        txtTotalprice= root.findViewById(R.id.txttotalprice)
        btnnext= root.findViewById(R.id.button9)

        btnnext?.setOnClickListener {
            if(userstatus=="0"){
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setMessage(getString(R.string.PleseLogin))
                        .setCancelable(false)
                        .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->val intent = Intent(context, LoginActivity::class.java)
                            startActivity(intent) })
                        .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                val alert: AlertDialog = builder.create()
                alert.show()
            }else {
                    if(txtTotalprice?.text.toString()=="0"){
                        Toast.makeText(context, getString(R.string.noproduct), Toast.LENGTH_LONG).show()
                    }else {
                            if(CheckStock()){
                                Toast.makeText(context, getString(R.string.without)+" "+productName+" "+getString(R.string.instock), Toast.LENGTH_LONG).show()
                            }else {
                                val bundle = Bundle()
                                bundle.putString("productprice", txtTotalprice?.text.toString())
                                val fm = Payment1Fragment()
                                fm.arguments = bundle;
                                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                                fragmentTransaction.commit()
                           }
                    }
            }
        }

        showDataList()

        return root
    }
    private fun showDataList() {
        recyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState);
        totalprice=0
        datacartC.clear()
        val datacart = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.viewCart_url)+userID
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

                            datacart.add(Data(
                                    item.getString("lensprice"),
                                    item.getString("lenstype"),
                                    item.getString("lensID"),
                                    item.getString("productName"),
                                    item.getString("productID"),
                                    item.getString("userID"),
                                    item.getString("typename"),
                                    item.getString("bandname"),
                                    item.getString("productPrice"),
                                    item.getString("image"),
                                    item.getString("cartID"),
                                    item.getString("amount"),
                                    item.getString("stock")

                            )
                            )
                            datacartC.add(DataC(item.getString("stock"),item.getString("productName")))
                            recyclerView!!.adapter = DataAdapter(datacart)

                            var  t =(Integer.valueOf(item.getString("productPrice").toString()) *
                                    Integer.valueOf(item.getString("amount").toString()))+
                                    Integer.valueOf(item.getString("lensprice").toString())
                            totalprice+=t


                        }
                        txtTotalprice?.text=totalprice.toString()
                    } else {
                        recyclerView!!.adapter = DataAdapter(datacart)
                        totalprice=0
                        txtTotalprice?.text=totalprice.toString()
                        (activity as? MainActivity)?.setupBadge(countcart())

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
            var lensprice: String, var lenstype: String, var lensID: String,var productName: String,
            var productID: String,var userID: String,var typename: String,var bandname: String,var productPrice: String
            ,var img: String,var CartID: String,var amont: String,var stockpro: String
    )
     class DataC(
     var stockpro: String,var productName: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_cart,
                    parent, false

            )
            return ViewHolder(view)
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.product_image_url) + data.img
            Picasso.get().load(url).into(holder.imageFileName)
            var pricellist = Integer.valueOf(data.productPrice)
            var lensprice = Integer.valueOf(data.lensprice)
            var amount = Integer.valueOf(data.amont)
            holder.txttotal.text = ((pricellist*amount)+lensprice).toString()
            holder.productname.text=data.productName
            holder.bandname.text=data.bandname+" | "+data.typename
            holder.lens.text=getString(R.string.Lens)+" : "+ data.lenstype
            holder.lensprice.text=getString(R.string.lensprice)+" "+data.lensprice


            holder.quty.text=data.amont
            holder.btnremove.setOnClickListener {
                var lQuantity =
                        Integer.valueOf(holder.quty.text.toString())
                if(lQuantity == 1){

                }else {
                    txtTotalprice?.text = (Integer.valueOf(txtTotalprice?.text.toString()) - (pricellist+lensprice)).toString()
                }
                lQuantity -= 1
                var pricellist = Integer.valueOf(data.productPrice)
                var lensprice = Integer.valueOf(data.lensprice)
                if (lQuantity == 0) lQuantity = 1
                holder.quty.text = lQuantity.toString()
                holder.txttotal.text = (((pricellist+ lensprice) * lQuantity)).toString()
                updateamout(data.CartID,holder.quty.text.toString())
                //txtTotalprice?.text=totalprice.toString()
            }
            holder.btnadd.setOnClickListener {
                var lQuantity =
                        Integer.valueOf(holder.quty.text.toString())
                var pricellist = Integer.valueOf(data.productPrice)
                var lensprice = Integer.valueOf(data.lensprice)

                if (lQuantity < Integer.valueOf(data.stockpro)) {

                    lQuantity += 1
                    holder.quty.text = lQuantity.toString()
                    holder.quty.text = lQuantity.toString()
                    holder.txttotal.text = (((pricellist+ lensprice) * lQuantity)).toString()
                    updateamout(data.CartID, holder.quty.text.toString())
                    txtTotalprice?.text = (Integer.valueOf(txtTotalprice?.text.toString()) + (pricellist+lensprice)).toString()

            }else{
                    Toast.makeText(context, getString(R.string.OutofStock), Toast.LENGTH_LONG).show()
                }

            }
            holder.deletecart.setOnClickListener {

              recyclerViewState = recyclerView?.layoutManager?.onSaveInstanceState()!!
                deleteLike(data.CartID) }
                (activity as? MainActivity)?.setupBadge(countcart())

        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var productname: TextView = itemView.findViewById(R.id.productname)
            var bandname: TextView = itemView.findViewById(R.id.bandname)
            var lens : TextView = itemView.findViewById(R.id.lens)
            var lensprice: TextView  = itemView.findViewById(R.id.lensprice)
//            var producttype : TextView = itemView.findViewById(R.id.typepro)
            var quty : TextView = itemView.findViewById(R.id.txtquty)
            var imageFileName : ImageView= itemView.findViewById(R.id.imagorderdetail)
            var deletecart : ImageButton= itemView.findViewById(R.id.deletecart)
            var btnadd :Button= itemView.findViewById(R.id.addquty)
            var btnremove : Button= itemView.findViewById(R.id.removerquty)
            var txttotal :TextView=itemView.findViewById(R.id.txttotal)

        }
    }
    private fun deleteLike(cartID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.deleteCart_url) + cartID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .delete()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {


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
    private fun countcart():Int
    {

        var count = 0
        var url: String = getString(R.string.root_url) + getString(R.string.countCart_url) + userID
       // Log.d("tt",url)
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
                       // Log.d("tt",count.toString())
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

    private fun updateamout(cartID: String?,amont: String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.updatamoutCart_url) + cartID
        val okHttpClient = OkHttpClient()
        var request: Request

        val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", amont)
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
    fun CheckStock():Boolean{
        var c =false
        for(i in datacartC){
            c = i.stockpro=="0"
            productName=i.productName
        }
        return  c
    }
}