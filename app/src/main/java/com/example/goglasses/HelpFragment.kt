package com.example.goglasses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback


class HelpFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val root= inflater.inflate(R.layout.fragment_help, container, false)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            (activity as? MainActivity)?.changeToolbarhome()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, UserFragment())
            fragmentTransaction.commit()
            (activity as? MainActivity)?.changeToolbarhome()
        }
        (activity as? MainActivity)?.changeToolbarhelp()
        (activity as? MainAdminActivity)?.changeToolbarhelp()
        callback.isEnabled

        return root
    }



}