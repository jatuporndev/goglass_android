package com.example.goglasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import java.util.*

class SettingFragment : Fragment() {

    var radiothai: RadioButton? = null
    var radioeng: RadioButton? = null
    var radiojp: RadioButton? = null
    var btnla :Button?=null
    var localMain:String?="th"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val root =inflater.inflate(R.layout.fragment_setting, container, false)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            (activity as? MainActivity)?.changeToolbarhome()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, UserFragment())
            fragmentTransaction.commit()
            (activity as? MainActivity)?.changeToolbarhome()
        }
        (activity as? MainActivity)?.changeToolbarsetting()
       // (activity as? MainAdminActivity)?.changeToolbarhelp()
        callback.isEnabled
        val sharedPrefer = requireContext().getSharedPreferences(
            LoginActivity().appPreference, Context.MODE_PRIVATE)
        localMain = sharedPrefer?.getString(MainActivity().localMain, null)
      //  Log.d("txtlo",localMain.toString())
        btnla = root.findViewById(R.id.btnla)
        radioeng = root.findViewById(R.id.radioButton2)
        radiothai = root.findViewById(R.id.radioButton)
        radiojp = root.findViewById(R.id.radioButton3)

        if (localMain=="th") {
            radiothai?.isChecked = true
        }
        else if(localMain=="values")
        {
            radioeng?.isChecked = true
        }else if(localMain=="ja")
        {
            radiojp?.isChecked = true
        }else{
            radiothai?.isChecked = true
        }


        btnla?.setOnClickListener {

                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setMessage(getString(R.string.restart))
                    .setCancelable(false)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                        var x: String
                        if (radioeng!!.isChecked) {
                            x = "values"
                        }else if (radiojp!!.isChecked) {
                             x = "ja"
                        } else {
                            x = "th"
                        }
                        (activity as? MainActivity)?.setl(x)
                    })
                    .setNegativeButton(
                        "No",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                val alert: AlertDialog = builder.create()
                alert.show()

        }

        return root
    }


}