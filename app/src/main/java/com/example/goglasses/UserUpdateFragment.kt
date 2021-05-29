package com.example.goglasses

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class UserUpdateFragment : Fragment() {

    var txtback:TextView? =null
    var chengimage :TextView? =null
    var updateusername:TextView? =null
   // var updatepass:TextView? =null
    var updatefirstname:TextView? =null
    var updatelastname:TextView? =null
    var updateemail:TextView? =null
    var updatephone:TextView? =null
    var updateaddress:TextView? =null
    var updateimage:ImageView?=null
    var btnupdate:Button?=null
    var file: File? = null
    var imageFilePath: String? = null
    var resetpass:TextView?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root =  inflater.inflate(R.layout.fragment_user_update, container, false)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        permission()
        val bundle = this.arguments
        updateusername= root.findViewById(R.id.editupdateUsername)
       // updatepass= root.findViewById(R.id.editupdatePassword)
        updatefirstname= root.findViewById(R.id.editupdateFirstname)
        updatelastname = root.findViewById(R.id.editupdateLastname)
        updateemail= root.findViewById(R.id.editupdateemail)
        updatephone= root.findViewById(R.id.editupdatePhone)
        updateaddress= root.findViewById(R.id.txtadress)
        updateimage=root.findViewById(R.id.updateimage)
        btnupdate=root.findViewById(R.id.btnupdate)
        chengimage = root.findViewById<TextView>(R.id.txtchangeimg)
        resetpass = root.findViewById(R.id.txtresetpassupdate)
        val content2 = SpannableString(getString(R.string.choseimage))
        content2.setSpan(UnderlineSpan(), 0, content2.length, 0)
        chengimage?.text = content2
        resetpass?.setOnClickListener {  }
        val content3 = SpannableString(getString(R.string.Newpassword))
        content3.setSpan(UnderlineSpan(), 0, content3.length, 0)
        resetpass?.text = content3
        resetpass?.setOnClickListener {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, ResetPasswordFragment())
            fragmentTransaction.commit()
        }

        chengimage?.setOnClickListener {
            val builder1 = AlertDialog.Builder(requireActivity())
            builder1.setMessage(getString(R.string.chosecamera))
            builder1.setNegativeButton(getString(R.string.gallery)
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 100)
               // imageViewSlip?.visibility = View.VISIBLE
            }
            builder1.setPositiveButton(getString(R.string.Takepicture)
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                var imageURI: Uri? = null
                try {
                    imageURI = FileProvider.getUriForFile(requireActivity(),
                            BuildConfig.APPLICATION_ID.toString() + ".provider",
                            createImageFile()!!)
                } catch (e: IOException) { e.printStackTrace() }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(intent, 200)
               // imageViewSlip?.visibility = View.VISIBLE
            }

            val alert11 = builder1.create()
            alert11.show()
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, UserFragment())
            fragmentTransaction.commit()
            (activity as? MainActivity)?.changeToolbarhome()
            (activity as? MainAdminActivity)?.changeToolbarhome()
        }
        callback.isEnabled

        (activity as? MainActivity)?.changeToolbar()
        (activity as? MainAdminActivity)?.changeToolbar()
        viewUser(bundle?.get("userID").toString())
        btnupdate?.setOnClickListener {
            updateUser(bundle?.get("userID").toString())

        }


        return root
    }
    private fun permission()
    {
        //Set permission to open camera and access a directory
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), 225)
        }
    }
    private fun viewUser(userID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.user_url) + userID
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
                                getString(R.string.user_image_url) +
                                data.getString("image")

                        Picasso.get().load(imgUrl).fit().centerCrop().into(updateimage);
                        updatefirstname?.text = data.getString("firstname")
                        updatelastname?.text = data.getString("lastname")
                        updateemail?.text = data.getString("email")
                        updateusername?.text = data.getString("username")
                      //  updatepass?.text = data.getString("password")
                        updateaddress?.text = data.getString("address")
                        updatephone?.text = data.getString("phone")

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

    private fun updateUser(userID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.user_url) + userID
        val okHttpClient = OkHttpClient()
        var request: Request
        if(file==null){
            val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("firstName", updatefirstname?.text.toString())
                .addFormDataPart("lastName", updatelastname?.text.toString())
                .addFormDataPart("email", updateemail?.text.toString())
                .addFormDataPart("username", updateusername?.text.toString())
                //.addFormDataPart("password", updatepass?.text.toString())
                //.add("gender", if (radioWoman!!.isChecked) "1" else "0")
                .addFormDataPart("phone", updatephone?.text.toString())
                .addFormDataPart("address", updateaddress?.text.toString())
                .build()
                 request= Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()
        }
        else{
        val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("firstName", updatefirstname?.text.toString())
                .addFormDataPart("lastName", updatelastname?.text.toString())
                .addFormDataPart("email", updateemail?.text.toString())
                .addFormDataPart("username", updateusername?.text.toString())
               // .addFormDataPart("password", updatepass?.text.toString())
                //.add("gender", if (radioWoman!!.isChecked) "1" else "0")
                .addFormDataPart("phone", updatephone?.text.toString())
                .addFormDataPart("address", updateaddress?.text.toString())

                .addFormDataPart("file", ( file?.name),
                        RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file!!))
                .build()
                 request = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()
        }

        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        Toast.makeText(context, getString(R.string.edited), Toast.LENGTH_LONG).show()
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.replace(R.id.nav_host_fragment,UserFragment())
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
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && null != intent) {
            val uri = intent.data
            file = File(getFilePath(uri))
            val bitmap: Bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                //show image
                updateimage?.setImageBitmap(bitmap)
                updateimage?.setImageURI(uri)
                updateimage?.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.parse("file:$imageFilePath")
            file = File(imageUri.path)
            try {
                val ims: InputStream = FileInputStream(file)
                var imageBitmap = BitmapFactory.decodeStream(ims)


                //show image
                updateimage?.setImageBitmap(imageBitmap)
                updateimage?.visibility = View.VISIBLE
                getFileName(imageUri)

            } catch (e: FileNotFoundException) {
                return
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val storageDir = File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "")
        val image = File.createTempFile(
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()), ".png",
                storageDir)
        imageFilePath = image.absolutePath
        return image
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getFilePath(uri: Uri?): String? {

        var path = ""
        val wholeID = DocumentsContract.getDocumentId(uri)
        // Split at colon, use second item in the arraygetDocumentId(uri)
        val id = wholeID.split(":".toRegex()).toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = requireActivity().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)
        var columnIndex = 0
        if (cursor != null) {
            columnIndex = cursor.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                path = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return path
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireActivity().contentResolver.query(
                    uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }




}