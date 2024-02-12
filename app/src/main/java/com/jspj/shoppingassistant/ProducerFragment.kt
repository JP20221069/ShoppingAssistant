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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentProducerBinding
import com.jspj.shoppingassistant.databinding.FragmentProducersViewBinding
import com.jspj.shoppingassistant.databinding.FragmentProductsViewBinding
import com.jspj.shoppingassistant.model.ItemsViewModel
import com.jspj.shoppingassistant.model.Producer
import com.jspj.shoppingassistant.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProducerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProducerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentProducerBinding
    private lateinit var navController: NavController
    private lateinit var searchCriteria:String;
    private lateinit var ProductList:List<Product>
    private lateinit var Producer: Producer;
    private val args:ProducerFragmentArgs by navArgs();
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
        binding = FragmentProducerBinding.inflate(inflater, container, false)
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

        //UpdateUI();
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
            builder.setNeutralButton(R.string.btn_remove_filter,
                DialogInterface.OnClickListener{ dialog, which->FilterData(true)})
            builder.setNegativeButton(R.string.btn_cancel,
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
        }

        binding.ibMap.setOnClickListener {
            var long = Producer.Coords.split(";")[0];
            var lat = Producer.Coords.split(";")[1];
            var dir = ProducerFragmentDirections.actionProducerFragmentToMapFragment(long.toFloat(),lat.toFloat(),true);
            navController.navigate(dir);
        }

    }

    private fun UpdateData()
    {
        val ctrl: ShoppingAssistantController = ShoppingAssistantController(requireContext());
        var TH: ToastHandler = ToastHandler(requireContext())
        var recyclerView = binding.rwProducts;
        recyclerView.layoutManager = LinearLayoutManager(context)
        val producerid = args.PRODUCERID
        lifecycleScope.launch(Dispatchers.Main) {
            var products:List<Product> = listOf();
            Producer=ctrl.getProducerById(producerid)!!;
            binding.twAddress.text=Producer.Address;
            binding.twTitle.text=Producer.Name;
            products=ctrl.getProductsByProducer(producerid);
            ProductList=products;
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in products)
            {
                data.add(ItemsViewModel(R.drawable.product,p.Name, p.ID.toString()))
            }
            var adapter = CustomAdapter(data)
            adapter.setOnClickListener(object: CustomAdapter.OnClickListener{
                override fun onClick(position: Int, model: ItemsViewModel) {
                    var dir = ProducerFragmentDirections.actionProducerFragmentToProductFragment(model.payload);
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
        var adapter = CustomAdapter(data);
        adapter.setOnClickListener(object: CustomAdapter.OnClickListener{
            override fun onClick(position: Int, model: ItemsViewModel) {
                var dir = ProducerFragmentDirections.actionProducerFragmentToProductFragment(model.payload);
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
         * @return A new instance of fragment ProducerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProducerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}