package com.jspj.shoppingassistant

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentProductBinding
import com.jspj.shoppingassistant.databinding.FragmentProductsViewBinding
import com.jspj.shoppingassistant.model.ItemsViewModel
import com.jspj.shoppingassistant.model.Product
import com.jspj.shoppingassistant.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [ProductsViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductsViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentProductsViewBinding
    private lateinit var navController: NavController
    private lateinit var searchCriteria:String;
    private lateinit var ProductList:List<Product>
    private val args:ProductsViewFragmentArgs by navArgs();
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
        binding = FragmentProductsViewBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init();
        navController= Navigation.findNavController(view)
        val recyclerview = binding.rwProducts;
        val TH : ToastHandler = ToastHandler(requireContext());
        recyclerview.layoutManager = LinearLayoutManager(context)
        var products:List<Product> = ArrayList();

        UpdateUI();
        UpdateData();

        binding.ibSearch.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(),R.style.Theme_ShoppingAssistant_Dialog))
            builder.setTitle(R.string.ttl_search).setIcon(R.drawable.magnifier)
            val input = EditText(requireContext());
            input.setTextColor(resources.getColor(R.color.sys_text));
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton(R.string.btn_ok,
                DialogInterface.OnClickListener { dialog, which ->searchCriteria = input.text.toString(); FilterData()})
            builder.setNeutralButton(R.string.btn_remove_filter,DialogInterface.OnClickListener{dialog,which->FilterData(true)})
            builder.setNegativeButton(R.string.btn_cancel,
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
        }

    }

    private fun UpdateData()
    {
        val ctrl: ShoppingAssistantController = ShoppingAssistantController();
        var TH: ToastHandler = ToastHandler(requireContext())
        var recyclerView = binding.rwProducts;
        recyclerView.layoutManager = LinearLayoutManager(context)
        val storeid = args.STOREID;
        lifecycleScope.launch(Dispatchers.Main) {
            var products:List<Product> = listOf();
            if(storeid=="-1")
            {
                products=ctrl.getProducts();
            }
            else
            {
                products=ctrl.getProductsByStore(storeid);
                binding.twStoreName.text=ctrl.getStoreById(args.STOREID)!!.Name;
            }
            ProductList=products;
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in products)
            {
                data.add(ItemsViewModel(R.drawable.product,p.Name, p.ID.toString()))
            }
            var adapter = CustomAdapter(data)
            adapter.setOnClickListener(object: CustomAdapter.OnClickListener{
                override fun onClick(position: Int, model: ItemsViewModel) {
                    var dir = ProductsViewFragmentDirections.actionProductsViewFragment2ToProductFragment(model.payload);
                    navController.navigate(dir);
                }
            })

            recyclerView.adapter=adapter;
        }
    }

    private fun FilterData(removeFilter:Boolean=false)
    {
        var recyclerView = binding.rwProducts;
        var data:ArrayList<ItemsViewModel> = arrayListOf();
        for(p in ProductList)
        {
            if(p.Name.contains(searchCriteria) || removeFilter==true)
            {
                data.add(ItemsViewModel(R.drawable.product,p.Name,p.ID.toString()))
            }
        }
        SetAdapter(data);
    }

    private fun UpdateUI()
    {
        if(args.STOREID=="-1")
        {
            binding.twStoreName.visibility=View.INVISIBLE;
        }
        else
        {
            binding.twStoreName.visibility=View.VISIBLE;
        }
    }

    private fun SetAdapter(data: ArrayList<ItemsViewModel>)
    {
        var recyclerView = binding.rwProducts;
        var adapter = CustomAdapter(data)
        adapter.setOnLongClickListener(object : CustomAdapter.OnLongClickListener {
            override fun onLongClick(position: Int, model: ItemsViewModel): Boolean {
                return true;
            }
        })
        adapter.setOnClickListener(object : CustomAdapter.OnClickListener {
            override fun onClick(position: Int, model: ItemsViewModel) {
                var dir = ProductsViewFragmentDirections.actionProductsViewFragment2ToProductFragment(model.payload);
                navController.navigate(dir);
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
         * @return A new instance of fragment ProductsViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsViewFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}