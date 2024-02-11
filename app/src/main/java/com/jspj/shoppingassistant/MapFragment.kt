package com.jspj.shoppingassistant

import android.graphics.Rect
import android.location.GpsStatus
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import com.jspj.shoppingassistant.Utils.ToastHandler
import com.jspj.shoppingassistant.controller.ShoppingAssistantController
import com.jspj.shoppingassistant.databinding.FragmentMapBinding
import com.jspj.shoppingassistant.model.Producer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.util.StorageUtils.getStorage
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(),MapListener, GpsStatus.Listener {

    private lateinit var binding:FragmentMapBinding;
    private lateinit var navController:NavController;
    private val args:MapFragmentArgs by navArgs();
    private var selection=3;
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
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UpdateUI();
        UpdateData();
        val TH: ToastHandler = ToastHandler(requireContext());
        binding.ibStores.setOnClickListener{
            selection=2;
            UpdateData();
            TH.showToast(R.string.tst_showstores, Toast.LENGTH_SHORT);
        }

        binding.ibManufacturers.setOnClickListener {
            selection=1;
            UpdateData();
            TH.showToast(R.string.tst_showmanufacturers, Toast.LENGTH_SHORT);
        }
        binding.ibBoth.setOnClickListener {
            selection=3;
            UpdateData();
            TH.showToast(R.string.tst_showboth, Toast.LENGTH_SHORT);
        }
        binding.ibShowMap.setOnClickListener {
            binding.osmmap.setTileSource(TileSourceFactory.MAPNIK);
            TH.showToast(R.string.tst_mapmode, Toast.LENGTH_SHORT);
        }
        binding.ibShowSat.setOnClickListener {
            binding.osmmap.setTileSource(TileSourceFactory.USGS_SAT);
            TH.showToast(R.string.tst_satellitemode, Toast.LENGTH_SHORT);
        }
    }

    private fun UpdateUI()
    {
        if(args.SHOWCOORDS)
        {
            binding.ibStores.visibility=View.INVISIBLE;
            binding.ibManufacturers.visibility=View.INVISIBLE;
            binding.ibBoth.visibility=View.INVISIBLE;
        }
        else
        {
            binding.ibStores.visibility=View.VISIBLE;
            binding.ibManufacturers.visibility=View.VISIBLE;
            binding.ibBoth.visibility=View.VISIBLE;
        }
    }
    private fun UpdateData()
    {
        val provider = Configuration.getInstance()
        provider.userAgentValue = org.osmdroid.thirdparty.BuildConfig.APPLICATION_ID;
        provider.osmdroidBasePath = getStorage()
        provider.osmdroidTileCache = getStorage()
        var map = binding.osmmap
        map.setTileSource(TileSourceFactory.MAPNIK)
        var mapPoint:GeoPoint?=null;
        if(args.SHOWCOORDS)
        {
            val startPoint = GeoPoint(args.LAT.toDouble(), args.LONG.toDouble())
            val startMarker = Marker(map)
            startMarker.setPosition(startPoint)
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.getOverlays().add(startMarker)
            mapPoint = GeoPoint(args.LAT.toDouble(), args.LONG.toDouble())
        }
        else
        {
            val ctrl:ShoppingAssistantController=ShoppingAssistantController();
            lifecycleScope.launch(Dispatchers.Main) {


                if(selection==1 || selection==3) {
                    var producers = ctrl.getProducers();
                    for (p in producers) {
                        val long = p.Coords.split(';')[0];
                        val lat = p.Coords.split(';')[1];
                        val startPoint = GeoPoint(long.toDouble(), lat.toDouble());
                        val startMarker = Marker(map)
                        startMarker.setPosition(startPoint)
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        map.getOverlays().add(startMarker)
                    }
                }
                if(selection==2 || selection==3) {
                    var stores = ctrl.getStores();
                    for (p in stores) {
                        val long = p.Coords.split(';')[0];
                        val lat = p.Coords.split(';')[1];
                        val startPoint = GeoPoint(long.toDouble(), lat.toDouble());
                        val startMarker = Marker(map)
                        startMarker.setPosition(startPoint)
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        map.getOverlays().add(startMarker)
                    }
                }
            }
        }
        map.setMultiTouchControls(true)
        map.getLocalVisibleRect(Rect())

        var mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        var controller = map.controller

        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        controller.setZoom(15.0)
        if(args.SHOWCOORDS) {
            controller.animateTo(mapPoint)
        }
        else
        {
            controller.setCenter(mMyLocationOverlay.myLocation);
            controller.animateTo(mMyLocationOverlay.myLocation)
        }
        map.addMapListener(this)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onScroll(event: ScrollEvent?): Boolean {
        // event?.source?.getMapCenter()
        Log.e("TAG", "onCreate:la ${event?.source?.getMapCenter()?.latitude}")
        Log.e("TAG", "onCreate:lo ${event?.source?.getMapCenter()?.longitude}")
        //  Log.e("TAG", "onScroll   x: ${event?.x}  y: ${event?.y}", )
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        //  event?.zoomLevel?.let { controller.setZoom(it) }


        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false;
    }

    override fun onGpsStatusChanged(event: Int) {


        TODO("Not yet implemented")
    }
}