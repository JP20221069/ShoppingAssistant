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
import androidx.cardview.widget.CardView
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColor
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentListsViewBinding
import com.jspj.shoppingassistant.databinding.FragmentShoppingListBinding
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
 * Use the [ShoppingListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShoppingListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var navController: NavController;
    private lateinit var binding: FragmentShoppingListBinding;
    private lateinit var List: ShoppingList;
    private lateinit var SelectedItems: MutableList<Int>;
    private var selectmode: Boolean = false;
    private val args: ShoppingListFragmentArgs by navArgs()
    private var searchCriteria:String="";
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
        binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)
        UpdateUI();
        SelectedItems = mutableListOf();
        super.onViewCreated(view, savedInstanceState)
        val ctrl: ShoppingAssistantController = ShoppingAssistantController();
        var TH: ToastHandler = ToastHandler(requireContext())
        UpdateData()
        binding.ibAddProduct.setOnClickListener {

        }

        binding.ibIncProduct.setOnClickListener {
            for (i in SelectedItems) {
                List.Products?.get(i)?.Amount = List.Products!![i]?.Amount?.plus(1)!!;
            }
            var ctrl: ShoppingAssistantController = ShoppingAssistantController();
            lifecycleScope.launch(Dispatchers.Main)
            {
                ctrl.updateList(List);
            }.invokeOnCompletion {
                TH.showToast(R.string.tst_updated_list, Toast.LENGTH_SHORT)
                UpdateData()
                selectmode = false;
                UpdateUI()
            }
        }

        binding.ibDecProduct.setOnClickListener {
            for (i in SelectedItems) {
                if(List?.Products?.get(i)?.Amount!! >1)
                {
                    List!!.Products?.get(i)?.Amount = List!!.Products!![i]?.Amount?.minus(1)!!;
                }
                else
                {
                    TH.showToast(R.string.tst_less_than_one, Toast.LENGTH_SHORT);
                }
            }
            lifecycleScope.launch(Dispatchers.Main)
            {
                ctrl.updateList(List);
            }.invokeOnCompletion {
                TH.showToast(R.string.tst_updated_list, Toast.LENGTH_SHORT);
                UpdateData()
                selectmode = false;
                UpdateUI()
            }
        }
        binding.ibAddProduct.setOnClickListener {
            val directions =
                ShoppingListFragmentDirections.actionShoppingListFragmentToFragmentAddProduct(List.ID.toString())
            navController.navigate(directions)

        }

        binding.ibChkProduct.setOnClickListener {
            for (i in SelectedItems) {
                List.Products?.get(i)?.Checked = true;
            }
            lifecycleScope.launch(Dispatchers.Main)
            {
                ctrl.updateList(List);
            }.invokeOnCompletion {
                TH.showToast(R.string.tst_updated_list, Toast.LENGTH_SHORT);
                UpdateData()
                selectmode = false;
                UpdateUI()
            }
        }
        binding.ibUnchkProduct.setOnClickListener {
            for (i in SelectedItems) {
                List.Products?.get(i)?.Checked = false;
            }
            lifecycleScope.launch(Dispatchers.Main)
            {
                ctrl.updateList(List);
            }.invokeOnCompletion {
                TH.showToast(R.string.tst_updated_list, Toast.LENGTH_SHORT);
                UpdateData()
                selectmode = false;
                UpdateUI()
            }
        }

        binding.ibRemoveProduct.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {

                            lifecycleScope.launch(Dispatchers.Main)
                            {
                                for(i in SelectedItems)
                                {
                                    List.Products?.removeAt(i);
                                }
                                ctrl.updateList(List);
                            }.invokeOnCompletion {
                                TH.showToast(R.string.tst_removed_list_item, Toast.LENGTH_SHORT);
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

    private fun UpdateData() {
        val ctrl: ShoppingAssistantController = ShoppingAssistantController();
        var TH: ToastHandler = ToastHandler(requireContext())
        var recyclerView = binding.rwShoppingList;
        recyclerView.layoutManager = LinearLayoutManager(context)
        var list: ShoppingList = ShoppingList();
        lifecycleScope.launch(Dispatchers.Main) {
            var listid = args.LISTID;
            list = ctrl.getList(ctrl.getUID()!!, listid)!!;
            List = list;
            binding.twTitle.setText(List.Name);
            var data: ArrayList<ItemsViewModel> = arrayListOf();
            var totalprice:Float=0.0f;
            for (p in list.Products!!) {

                if(p.Checked)
                {
                    totalprice += ctrl.getPriceForProduct(
                        p.Product?.ID.toString(),
                        list.Store?.ID.toString()
                    )?.Price?.times(p.Amount)!!
                }

                data.add(
                    ItemsViewModel(
                        R.drawable.product,

                        p.Product?.Name + "("+p.Amount+")\t"+ctrl.getPriceForProduct(p.Product?.ID.toString(),list.Store?.ID.toString())?.Price.toString(),
                        p.Product!!.ID.toString(),
                        p.Checked
                    )
                )
            }
            SetAdapter(data);
            binding.twPrice.text=totalprice.toString();
        }
    }

    private fun FilterData(removeFilter:Boolean=false)
    {
        lifecycleScope.launch(Dispatchers.Main) {
            var ctrl: ShoppingAssistantController = ShoppingAssistantController();
            var data: ArrayList<ItemsViewModel> = arrayListOf();
            for (p in List.Products!!) {
                if (p.Product?.Name?.contains(searchCriteria)!! || removeFilter == true || searchCriteria == "") {
                    data.add(
                        ItemsViewModel(
                            R.drawable.product,
                            p.Product?.Name + " (" + p.Amount + ")\t" + ctrl.getPriceForProduct(
                                p.Product?.ID.toString(),
                                List.Store?.ID.toString()
                            )?.Price.toString(),
                            p.Product!!.ID.toString(),
                            p.Checked
                        )
                    )
                }
            }
            SetAdapter(data);
            if (removeFilter) {
                searchCriteria = "";
            }
        }
    }

    private fun UpdateUI() {

        if (selectmode == true) {
            binding.ibDecProduct.visibility = View.VISIBLE;
            binding.ibIncProduct.visibility = View.VISIBLE;
            binding.ibRemoveProduct.visibility = View.VISIBLE;
            binding.ibChkProduct.visibility = View.VISIBLE;
            binding.ibUnchkProduct.visibility = View.VISIBLE;

            binding.ibAddProduct.visibility = View.INVISIBLE;

            binding.ibSearch.visibility=View.INVISIBLE;

            binding.iwCart.visibility=View.INVISIBLE;
            binding.twPrice.visibility=View.INVISIBLE;
        } else {

            binding.ibDecProduct.visibility = View.INVISIBLE;
            binding.ibIncProduct.visibility = View.INVISIBLE;
            binding.ibRemoveProduct.visibility = View.INVISIBLE;
            binding.ibChkProduct.visibility = View.INVISIBLE;
            binding.ibUnchkProduct.visibility = View.INVISIBLE;

            binding.ibAddProduct.visibility = View.VISIBLE;
            binding.ibSearch.visibility = View.VISIBLE;

            binding.iwCart.visibility=View.VISIBLE;
            binding.twPrice.visibility=View.VISIBLE;
        }
    }

    private fun SetAdapter(data: ArrayList<ItemsViewModel>)
    {
        var recyclerView = binding.rwShoppingList;
        var adapter = CustomAdapter(data)
        adapter.setOnLongClickListener(object : CustomAdapter.OnLongClickListener {
            override fun onLongClick(position: Int, model: ItemsViewModel): Boolean {

                selectmode = true;
                for (item in SelectedItems) {
                    updateCardBackgroundColor(item, R.color.sys_background)
                }
                SelectedItems.clear()
                UpdateUI();
                if (SelectedItems.contains(position) == false) {
                    updateCardBackgroundColor(position, R.color.sys_selection)
                    SelectedItems.add(position);
                }
                return true;
            }
        })
        adapter.setOnClickListener(object : CustomAdapter.OnClickListener {
            override fun onClick(position: Int, model: ItemsViewModel) {
                if (selectmode) {
                    if (SelectedItems.contains(position)) {
                        updateCardBackgroundColor(position, R.color.sys_background);
                        SelectedItems.remove(position);
                        if (SelectedItems.size == 0) {
                            selectmode = false;
                            UpdateUI();
                        }
                    } else {
                        updateCardBackgroundColor(position, R.color.sys_selection)
                        SelectedItems.add(position);
                    }
                }
                else
                {
                    var dir = ShoppingListFragmentDirections.actionShoppingListFragmentToListItemViewFragment(List.ID.toString(),position);
                    navController.navigate(dir);
                }
            }
        })
        recyclerView.adapter=adapter;
    }

    private fun updateCardBackgroundColor(position: Int,color:Int) {
        // Update the background color of the clicked card
        // You can access the card at the specified position and modify its background color
        var recyclerView = binding.rwShoppingList;
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
         * @return A new instance of fragment ShoppingListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShoppingListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}