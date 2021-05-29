package com.example.goglasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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
import java.time.LocalDateTime


class ShopPage2Fragment : Fragment() {

    var image :ImageView?=null
    var productIDbundle:String?=null
    var txtnamepro:TextView?=null
    var txttype:TextView?=null
    var spinlens:Spinner?=null
    var txtlike:TextView?=null
    var txtprice:TextView?=null
    var btnlike:ImageButton?=null
    var addtocart:Button?=null
    var edittextcoment:EditText?=null
    var recyclerView: RecyclerView? = null
    var recyclerViewRecommend: RecyclerView? = null
    var userID: String? = null
    var userstatus: String? = null
    var btncomment:Button?=null
    var lensID = ""
    var txtshowcomment:Button?=null
    var productIDA = java.util.ArrayList<String>()
    private var productCartname = java.util.ArrayList<String>()
    private var lens = java.util.ArrayList<Lens>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_shop_page2, container, false)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val bundle = this.arguments

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        image =root.findViewById(R.id.imgpro)
        txtnamepro=root.findViewById(R.id.txtnameproshop2)
        txttype=root.findViewById(R.id.txtypeshop2)
        spinlens=root.findViewById(R.id.spinnerlensshop2)
        txtlike=root.findViewById(R.id.textView38)
        btnlike=root.findViewById(R.id.btnlike)
        addtocart=root.findViewById(R.id.btnaddtocart2)
        btncomment=root.findViewById(R.id.btneditcomment3)
        edittextcoment=root.findViewById(R.id.edittextcomment4)
        txtprice=root.findViewById(R.id.txtpriceshop2)
        recyclerView=root.findViewById(R.id.recyclerViewcomment)
      //  recyclerViewRecommend=root.findViewById(R.id.recyclerViewRecommend)
        //recyclerViewRecommend?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        txtshowcomment=root.findViewById(R.id.textView31)

        recyclerView?.visibility = View.GONE
        edittextcoment?.visibility = View.GONE
        btncomment?.visibility = View.GONE

        var show = true;
        txtshowcomment?.setOnClickListener {
            if(show){
                recyclerView?.visibility = View.VISIBLE
                edittextcoment?.visibility = View.VISIBLE
                btncomment?.visibility = View.VISIBLE
                txtshowcomment?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_upward_24, 0);
                show = false
            }else{
                recyclerView?.visibility = View.GONE
                edittextcoment?.visibility = View.GONE
                btncomment?.visibility = View.GONE
                txtshowcomment?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_downward_24, 0);
                show = true
            }

        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment,ShopFragment())
            fragmentTransaction.commit()
            (activity as? MainActivity)?.changeToolbarhome()
           // (activity as? MainAdminActivity)?.changeToolbarhome()

        }
        callback.isEnabled

        (activity as? MainActivity)?.changeToolbarShop2()
        //(activity as? MainAdminActivity)?.changeToolbarSh
        productIDbundle=bundle?.get("productID").toString()
        btnlike?.setOnClickListener {
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
                if (productIDA.contains(bundle?.get("productID").toString())) {
                    deleteLike(bundle?.get("productID").toString(), userID.toString())
                } else {
                    addLikes(bundle?.get("productID").toString())

                }
            }
        }
        addtocart?.setOnClickListener {
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
                if (productCartname.contains(txtnamepro?.text.toString())) {
                    Toast.makeText(context, getString(R.string.Alreadyhave), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, getString(R.string.Youchoose)+" " + txtnamepro?.text.toString(), Toast.LENGTH_LONG).show()
                    addtoCart(bundle?.get("productID").toString(), lensID)
                    (activity as? MainActivity)?.setupBadge(countcart())
                }
            }
        }
        txtlike?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("productID",productIDbundle)
            val fm = UserlikesFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        btncomment?.setOnClickListener {
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
                addComment()

            }
        }
        listlens()
        val adapterlens = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, lens)
        spinlens?.adapter = adapterlens
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinlens?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val protype = spinlens!!.selectedItem as Lens
                 lensID = protype.lensID

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

      //  Log.d("txtt",bundle?.get("productID").toString())
        productIDA.clear()
        showLikes()
       // showRecomment()
        showcomment(bundle?.get("productID").toString())
        viewProduct(bundle?.get("productID").toString())



        return root
    }
    private fun viewProduct(productID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.productfromid_url) + productID
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

                        txtnamepro?.text = data.getString("productname")
                        txttype?.text = data.getString("bandName")+" | "+ data.getString("typename")
                        txtprice?.text="ราคา : "+data.getString("price")+"THB"

                        var url = getString(R.string.root_url) +
                                getString(R.string.product_image_url) + data.getString("image")
                        Picasso.get().load(url).into(image)
                        txtlike?.text=getString(R.string.likes)+" "+data.getString("countLike")+" "+getString(R.string.person)
                        addtocart?.text=data.getString("stock")

                    }
                    if(addtocart?.text.toString()=="0"){
                        addtocart?.text = getString(R.string.OutofStock)
                        addtocart?.setTextColor(Color.parseColor("#ff0000"))
                        addtocart?.isEnabled = false
                    }else{
                        addtocart?.text = getString(R.string.addtocart)
                        addtocart?.setTextColor(Color.parseColor("#FF000000"))
                        addtocart?.isEnabled = true
                    }
                    if(data.getString("typename")=="SUNGLASSES"){
                        spinlens?.isEnabled=false
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

    private fun showcomment(productID: String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.review_url)+"?productID="+ productID
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
                                    item.getString("comment"),
                                    item.getString("firstname"),
                                    item.getString("date_time"),
                                    item.getString("image")

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
            var productID: String, var detailcommnet: String, var firstname: String,var date: String,var img: String

    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_comment,
                    parent, false

            )
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.user_image_url) + data.img

            //Picasso.get().load(url).into(holder.imageFileName)
           Picasso.get().load(url).fit().centerCrop().into(holder.imageFileName);
            holder.detailcomment.text = data.detailcommnet
            holder.nameUser.text=data.firstname
            holder.commentdate.text=data.date



        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var detailcomment: TextView = itemView.findViewById(R.id.txtadress)
            var nameUser: TextView = itemView.findViewById(R.id.txtcommentuser)
            var commentdate :TextView = itemView.findViewById(R.id.txtdatecomment)
            var imageFileName: ImageView = itemView.findViewById(R.id.imguserLike)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addComment()
    {
        val current = LocalDateTime.now()
        val bundle = this.arguments
        var url: String = getString(R.string.root_url) + getString(R.string.addComment_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("productID", bundle?.get("productID").toString())
                .add("date_time", current.toString())
                .add("userID", userID.toString())
                .add("comment", edittextcoment?.text.toString())

                //.add("userTypeID", "1")
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
                    showcomment(bundle?.get("productID").toString())
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
    class Lens(var lensID: String, var lensName: String,var lensprice: String) {
        override fun toString(): String {
            return "$lensName   $lensprice:THB"
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

                        viewProduct(bundle?.get("productID").toString())
                        showLikes()

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
        val bundle = this.arguments
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

                    viewProduct(bundle?.get("productID").toString())
                    showLikes()


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
    private fun showLikes() {
        productIDA.clear()
        val bundle = this.arguments
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
                        if (productIDA.contains(bundle?.get("productID").toString())) {
                            btnlike?.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24)
                        }else{
                            btnlike?.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24_black)
                        }
                    } else {
                        if (productIDA.contains(bundle?.get("productID").toString())) {
                            btnlike?.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24)
                        }else{
                            btnlike?.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24_black)
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

/*
    private fun showRecomment() {
        val data = ArrayList<Data2>()
        val url: String = getString(R.string.root_url) + getString(R.string.productRecommend_url)
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
                            data.add(Data2(
                                    item.getString("productname"),
                                    item.getString("image")


                            )
                            )
                            recyclerViewRecommend!!.adapter = DataAdapter2(data)

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

    internal class Data2(
            var productname: String,var img: String
    )
    internal inner class DataAdapter2(private val list: List<Data2>) :
            RecyclerView.Adapter<DataAdapter2.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_productrecommed,
                    parent, false

            )
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.product_image_url) + data.img
            Picasso.get().load(url).into(holder.imageFileName)
            holder.txtproname.text=data.productname





        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data2? = null
            var txtproname: TextView = itemView.findViewById(R.id.txtrecom)
            var imageFileName: ImageView = itemView.findViewById(R.id.imgrecom)

        }
    }
*/
}