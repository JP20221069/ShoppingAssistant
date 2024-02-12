package com.jspj.shoppingassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentProductBinding
import com.jspj.shoppingassistant.databinding.FragmentProductsViewBinding
import com.jspj.shoppingassistant.databinding.FragmentStoreBinding
import com.jspj.shoppingassistant.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [StoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StoreFragment : Fragment() {

    private lateinit var binding: FragmentStoreBinding
    private lateinit var navController: NavController
    private lateinit var Store: Store;
    private val args:StoreFragmentArgs by navArgs();
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
        binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        var ctrl:ShoppingAssistantController= ShoppingAssistantController(requireContext());
        lifecycleScope.launch(Dispatchers.Main)
        {
            Store=ctrl.getStoreById(args.STOREID)!!;
        }.invokeOnCompletion { UpdateData()  }

        binding.ibProducts.setOnClickListener{
            var dir = StoreFragmentDirections.actionStoreFragmentToProductsViewFragment2(args.STOREID);
            navController.navigate(dir);
        }

        binding.ibMap.setOnClickListener {
            var long = Store.Coords.split(";")[0];
            var lat = Store.Coords.split(";")[1];
            var dir = StoreFragmentDirections.actionStoreFragmentToMapFragment(long.toFloat(),lat.toFloat(),true);
            navController.navigate(dir);
        }

    }

    private fun UpdateData()
    {
        binding.twTitle.text=Store.Name;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StoreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StoreFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}