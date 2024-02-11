package com.jspj.shoppingassistant

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentProductBinding
import com.jspj.shoppingassistant.model.ItemsViewModel
import com.jspj.shoppingassistant.model.Product
import com.jspj.shoppingassistant.model.Store
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
    private lateinit var StoreList:List<Store>
    private lateinit var Product: Product;
    private val args:ProductFragmentArgs by navArgs();

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
        navController= Navigation.findNavController(view)
        val TH : ToastHandler = ToastHandler(requireContext());
        UpdateData();

        binding.ibSearch.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(),R.style.Theme_ShoppingAssistant_Dialog))
            builder.setTitle(R.string.ttl_search).setIcon(R.drawable.search)
            val input = EditText(requireContext());
            input.setTextColor(resources.getColor(R.color.sys_text));
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton(R.string.btn_ok,
                DialogInterface.OnClickListener { dialog, which ->searchCriteria = input.text.toString(); FilterData()})
            builder.setNegativeButton(R.string.btn_cancel,
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
        }

        binding.ibDetails.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(),R.style.Theme_ShoppingAssistant_Dialog))
            builder.setTitle(R.string.ttl_information).setIcon(R.drawable.information)
            builder.setMessage(getString(R.string.lbl_description)+" "+Product.Description+"\n"+getString(R.string.lbl_manufacturer)+" "+Product.Producer!!.Name + " , "+ Product.Producer!!.Address +"\n"+getString(R.string.lbl_barcode)+" "+Product.Barcode);
            builder.setPositiveButton(R.string.btn_ok,
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel()})
            builder.show()
        }

    }

    private fun UpdateData()
    {
        val ctrl:ShoppingAssistantController = ShoppingAssistantController();
        var TH:ToastHandler = ToastHandler(requireContext())
        var recyclerView = binding.recyclerview;
        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.Main) {
            var product = ctrl.getProductById(args.PRODUCTID);
            Product=product!!;
            binding.twTitle.text=product?.Name;
            binding.twManufacturer.text=product?.Producer?.Name;
            var stores:List<Store> = ctrl.getStoresByProduct(args.PRODUCTID);
            StoreList=stores;
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in stores)
            {
                data.add(ItemsViewModel(R.drawable.store,p.Name + "\t("+ ctrl.getPriceForProduct(args.PRODUCTID,p.ID.toString())!!.Price.toString()+")", p.ID.toString()))
            }
            var adapter = CustomAdapter(data)
            adapter.setOnClickListener(object:CustomAdapter.OnClickListener{
                override fun onClick(position: Int, model: ItemsViewModel) {
                    var dir = ProductFragmentDirections.actionProductFragmentToStoreFragment(model.payload);
                    navController.navigate(dir);
                }
            })

            recyclerView.adapter=adapter;
        }
    }

    private fun FilterData(removeFilter:Boolean=false)
    {
        var recyclerView = binding.recyclerview;
        var data:ArrayList<ItemsViewModel> = arrayListOf();
        val ctrl:ShoppingAssistantController = ShoppingAssistantController();
        lifecycleScope.launch(Dispatchers.Main)
        {
        for(p in StoreList)
        {
            if(p.Name.contains(searchCriteria) || removeFilter==true)
            {
                data.add(ItemsViewModel(R.drawable.store,p.Name + "\t("+ ctrl.getPriceForProduct(args.PRODUCTID,p.ID.toString())!!.Price.toString()+")", p.ID.toString()))
            }
        }
        }
        var adapter = CustomAdapter(data);
        adapter.setOnClickListener(object:CustomAdapter.OnClickListener{
            override fun onClick(position: Int, model: ItemsViewModel) {
                var dir = ProductFragmentDirections.actionProductFragmentToStoreFragment(model.payload);
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