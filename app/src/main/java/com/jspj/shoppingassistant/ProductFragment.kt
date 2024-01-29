package com.jspj.shoppingassistant

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentProductBinding
import com.jspj.shoppingassistant.model.ItemsViewModel
import com.jspj.shoppingassistant.model.Product
import com.jspj.shoppingassistant.model.ShoppingList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
    private lateinit var searchCriteria:String;
    private lateinit var ProductList:List<Product>

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
        UpdateData()

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

        binding.btFind.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(),R.style.Theme_ShoppingAssistant_Dialog))
            builder.setTitle("Search").setIcon(R.drawable.search)
            val input = EditText(requireContext());
            input.setTextColor(resources.getColor(R.color.sys_text));
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which ->searchCriteria = input.text.toString(); FilterData()})
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
        }

        binding.btRemoveFilter.setOnClickListener{
            FilterData(true);
        }
    }

    private fun UpdateData()
    {
        val ctrl:ShoppingAssistantController = ShoppingAssistantController();
        var TH:ToastHandler = ToastHandler(requireContext())
        var recyclerView = binding.recyclerview;
        recyclerView.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.Main) {
            var products:List<Product> = ctrl.getProducts();
            ProductList=products;
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in products)
            {
                data.add(ItemsViewModel(R.drawable.product,p.Name, p.ID.toString()))
            }
            var adapter = CustomAdapter(data)
            adapter.setOnClickListener(object:CustomAdapter.OnClickListener{
                override fun onClick(position: Int, model: ItemsViewModel) {

                }
            })

            recyclerView.adapter=adapter;
        }
    }

    private fun FilterData(removeFilter:Boolean=false)
    {
        var recyclerView = binding.recyclerview;
        var data:ArrayList<ItemsViewModel> = arrayListOf();
        for(p in ProductList)
        {
            if(p.Name.contains(searchCriteria) || removeFilter==true)
            {
                data.add(ItemsViewModel(R.drawable.product,p.Name,p.ID.toString()))
            }
        }
        var adapter = CustomAdapter(data);
        adapter.setOnClickListener(object:CustomAdapter.OnClickListener{
            override fun onClick(position: Int, model: ItemsViewModel) {

            }
        })

        recyclerView.adapter=adapter;
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