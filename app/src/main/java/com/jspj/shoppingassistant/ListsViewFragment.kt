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
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentListsViewBinding
import com.jspj.shoppingassistant.model.ItemsViewModel
import com.jspj.shoppingassistant.model.ShoppingList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListsViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListsViewFragment : Fragment() {
    private lateinit var binding: FragmentListsViewBinding
    private lateinit var navController: NavController
    private lateinit var Lists:MutableList<ShoppingList>
    private var selectmode : Boolean=false;
    private var SelectedItems:MutableList<Int> = mutableListOf();
    private var searchCriteria="";

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
        binding = FragmentListsViewBinding.inflate(inflater, container, false)
        return binding.root;
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        val TH : ToastHandler = ToastHandler(requireContext());
        val ctrl:ShoppingAssistantController = ShoppingAssistantController(requireContext());
        UpdateData();
        UpdateUI();
        binding.ibAddList.setOnClickListener{
            navController.navigate(R.id.action_listsViewFragment_to_addListFragment)
        }

        binding.ibRemoveList.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {

                                lifecycleScope.launch(Dispatchers.Main)
                                {
                                    for(i in SelectedItems)
                                    {
                                        ctrl.deleteList(Lists[i]);
                                    }
                                }.invokeOnCompletion {
                                    TH.showToast(getString(R.string.tst_deletedlists), Toast.LENGTH_SHORT);
                                    UpdateData()
                                    selectmode=false;
                                    UpdateUI()
                                    FilterData()
                                }


                        }
                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder( ContextThemeWrapper(context,R.style.Theme_ShoppingAssistant_Dialog))
            builder.setMessage(R.string.pup_areyousure).setPositiveButton(R.string.btn_yes, dialogClickListener)
                .setNegativeButton(R.string.btn_no, dialogClickListener).setTitle(R.string.ttl_question).setIcon(R.drawable.question).show()
        }


        binding.ibSearch.setOnClickListener {
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
        val ctrl:ShoppingAssistantController = ShoppingAssistantController(requireContext());
        var TH:ToastHandler = ToastHandler(requireContext())
        var recyclerView = binding.rwLists;
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch(Dispatchers.Main) {
            var lists = ctrl.getListsByUser(ctrl.getUID()!!);
            Lists=lists;
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in lists)
            {
                data.add(ItemsViewModel(R.drawable.onelist,p.Name,p.ID.toString()))
            }
            SetAdapter(data);
        }
    }

    private fun FilterData(removeFilter:Boolean=false)
    {
            var data: ArrayList<ItemsViewModel> = arrayListOf();
            for (p in Lists) {
                if (p.Name.contains(searchCriteria) || removeFilter == true || searchCriteria=="") {
                    data.add(ItemsViewModel(R.drawable.onelist, p.Name, p.ID.toString()))
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
        if(selectmode==true)
        {
            binding.ibRemoveList.visibility=View.VISIBLE;
            binding.ibAddList.visibility=View.INVISIBLE;
            binding.ibSearch.visibility=View.INVISIBLE;
        }
        else
        {

            binding.ibAddList.visibility=View.VISIBLE;
            binding.ibRemoveList.visibility=View.INVISIBLE;
            binding.ibSearch.visibility=View.VISIBLE;
        }
    }


    private fun SetAdapter(data:ArrayList<ItemsViewModel>)
    {
        var recyclerView = binding.rwLists;
        var adapter = CustomAdapter(data)
        adapter.setOnLongClickListener(object:CustomAdapter.OnLongClickListener{
            override fun onLongClick(position: Int, model: ItemsViewModel) : Boolean
            {

                selectmode=true;
                for(item in SelectedItems)
                {
                    updateCardBackgroundColor(item, R.color.sys_background)
                }
                SelectedItems.clear()
                UpdateUI();
                if(SelectedItems.contains(position)==false)
                {
                    updateCardBackgroundColor(position,R.color.sys_selection)
                    SelectedItems.add(position);
                }
                return true;
            }
        })
        adapter.setOnClickListener(object:CustomAdapter.OnClickListener{
            override fun onClick(position: Int, model: ItemsViewModel) {
                if(selectmode)
                {
                    if(SelectedItems.contains(position)) {
                        updateCardBackgroundColor(position,R.color.sys_background);
                        SelectedItems.remove(position);
                        if(SelectedItems.size==0)
                        {
                            selectmode=false;
                            UpdateUI();
                        }
                    }
                    else
                    {
                        updateCardBackgroundColor(position,R.color.sys_selection)
                        SelectedItems.add(position);
                    }
                }
                else
                {
                    val directions = ListsViewFragmentDirections.actionListsViewFragmentToShoppingListFragment(model.payload)
                    navController.navigate(directions);
                }
            }
        })
        recyclerView.adapter=adapter;
    }

    private fun updateCardBackgroundColor(position: Int,color:Int) {
        // Update the background color of the clicked card
        // You can access the card at the specified position and modify its background color
        var recyclerView = binding.rwLists;
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
         * @return A new instance of fragment ListsViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListsViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}