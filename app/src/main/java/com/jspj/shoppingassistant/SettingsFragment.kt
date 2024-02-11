package com.jspj.shoppingassistant

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.databinding.FragmentMainMenuBinding
import com.jspj.shoppingassistant.databinding.FragmentSettingsBinding
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sharedPref = context?.getSharedPreferences("settings",Context.MODE_PRIVATE)?.edit()
        var TH:ToastHandler = ToastHandler(requireContext());
        binding.ibENG.setOnClickListener {
            if (sharedPref != null) {
                sharedPref.putString("LANG","en")
                sharedPref.apply()
                TH.showToast(R.string.tst_restart, Toast.LENGTH_SHORT)
            };
        }
        binding.ibSC.setOnClickListener {
            if (sharedPref != null) {
                sharedPref.putString("LANG","sh")
                TH.showToast(R.string.tst_restart, Toast.LENGTH_SHORT)
                sharedPref.apply()
            };
        }
        binding.ibGER.setOnClickListener {
            if (sharedPref != null) {
                sharedPref.putString("LANG","de")
                TH.showToast(R.string.tst_restart, Toast.LENGTH_SHORT)
                sharedPref.apply()
            };
        }
        binding.ibSLO.setOnClickListener {
            if (sharedPref != null) {
                sharedPref.putString("LANG","sl")
                TH.showToast(R.string.tst_restart, Toast.LENGTH_SHORT)
                sharedPref.apply()
            };
        }

        binding.ibSR.setOnClickListener {
            if (sharedPref != null) {
                sharedPref.putString("LANG","sr")
                TH.showToast(R.string.tst_restart, Toast.LENGTH_SHORT)
                sharedPref.apply()
            };
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}