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
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentMainMenuBinding
import com.jspj.shoppingassistant.databinding.FragmentStoresViewBinding
import com.jspj.shoppingassistant.model.ItemsViewModel
import com.jspj.shoppingassistant.model.ShoppingList
import com.jspj.shoppingassistant.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StoresViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StoresViewFragment : Fragment() {
    private lateinit var binding: FragmentStoresViewBinding
    private lateinit var navController: NavController
    private var searchCriteria="";
    private lateinit var Stores:List<Store>;
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
        binding = FragmentStoresViewBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        val ctrl:ShoppingAssistantController = ShoppingAssistantController(requireContext());
        UpdateData();
        UpdateUI();

        binding.ibSearch.setOnClickListener {
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
    }


    private fun UpdateData()
    {
        val ctrl: ShoppingAssistantController = ShoppingAssistantController(requireContext());
        var TH: ToastHandler = ToastHandler(requireContext())
        var recyclerView = binding.rwStores;
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch(Dispatchers.Main) {
            var sts = ctrl.getStores();
            Stores=sts;
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in sts)
            {
                data.add(ItemsViewModel(R.drawable.store,p.Name,p.ID.toString()))
            }
            SetAdapter(data);
        }
    }

    private fun FilterData(removeFilter:Boolean=false)
    {
        var data: ArrayList<ItemsViewModel> = arrayListOf();
        for (p in Stores) {
            if (p.Name.contains(searchCriteria) || removeFilter == true || searchCriteria=="") {
                data.add(ItemsViewModel(R.drawable.store, p.Name, p.ID.toString()))
            }
        }
        SetAdapter(data);
        if(removeFilter)
        {
            searchCriteria="";
        }
    }

    private fun UpdateUI()
    {
    }


    private fun SetAdapter(data:ArrayList<ItemsViewModel>)
    {
        var recyclerView = binding.rwStores;
        var adapter = CustomAdapter(data)
        adapter.setOnClickListener(object: CustomAdapter.OnClickListener{
            override fun onClick(position: Int, model: ItemsViewModel) {
                var dir = StoresViewFragmentDirections.actionStoresViewFragmentToStoreFragment(model.payload);
                navController.navigate(dir);
            }
        })
        recyclerView.adapter=adapter;
    }

    private fun updateCardBackgroundColor(position: Int,color:Int) {
        // Update the background color of the clicked card
        // You can access the card at the specified position and modify its background color
        var recyclerView = binding.rwStores;
        val cardView = recyclerView.layoutManager?.findViewByPosition(position)?.findViewById<CardView>(R.id.cardview)
        cardView?.setCardBackgroundColor(resources.getColor(color))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StoresViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StoresViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}