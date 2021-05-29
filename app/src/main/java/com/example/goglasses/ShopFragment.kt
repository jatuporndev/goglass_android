package com.example.goglasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ShopFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    private var lens = java.util.ArrayList<Lens>()
    private var band = java.util.ArrayList<Band>()
    private var producttype = java.util.ArrayList<Producttype>()
    private var productCartname = java.util.ArrayList<String>()
    var lensID = ""
    var bandID = ""
    var producttypeID = ""
    var spinnerLens: Spinner? = null
    var spinnerBand: Spinner? = null
    var spinnerProducttype: Spinner? = null

    var userID: String? = null
    var userstatus: String? = null
    var usertypeID: String? = null
    lateinit var adapterlens: ArrayAdapter<Lens>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_shop, container, false)
        val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        (activity as? MainActivity)?.changeToolbarhome()
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        usertypeID = sharedPrefer?.getString(LoginActivity().userTypePreference, null)
        //List data
        spinnerLens = root.findViewById(R.id.shopspinlend)
        spinnerBand = root.findViewById(R.id.spinreviewband)
        spinnerProducttype = root.findViewById(R.id.spinrevIewtype)
        recyclerView = root.findViewById(R.id.recyclerViewShop)



        band.add(Band(" ", "-All-"))
        producttype.add(Producttype(" ", "-All-"))
        listband()
        listtype()
        listlens()

        showDataList()


        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))

        val adapterband = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, band)
        spinnerBand?.adapter = adapterband
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinnerBand?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val band = spinnerBand!!.selectedItem as Band
                bandID = band.bandID
                showDataList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val adapterType = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, producttype)
        spinnerProducttype?.adapter = adapterType
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinnerProducttype?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val protype = spinnerProducttype!!.selectedItem as Producttype
                producttypeID = protype.producttypeID
                showDataList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
         adapterlens = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, lens)
        return root
    }



    private fun showDataList() {
        val data = ArrayList<Data>()
        data.clear()
        val url: String = getString(R.string.root_url) + getString(R.string.product_url)+"?productTypeID="+producttypeID+"&bandID="+bandID
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
                                    item.getString("productID"),
                                    item.getString("productname"),
                                    item.getString("price"),
                                    item.getString("image"),
                                    item.getString("typename"),
                                    item.getString("bandName"),
                                    item.getInt("stock"),
                                    item.getString("likesCount")
                            )
                            )
                            recyclerView!!.adapter = DataAdapter(data)

                        }
                    } else {
                        Toast.makeText(context, getString(R.string.noproduct),
                                Toast.LENGTH_LONG).show()
                        spinnerBand?.setSelection(0)
                        spinnerProducttype?.setSelection(0)
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
            var productID: String, var productName: String, var price: String,
            var image: String,var type: String,var bandname:String,var Stock:Int,var like:String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_shop,
                    parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.product_image_url) + data.image
            Picasso.get().load(url).fit().centerCrop().into(holder.imageFileName);
           // Picasso.get().load(url).into(holder.imageFileName)
            holder.productName.text = data.productName
            holder.price.text = "\u0E3F" + data.price
            holder.type.text = data.type
            holder.bandname.text = data.bandname
            holder.addToCart.text=data.Stock.toString()

            holder.lendspiner.adapter = adapterlens

            if(data.type=="SUNGLASSES"){
                holder.lendspiner.isEnabled=false
            }
            holder.txtlike.text = getString(R.string.likes)+" "+data.like+" "+getString(R.string.person)

            holder.imageFileName.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("productID", data.productID)
                val fm = ShopPage2Fragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().
                supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
            }

            if(holder.addToCart.text.toString()=="0"){
                holder.addToCart.text = getString(R.string.OutofStock)
                holder.addToCart.setTextColor(Color.parseColor("#ff0000"))
                holder.addToCart.isEnabled = false
            }else{
                holder.addToCart.text = getString(R.string.addtocart)
                holder.addToCart.setTextColor(Color.parseColor("#FF000000"))
                holder.addToCart.isEnabled = true
            }

            if(usertypeID=="2") {
                holder.addToCart.isEnabled = false
                holder.addToCart.text = "มี "+data.Stock+" ชิ้น"
           }



            holder.lendspiner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    val lens =  holder.lendspiner!!.selectedItem as Lens
                    holder.idlens= lens.lensID
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            holder.addToCart.setOnClickListener {
                if(userstatus=="0"){
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setMessage(getString(R.string.PleseLogin))
                            .setCancelable(false)
                            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->val intent = Intent(context, LoginActivity::class.java)
                                startActivity(intent) })
                            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                    val alert: AlertDialog = builder.create()
                    alert.show()
                }else{
                    getcart()
                    if (productCartname.contains(data.productName)) {
                        Toast.makeText(context,  getString(R.string.Alreadyhave), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, getString(R.string.Youchoose)+" " + data.productName, Toast.LENGTH_LONG).show()
                        addtoCart(data.productID, holder.idlens.toString())
                        (activity as? MainActivity)?.setupBadge(countcart())
                    }
            }

            }
        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var productName: TextView = itemView.findViewById(R.id.txtproductreviewname)
            var type: TextView = itemView.findViewById(R.id.txtshoptypename)
            var bandname :TextView = itemView.findViewById(R.id.txtreviewbandname)
            var price: TextView = itemView.findViewById(R.id.txtshoprice)
            var addToCart: Button = itemView.findViewById(R.id.btnaddtocart)
            var imageFileName: ImageView = itemView.findViewById(R.id.imgshop)
            var lendspiner: Spinner = itemView.findViewById(R.id.shopspinlend)
            var idlens:String?=null
            var txtlike:TextView=itemView.findViewById(R.id.txtshoplike)
        }
    }
    private fun listlens() {
       // lens.add(Lens("0", "-All-"))
        val urlProvince: String = getString(R.string.root_url) + getString(R.string.lens_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlProvince).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            lens.add(
                                    Lens(
                                            item.getString("lensID"),
                                            item.getString("lenstype"),
                                            item.getString("lensprice")
                                    )
                            )
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }
    private fun listband() {

        val urlProvince: String = getString(R.string.root_url) + getString(R.string.band_url)
        Log.d("txt",urlProvince)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlProvince).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            band.add(
                                    Band(
                                            item.getString("bandID"),
                                            item.getString("bandName")
                                    )
                            )
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }
    private fun listtype() {

        val urlProvince: String = getString(R.string.root_url) + getString(R.string.producttype_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlProvince).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            producttype.add(
                                    Producttype(
                                            item.getString("producttypeID"),
                                            item.getString("typename")
                                    )
                            )
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    class Lens(var lensID: String, var lensName: String,var lensprice: String) {
        override fun toString(): String {
            return "$lensName   $lensprice:THB"
        }

    }
    class Band(var bandID: String, var bandName: String) {
        override fun toString(): String {
            return bandName
        }

    }
    class Producttype(var producttypeID: String, var producttypeName: String) {
        override fun toString(): String {
            return producttypeName
        }

    }
    private fun addtoCart(productID: String,lensID: String)
    {
        val bundle = this.arguments
        var url: String = getString(R.string.root_url) + getString(R.string.addtoCart_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("productID", productID)
                .add("userID", userID.toString())
                .add("lensID", lensID)
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
    private fun countcart():Int
    {
        var count = 0
        var url: String = getString(R.string.root_url) + getString(R.string.countCart_url) + userID
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
    private fun getcart() {
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
                            productCartname.add(item.getString("productName"))
                        }
                    } else {


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
