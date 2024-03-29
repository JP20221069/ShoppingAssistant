package com.jspj.shoppingassistant

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.adapter.CustomAdapter
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentAddProductBinding
import com.jspj.shoppingassistant.databinding.FragmentProductBinding
import com.jspj.shoppingassistant.model.ItemsViewModel
import com.jspj.shoppingassistant.model.Product
import com.jspj.shoppingassistant.model.ShoppingItem
import com.jspj.shoppingassistant.model.ShoppingList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAddProduct.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAddProduct : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var navController: NavController
    private var selectedProduct: Product?=null;
    private var selectedIndex:Int=-1;
    private val args:FragmentAddProductArgs by navArgs();
    private var selectmode:Boolean=false;
    private var searchCriteria:String="";
    private var Products:List<Product> = mutableListOf()
    private var scanflag = false;

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
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        var np = binding.npAmount;
        np.minValue=1;
        np.maxValue=Int.MAX_VALUE;
        np.value=1;
        var recyclerView = binding.rwProducts;
        recyclerView.layoutManager = LinearLayoutManager(context)
        var ctrl:ShoppingAssistantController= ShoppingAssistantController(requireContext());
        UpdateData()

        binding.btConfirm.setOnClickListener{

            var ctrl:ShoppingAssistantController = ShoppingAssistantController(requireContext());
            var TH:ToastHandler = ToastHandler(requireContext());


            lifecycleScope.launch(Dispatchers.Main){
                if(selectedProduct!=null) {
                    var list: ShoppingList = ShoppingList()
                    var listid = args.SHLISTID;
                    list = ctrl.getList(ctrl.getUID()!!, listid)!!;

                    list.addProduct(selectedProduct!!, np.value);
                    ctrl.updateList(list);
                    TH.showToast(R.string.tst_added_product, Toast.LENGTH_SHORT)
                    var directions=FragmentAddProductDirections.actionFragmentAddProductToShoppingListFragment(listid)
                    navController.navigate(directions);
                }
                else
                {

                    TH.showToast(R.string.tst_no_product_selected, Toast.LENGTH_SHORT)
                }
            }
        }

        binding.ibScan.setOnClickListener {

            ScanCode()


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
            builder.setNeutralButton(R.string.btn_remove_filter, DialogInterface.OnClickListener{ dialog, which->FilterData(true)})
            builder.setNegativeButton(R.string.btn_cancel,
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
        }

    }


    private fun updateCardBackgroundColor(position: Int,color:Int) {
        // Update the background color of the clicked card
        // You can access the card at the specified position and modify its background color
        var recyclerView = binding.rwProducts;
        val cardView = recyclerView.layoutManager?.findViewByPosition(position)?.findViewById<CardView>(R.id.cardview)
        cardView?.setCardBackgroundColor(resources.getColor(color))
    }

    private fun UpdateData() {

        var products:List<Product> = ArrayList();
        val ctrl: ShoppingAssistantController = ShoppingAssistantController(requireContext());
        lifecycleScope.launch(Dispatchers.Main) {
            products = ctrl.getProducts()
            Products=products;
            var data:ArrayList<ItemsViewModel> = arrayListOf();
            for(p in products)
            {
                data.add(ItemsViewModel(R.drawable.product,p.Name,p.ID.toString()))
            }
            SetAdapter(data);

        }
    }

    private fun FilterData(removeFilter:Boolean=false)
    {
        var data: ArrayList<ItemsViewModel> = arrayListOf();
        for (p in Products) {
            if (p.Name?.contains(searchCriteria)!! || removeFilter == true || searchCriteria=="") {
                data.add(
                    ItemsViewModel(
                        R.drawable.product,
                        p.Name,
                        p.ID.toString()
                    )
                )
            }
        }
        SetAdapter(data);

        if(removeFilter)
        {
            searchCriteria="";
        }
        if(scanflag==true) {
            Handler(Looper.getMainLooper()).postDelayed({
                updateCardBackgroundColor(0, R.color.sys_selection)
                scanflag = false
            }, 100)
        }
    }

    private fun SetAdapter(data: ArrayList<ItemsViewModel>) {
        val recyclerView = binding.rwProducts;
        val ctrl:ShoppingAssistantController = ShoppingAssistantController(requireContext());
        val adapter = CustomAdapter(data)
        adapter.setOnClickListener(object:CustomAdapter.OnClickListener{
            override fun onClick(position: Int, model: ItemsViewModel) {
                if(selectedIndex!=-1)
                {
                    updateCardBackgroundColor(selectedIndex,R.color.sys_background);
                }
                selectedIndex=position;
                lifecycleScope.launch(Dispatchers.Main)
                {
                    selectedProduct=ctrl.getProductById(model.payload);
                }
                updateCardBackgroundColor(position,R.color.sys_selection);
            }
        })

        recyclerView.adapter = adapter;


    }

    /*private fun SimulateScan()
    {
        var ctrl:ShoppingAssistantController= ShoppingAssistantController(requireContext());
        var p: Product? =null;
        lifecycleScope.launch(Dispatchers.Main)
        {
            p=ctrl.getProductByBarcode("123")!!
        }.invokeOnCompletion {
            searchCriteria= p?.Name!!;
            selectedProduct=p;
            selectedIndex=Products.indexOf(p);
            scanflag=true;
            FilterData();
        }
    }*/

    private fun ScanCode() {
        val options = ScanOptions()
        options.setPrompt("Volume up to flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setCaptureActivity(CaptureAct::class.java)
        barLaucher.launch(options)
    }
    var barLaucher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            var ctrl: ShoppingAssistantController = ShoppingAssistantController(requireContext());
            var p: Product? = null;
            lifecycleScope.launch(Dispatchers.Main)
            {
                p = ctrl.getProductByBarcode(result.contents)
            }.invokeOnCompletion {
                if(p==null)
                {
                    var TH: ToastHandler = ToastHandler(requireContext());
                    TH.showToast(getString(R.string.tst_noproductfound), Toast.LENGTH_SHORT);
                }
                else {
                    searchCriteria = p?.Name!!;
                    selectedProduct = p;
                    selectedIndex = Products.indexOf(p);
                    scanflag = true;
                    FilterData();
                }
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAddProduct.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentAddProduct().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}