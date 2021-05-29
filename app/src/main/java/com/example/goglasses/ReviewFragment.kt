package com.example.goglasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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


class ReviewFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var band = java.util.ArrayList<Band>()
    private var producttype = java.util.ArrayList<Producttype>()
    var userlike = ArrayList<String>()
    var userID: String? = null
    var userstatus: String? = null
    var bandID = ""
    var producttypeID = ""
    var spinnerBand: Spinner? = null
    var spinnerProducttype: Spinner? = null
    var parce: Parcelable? = null
    var lastFirstVisiblePosition =0

    var productIDA = java.util.ArrayList<String>()
    var index = -1
    var top = -1
    var mLayoutManager: LinearLayoutManager? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_review, container, false)
        val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        (activity as? MainActivity)?.changeToolbarhome()
        (activity as? MainAdminActivity)?.changeToolbarhome()


        //List data
        spinnerBand = root.findViewById(R.id.spinreviewband)
        spinnerProducttype = root.findViewById(R.id.spinrevIewtype)
        recyclerView = root.findViewById(R.id.recyclerViewReview)
        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        mLayoutManager = LinearLayoutManager(requireContext());
        recyclerView?.setHasFixedSize(true);
        recyclerView?.setLayoutManager(mLayoutManager);

        band.add(Band("", "-All-"))
        producttype.add(Producttype("", "-All-"))

        productIDA.clear()
        listband()
        listtype()
        showLikes(userID.toString())
        //showDataList()

        val adapterband = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, band)
        spinnerBand?.adapter = adapterband
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinnerBand?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val band = spinnerBand!!.selectedItem as Band
                bandID = band.bandID

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

        return root
    }


    private fun showDataList() {

        if(index != -1)
        {
            mLayoutManager?.scrollToPositionWithOffset( index, top);

        }
        val data = ArrayList<Data>()
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
                                    item.getString("likesCount"),
                                    item.getString("commentCount")
                            )
                            )
                            recyclerView!!.adapter = DataAdapter(data)


                        }
                    } else {
                        Toast.makeText(context, "ไม่มีสินค้า",
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
    private fun showLikes(userID: String) {
        productIDA.clear()
        val url: String = getString(R.string.root_url) + getString(R.string.userlike_url)+"?userID="+userID
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


                            productIDA.add(item.getString("productID"))


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

    internal class Data(
            var productID: String, var productName: String, var price: String,
            var image: String,var type: String,var bandname:String, var like: String,var countcomment:String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_review,
                    parent, false
            )
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.product_image_url) + data.image
            Picasso.get().load(url).into(holder.imageFileName)
            holder.productName.text = data.productName
            holder.price.text = "\u0E3F" + data.price
            holder.type.text = data.type
            holder.bandname.text = data.bandname
            holder.like.text = data.like+" คน ถูกใจสิ่งนี้"
            holder.like.setOnClickListener{

                val bundle = Bundle()
                bundle.putString("productID", data.productID)
                val fm = UserlikesFragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().
                supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
            }
            holder.btnlike.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24_black)

           if (productIDA.contains(data.productID)) {
               holder.btnlike.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24)
            }else{
               holder.btnlike.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24_black)
          }

            holder.btncomment.setOnClickListener {


                val bundle = Bundle()
                bundle.putString("productID", data.productID)
                val fm = ReviewPage2Fragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().
                supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()

            }
            holder.btnlike.setOnClickListener {
              //  viewLike(data.productID,userID.toString())
                if(userstatus=="0"){
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("กรุณาเข้าสู่ระบบ")
                            .setCancelable(false)
                            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->val intent = Intent(context, LoginActivity::class.java)
                                startActivity(intent) })
                            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                    val alert: AlertDialog = builder.create()
                    alert.show()
                }else {
                    index = mLayoutManager?.findFirstVisibleItemPosition()!!
                    val v: View? = recyclerView?.getChildAt(0)
                    top = if (v == null) 0 else (v.top - (recyclerView?.paddingTop!!))
                    if (productIDA.contains(data.productID)) {
                        deleteLike(data.productID,userID.toString())
                    } else {
                        addLikes(data.productID)

                    }
                    showLikes(userID.toString())
                }

                 }
            holder.commentcount.text = "ดูความเห็นทั้ง "+data.countcomment+ " รายการ"
            holder.commentcount.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("productID", data.productID)
                val fm = ReviewPage2Fragment()
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
            var productName: TextView = itemView.findViewById(R.id.txtproductreviewname)
            var type: TextView = itemView.findViewById(R.id.txtreviewtype)
            var like: TextView = itemView.findViewById(R.id.txtlike)
            var bandname : TextView = itemView.findViewById(R.id.txtreviewbandname)
            var price: TextView = itemView.findViewById(R.id.txtpricereview)
            var btncomment: ImageButton = itemView.findViewById(R.id.imgbtncommnet)
            var btnlike: ImageButton = itemView.findViewById(R.id.imgbtnlike)
            var imageFileName: ImageView = itemView.findViewById(R.id.imgproductreview)
            var commentcount :TextView = itemView.findViewById(R.id.commentcount)


        }
    }

    private fun listband() {

        val urlProvince: String = getString(R.string.root_url) + getString(R.string.band_url)
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

    private fun addLikes(productID: String)
    {
        val bundle = this.arguments
        var url: String = getString(R.string.root_url) + getString(R.string.addLike_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("productID", productID)
                .add("userID", userID.toString())
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

    private fun deleteLike(productID: String?,userID :String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.deletelike_url) + "?userID="+ userID +"&productID="+productID
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
    private fun userLike(productID: String?)
    {
        var check : Boolean = false
        var url: String = getString(R.string.root_url) + getString(R.string.userlike_url) +"?productID="+productID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .get()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)

                            userlike.add(item.getString("firstname"))


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


