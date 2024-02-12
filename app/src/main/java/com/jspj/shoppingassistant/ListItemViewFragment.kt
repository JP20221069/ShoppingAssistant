package com.jspj.shoppingassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentListItemViewBinding
import com.jspj.shoppingassistant.databinding.FragmentListsViewBinding
import com.jspj.shoppingassistant.databinding.FragmentShoppingListBinding
import com.jspj.shoppingassistant.model.ShoppingList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListItemViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListItemViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var navController: NavController;
    private lateinit var binding: FragmentListItemViewBinding;
    private val args: ListItemViewFragmentArgs by navArgs()
    private var List:ShoppingList?=null;
    private var ItemPrice:Float=0.00f;
    private var hasbeenchanged:Boolean=false;
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
        binding = FragmentListItemViewBinding.inflate(inflater, container, false)
        return binding.root;
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        var listid = args.SHLISTID;
        var i = args.ITEMID;
        var TH: ToastHandler = ToastHandler(requireContext());
        lifecycleScope.launch(Dispatchers.Main)
        {
            var ctrl: ShoppingAssistantController = ShoppingAssistantController(requireContext());
            List = ctrl.getList(ctrl.getUID()!!, args.SHLISTID);
            var item = List?.Products?.get(args.ITEMID);
            if (item != null) {
                binding.twTitle.text = item.Product?.Name
                val price = ctrl.getPriceForProduct(
                    item.Product?.ID.toString(),
                    List?.Store?.ID.toString()
                )?.Price;
                ItemPrice=price!!;
                binding.twPrice.text = price.toString();
                binding.twAmount.text = item.Amount.toString();
                if (price != null) {
                    binding.twTotal.text = (price * item.Amount).toString()
                }
                binding.txtNotes.setText(item.Notes, TextView.BufferType.EDITABLE)
                UpdateUI()
            }
        }
        binding.btMinus.setOnClickListener {
            if(List?.Products?.get(i)?.Amount!! >1)
            {
                List!!.Products?.get(i)?.Amount = List!!.Products!![i]?.Amount?.minus(1)!!;
                hasbeenchanged=true;
                UpdateUI()
            }
            else
            {
                TH.showToast(R.string.tst_less_than_one, Toast.LENGTH_SHORT);
            }
        }

        binding.btPlus.setOnClickListener {
            List!!.Products?.get(i)?.Amount = List!!.Products!![i]?.Amount?.plus(1)!!;
            hasbeenchanged=true;
            UpdateUI()
        }
        binding.ibChk.setOnClickListener {
            List!!.Products?.get(i)?.Checked=true;
            hasbeenchanged=true;
            UpdateUI();
        }
        binding.ibUnChk.setOnClickListener {
            List!!.Products?.get(i)?.Checked=false;
            hasbeenchanged=true;
            UpdateUI();
        }

        binding.btAccept.setOnClickListener {
            if(binding.txtNotes.text.toString()!=List!!.Products?.get(i)?.Notes)
            {
                List!!.Products?.get(i)?.Notes=binding.txtNotes.text.toString();
                hasbeenchanged=true;
            }
            if(hasbeenchanged)
            {
                var ctrl:ShoppingAssistantController= ShoppingAssistantController(requireContext());
                lifecycleScope.launch(Dispatchers.Main) {
                    ctrl.updateList(List!!);
                    var dir = ListItemViewFragmentDirections.actionListItemViewFragmentToShoppingListFragment(listid);
                    navController.navigate(dir);
                }
                TH.showToast(R.string.tst_updated_item,Toast.LENGTH_SHORT);
            }
        }
    }


    private fun UpdateUI(){
        val item = List?.Products?.get(args.ITEMID);
        if (item != null) {
            if(item.Checked) {
                binding.iwChk.visibility=View.VISIBLE;
            } else {
                binding.iwChk.visibility=View.INVISIBLE;
            }
        }
        binding.twPrice.text = ItemPrice.toString();
        binding.twAmount.text = item?.Amount.toString();
        binding.twTotal.text = (ItemPrice * item?.Amount!!).toString()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListItemViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListItemViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}