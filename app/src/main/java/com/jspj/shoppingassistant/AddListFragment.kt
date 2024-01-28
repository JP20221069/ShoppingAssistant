package com.jspj.shoppingassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jspj.shoppingassistant.Utils.MySpinner
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentAddListBinding
import com.jspj.shoppingassistant.databinding.FragmentListsViewBinding
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
 * Use the [AddListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddListFragment : Fragment() {

    private lateinit var binding:FragmentAddListBinding;
    private lateinit var navController: NavController;
    private lateinit var StoreList:MutableList<Store>
    private lateinit var StoreNames:MutableList<String>;
    private var CurrentStore:Store?=null;
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
        binding = FragmentAddListBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var TH:ToastHandler=ToastHandler(requireContext());
        var ctrl:ShoppingAssistantController = ShoppingAssistantController();
        navController= Navigation.findNavController(view)
        StoreList= mutableListOf()
        StoreNames = mutableListOf()
        lifecycleScope.launch(Dispatchers.Main){
            StoreList.add(Store(-1,"Select a store:"));
            StoreList.addAll(ctrl.getStores());
            for(s in StoreList)
            {
                StoreNames.add(s.Name);
            }
            CurrentStore=StoreList[0];
            var storeSpinner: Spinner = binding.cbStore;
            var adapter =  ArrayAdapter<Store>(requireContext(),android.R.layout.simple_spinner_dropdown_item,StoreList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            storeSpinner.setAdapter(adapter);
            storeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    var TH:ToastHandler = ToastHandler(requireContext())
                    CurrentStore = StoreList[position]
                    //TH.showToast("Selected "+position.toString(),Toast.LENGTH_SHORT)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    var TH:ToastHandler = ToastHandler(requireContext())
                    TH.showToast("Nothing Selected",Toast.LENGTH_SHORT)
                }


            }
        }




        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)



        binding.btConfirm.setOnClickListener{
                if(Validate())
                {
                    var sl = ShoppingList(-1, mutableListOf(),binding.txtListName.text.toString(),CurrentStore);
                    lifecycleScope.launch(Dispatchers.Main)
                    {
                        ctrl.insertList(sl);
                    }.invokeOnCompletion {
                        TH.showToast("Successfully added list",Toast.LENGTH_SHORT)
                        navController.navigate(R.id.action_addListFragment_to_listsViewFragment)
                    }
                }

        }
    }

    fun Validate():Boolean
    {
        var TH:ToastHandler=ToastHandler(requireContext());
        if(binding.txtListName.text.isNullOrBlank())
        {
            TH.showToast("List name is required.", Toast.LENGTH_SHORT)
            return false;
        }
        if(CurrentStore?.ID==-1)
        {
            TH.showToast("A store is required.", Toast.LENGTH_SHORT)
            return false;
        }
        return true;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}