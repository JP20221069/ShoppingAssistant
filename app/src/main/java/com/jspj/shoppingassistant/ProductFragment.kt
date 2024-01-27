package com.jspj.shoppingassistant

import com.jspj.shoppingassistant.model.ItemsViewModel
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentProductBinding
import com.jspj.shoppingassistant.model.Price
import com.jspj.shoppingassistant.model.Product
import com.jspj.shoppingassistant.model.ShoppingList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
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
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init();
        val recyclerview = binding.recyclerview;
        val TH : ToastHandler = ToastHandler(requireContext());
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)
        var products:List<Product> = ArrayList();
        val ctrl: ShoppingAssistantController = ShoppingAssistantController()
        lifecycleScope.launch(Dispatchers.Main) {
            products = ctrl.getProducts()
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in products)
            {
                data.add(ItemsViewModel(R.drawable.product,p.ID.toString()+" "+p.Name,p.ID.toString()))
            }
            // This will pass the ArrayList to our Adapter
            val adapter = CustomAdapter(data)
            recyclerview.adapter = adapter
            navController= Navigation.findNavController(view)
        }


        binding.bttest.setOnClickListener{
            val ctrl:ShoppingAssistantController = ShoppingAssistantController();
            //var test: MutableList<ShoppingList> = mutableListOf(ShoppingList());
            var test:String="FAILED";
            lifecycleScope.launch(Dispatchers.IO) {

                //test= ctrl.getListsByUser(ctrl.getUID()!!);
                test=ctrl.getLastListIDByUser(ctrl.getUID()!!);
            }.invokeOnCompletion {
                TH.showToast(test, Toast.LENGTH_SHORT);
            }
            //TH.showToast(ctrl.getUID(),Toast.LENGTH_SHORT);


        }
        /*binding.btntestprod.setOnClickListener{
            val ctrl:ShoppingAssistantController = ShoppingAssistantController()
            var test = Product(0,"Test","N/A.");
            ctrl.insertProduct(test);
        }*/
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}