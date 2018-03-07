package com.mathildeguillossou.thebluebattery

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.BroadcastReceiver
import android.util.Log
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.widget.TextView
import android.widget.AdapterView
import kotlinx.android.synthetic.main.fragment_blank.*
import java.util.*
import com.mathildeguillossou.thebluebattery.bluetooth.BindRequest
import com.mathildeguillossou.thebluebattery.bluetooth.BluetoothService
import com.mathildeguillossou.thebluebattery.bluetooth.ScanReceiver


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BlankFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment(), AdapterView.OnItemClickListener, ScanReceiver.ScanRequestListener, BindRequest.BindRequestListener {

    fun scan(ble : BluetoothService) {
        Log.d("SCAN", "scan processing")
        mAdapter?.clear()
        ble.devices(this)

        adapter?.bondedDevices!!
                .map { DeviceItem(it.name, it.address, false, 0) }
                .forEach { mAdapter?.add(it) }
    }

    override fun onScanFinish() {
        Log.d("SCAN", "onScanEnded")
        mListener?.hide()
//        hide()
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindingSuccess() {
        Log.d("SCAN", "onBindingSuccess")
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDeviceFound(device: BluetoothDevice?) {
        Log.d("DEVICE FOUND", device.toString())
//        if(device != null) {

            Log.d("DEVICE FOUND", device?.type.toString())
            Log.d("DEVICE FOUND", device?.name.toString())
            Log.d("DEVICE FOUND", device?.address.toString())
//            if (!device.name.isNullOrEmpty() && !device.address.isNullOrEmpty()) {
                val newDevice = DeviceItem(device?.name, device?.address, false, 0)
                // Add it to our adapter
                if (!mAdapter!!.list().contains(newDevice)) {
                    mAdapter?.add(newDevice)
                    mAdapter?.notifyDataSetChanged()
                }
//            }
//        }
    }

    override fun onBindingFailed() {
        Log.d("SCAN", "onBindingFailed")
        //To change body of created functions use File | Settings | File Templates.
    }

    val TAG: String = BlankFragment::class.java.simpleName

    var adapter: BluetoothAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null
    private var mAdapter: DeviceListAdapter? = null
    private var deviceItemList: ArrayList<DeviceItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter()
        /*filter.addAction(BLEService.CONNECTION_STATE_CHANGE)
        filter.addAction(BLEService.BATTERY)*/
        LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, filter)

        adapter = BluetoothAdapter.getDefaultAdapter()


        deviceItemList = ArrayList()
        val pairedDevices = adapter?.bondedDevices
        if (pairedDevices!!.size > 0) {
            for (device in pairedDevices) {
                Log.d("address to ", device.address)


                val newDevice = DeviceItem(device.name, device.address, false, 0)
                deviceItemList!!.add(newDevice)
            }
        }

        // If there are no devices, add an item that states so. It will be handled in the view.
        if (deviceItemList!!.size == 0) {
            deviceItemList!!.add(DeviceItem("No Devices", "", false, 0))
        }

        Log.d("DEVICELIST", "DeviceList populated\n")

        mAdapter = DeviceListAdapter(context, R.layout.device_list_item, deviceItemList!!)

        Log.d("DEVICELIST", "Adapter created\n")
    }

    // handler for received data from service
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            if (intent.action == BLEService.CONNECTION_STATE_CHANGE) {
//                val status = intent.getIntExtra(BLEService.EXTRA_PARAM_STATUS, -1)
//                Log.d(TAG, "Status: " + status.toString())
//                // do something
//            } else if (intent.action == BLEService.BATTERY) {
//                val battery = intent.getIntExtra(BLEService.EXTRA_PARAM_BATTERY, -1)
//                val position = intent.getIntExtra(BLEService.EXTRA_PARAM_BATTERY_POSITION, -1)
//                Log.d(TAG, "Batterye: " + battery.toString())
//                mAdapter?.update(battery, position)
//            }
        }
    }

    private val bReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d("action", action)
            if (BluetoothDevice.ACTION_FOUND == action) {
                Log.d("DEVICELIST", "Bluetooth device found\n")
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Create a new device item
                val newDevice = DeviceItem(device.name, device.address, false, 0)
                // Add it to our adapter
                mAdapter?.add(newDevice)
                mAdapter?.notifyDataSetChanged()


                if(device.address.equals("00:11:67:2C:E8:F7")) {
                    //ConnectThread(device, UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")).connect()
                }
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                mListener?.hide()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        list.adapter = mAdapter
        // Set OnItemClickListener so we can be notified on item clicks
        list!!.onItemClickListener = this
    }

    fun getbattery(address: String, position: Int) {
//        activity.startService(Intent(activity, BLEService::class.java).putExtra("address", address).putExtra("position", position))
    }


    override
    fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d("DEVICELIST", "onItemClick position: " + position +
                " id: " + id + " name: " + deviceItemList!![position].deviceName + "\n"+ deviceItemList!![position].address)
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            if(deviceItemList != null)
                deviceItemList!![position].address?.let { mListener?.onFragmentInteraction(it) }

            mListener?.discover(deviceItemList!![position].address, deviceItemList!![position].deviceName)
//            getbattery(deviceItemList!![position].address!!, position)
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    fun setEmptyText(emptyText: CharSequence) {
        val emptyView = list.emptyView

        if (emptyView is TextView) {
            emptyView.text = emptyText
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(macAddress: String)
        fun hide()
        fun discover(macAddress: String?, name: String?)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): BlankFragment {
            return BlankFragment()
        }
    }

}
