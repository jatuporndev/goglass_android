package com.example.goglasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeFragment : Fragment() {
    var fade_in: Animation? = null
    var fade_out:Animation? = null
    var viewFlipper: ViewFlipper? = null
    var btnshop: Button?=null
    var test: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val root = inflater.inflate(R.layout.fragment_home, container, false)

        btnshop = root.findViewById(R.id.button)
        viewFlipper = root.findViewById(R.id.bckgrndViewFlipper1) as ViewFlipper

        fade_in = AnimationUtils.loadAnimation(requireContext(),
                android.R.anim.slide_in_left);
        fade_out = AnimationUtils.loadAnimation(requireContext(),
                android.R.anim.slide_out_right);

        viewFlipper!!.inAnimation = fade_in;
        viewFlipper!!.outAnimation = fade_out;

        viewFlipper!!.isAutoStart = true;
        viewFlipper!!.flipInterval = 2500;
        viewFlipper!!.startFlipping();

        btnshop!!.setOnClickListener {

            val mBottomNavigationView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            mBottomNavigationView.selectedItemId = R.id.nav_shop

        }


        return root



    }




}