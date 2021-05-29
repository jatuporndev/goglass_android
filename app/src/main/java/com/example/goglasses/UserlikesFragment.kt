package com.example.goglasses

import android.os.Bundle
import android.util.Log
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


class UserlikesFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    var txtlike:TextView?=null
    var like:String?=null

    var userID: String? = null
    var userstatus: String? = null
    var usertypeID: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       val root = inflater.inflate(R.layout.fragment_userlikes, container, false)
        recyclerView = root.findViewById(R.id.recyclsviewuserlike)
        txtlike = root.findViewById(R.id.txtlikeall)
        val bundle = this.arguments


        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment,ReviewFragment())
            fragmentTransaction.commit()
            (activity as? MainActivity)?.changeToolbarhome()
            (activity as? MainAdminActivity)?.changeToolbarhome()

        }
        callback.isEnabled

        (activity as? MainActivity)?.changeToolbarLike(bundle?.get("productID").toString())
        (activity as? MainAdminActivity)?.changeToolbarLike()
        showLike(bundle?.get("productID").toString())
        countLike(bundle?.get("productID").toString())
       // Log.d("yyy",bundle?.get("productID").toString())
        return  root
    }
    private  fun countLike(productID: String){
        var url: String = getString(R.string.root_url) +    getString(R.string.countLike_url) + productID
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

                        like =data.getString("countLike")
                        txtlike?.text = getString(R.string.likes)+" "+like+" "+getString(R.string.person)

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
    private fun showLike(productID: String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.userlike_url)+"?productID="+ productID
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
                                item.getString("firstname"),
                                item.getString("lastname"),
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
        var firstname: String,var lastname: String,var img: String

    )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_userlike,
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
            holder.txtname.text=data.firstname+" "+data.lastname

        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txtname: TextView = itemView.findViewById(R.id.txtUserLike)
            var imageFileName: ImageView = itemView.findViewById(R.id.imguserLike)

        }
    }

    }
