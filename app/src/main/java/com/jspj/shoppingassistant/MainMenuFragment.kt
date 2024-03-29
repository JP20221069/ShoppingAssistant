package com.jspj.shoppingassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jspj.shoppingassistant.Utils.LocaleManager
import com.jspj.shoppingassistant.databinding.FragmentMainMenuBinding
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentMainMenuBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController= Navigation.findNavController(view)
        binding.ibProducts.setOnClickListener{
            navController.navigate(R.id.action_mainMenuFragment_to_productsViewFragment2);
        }

        binding.ibLists.setOnClickListener{
            navController.navigate(R.id.action_mainMenuFragment_to_listsViewFragment )
        }

        binding.ibStores.setOnClickListener{
            navController.navigate(R.id.action_mainMenuFragment_to_storesViewFragment)
        }

        binding.ibMap.setOnClickListener{
            navController.navigate(R.id.action_mainMenuFragment_to_mapFragment)
        }

        binding.ibScanner.setOnClickListener {
            navController.navigate(R.id.action_mainMenuFragment_to_scannerFragment)
        }

        binding.ibProducers.setOnClickListener {
            navController.navigate(R.id.action_mainMenuFragment_to_producersViewFragment)
        }

        binding.ibSettings.setOnClickListener {
            navController.navigate(R.id.action_mainMenuFragment_to_settingsFragment)
        }

        binding.ibQuit.setOnClickListener {
            activity?.finishAndRemoveTask();
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}