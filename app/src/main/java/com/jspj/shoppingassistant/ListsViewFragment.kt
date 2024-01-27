package com.jspj.shoppingassistant

import android.os.Bundle
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
import com.jspj.shoppingassistant.databinding.FragmentListsViewBinding
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
 * Use the [ListsViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListsViewFragment : Fragment() {
    private lateinit var binding: FragmentListsViewBinding
    private lateinit var navController: NavController
    private lateinit var Lists:MutableList<ShoppingList>

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
        val recyclerview = binding.rwLists;
        val TH : ToastHandler = ToastHandler(requireContext());
        recyclerview.layoutManager = LinearLayoutManager(context)
        var lists:List<ShoppingList> = ArrayList();
        val ctrl: ShoppingAssistantController = ShoppingAssistantController()
        lifecycleScope.launch(Dispatchers.Main) {
            navController= Navigation.findNavController(view)
            lists = ctrl.getListsByUser(ctrl.getUID()!!)
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in lists)
            {
                data.add(ItemsViewModel(R.drawable.onelist,p.Name,p.ID.toString()))
            }
            val adapter = CustomAdapter(data)
            adapter.setOnClickListener(object:CustomAdapter.OnClickListener{
                override fun onClick(position: Int, model: ItemsViewModel)
                {
                    val list_fragment = ShoppingListFragment();
                    val directions = ListsViewFragmentDirections.actionListsViewFragmentToShoppingListFragment(model.payload)
                    navController.navigate(directions);
                }
            })
            recyclerview.adapter = adapter

        }



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