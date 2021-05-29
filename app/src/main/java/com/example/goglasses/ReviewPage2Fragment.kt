package com.example.goglasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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


class ReviewPage2Fragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var imagereview2:ImageView? =  null
    var txtnamereview2:TextView? =null
    var txttypereview2:TextView? =null
    var txtbandreview2:TextView? =null
    var edittextcoment:EditText? =null
    var file:TextView? =null
    var btncomment:Button? =null
    var userID: String? = null
    var userstatus: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val bundle = this.arguments
        val root =inflater.inflate(R.layout.fragment_review_page2, container, false)

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userstatus = sharedPrefer?.getString(LoginActivity().userstatus, null)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)

        (activity as? MainActivity)?.changeToolbarhome()

        imagereview2 = root.findViewById(R.id.imagepro)
        txtnamereview2= root.findViewById(R.id.txtnamepro)
        txttypereview2= root.findViewById(R.id.txttype2)
        txtbandreview2= root.findViewById(R.id.txtband2)
        edittextcoment = root.findViewById(R.id.edittextcomment)
        btncomment= root.findViewById(R.id.btneditcomment)
        recyclerView= root.findViewById(R.id.recyclerViewcomment)


        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, ReviewFragment())
            fragmentTransaction.commit()
            (activity as? MainActivity)?.changeToolbarhome()
        }
        (activity as? MainActivity)?.changeToolbarcomment()
        callback.isEnabled


        btncomment?.setOnClickListener {
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
                addComment()
            }
        }

        viewProduct(bundle?.get("productID").toString())
        showcomment(bundle?.get("productID").toString())
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

                        txtnamereview2?.text = data.getString("productname")
                        txtbandreview2?.text = data.getString("bandName")

                        txttypereview2?.text = data.getString("typename")
                        var url = getString(R.string.root_url) +
                                getString(R.string.product_image_url) + data.getString("image")
                        Picasso.get().load(url).into(imagereview2)
                        /*
                        // val fragmentTransaction = fragmentManager!!.beginTransaction()
                        btnCancel!!.setOnClickListener {
                            val fragmentTransaction = requireActivity().
                            supportFragmentManager.beginTransaction()
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.replace(R.id.nav_host_fragment, UserFragment())
                            fragmentTransaction.commit()

                        }
                        */
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
            Picasso.get().load(url).into(holder.imageFileName)
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

                    edittextcoment?.text?.clear()
                        val fm = ReviewPage2Fragment()
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

