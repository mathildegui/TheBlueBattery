package com.mathildeguillossou.thebluebattery

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.content.BroadcastReceiver
import android.util.Log
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.widget.AdapterView
import com.mathildeguillossou.thebluebattery.bluetooth.BleGattCallback
import kotlinx.android.synthetic.main.fragment_blank.*
import java.util.*
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
class BlankFragment : Fragment(), AdapterView.OnItemClickListener, ScanReceiver.ScanRequestListener {


    val TAG: String = BlankFragment::class.java.simpleName

    var adapter: BluetoothAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null
    private var mAdapter: DeviceListAdapter? = null
    private var deviceItemList: ArrayList<DeviceItem>? = null

    fun scan(ble : BluetoothService) {
        Log.d("SCAN", "scan processing ...")
        mAdapter?.clear()
        ble.devices(this)

        /*adapter?.bondedDevices!!
                .map { DeviceItem(it.name, it.address, false, 0) }
                .forEach { mAdapter?.add(it) }*/
    }

    override fun onScanFinish() {
        Log.d("SCAN", "onScanFinish")
        mListener?.hide()
    }

    override fun onDeviceFound(device: BluetoothDevice?) {
        Log.d("onDeviceFound", device.toString())
        val newDevice = DeviceItem(device?.name, device?.address, false, 0)
        if (!mAdapter!!.list().contains(newDevice)) {
            mAdapter?.add(newDevice)
            mAdapter?.notifyDataSetChanged()

            BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device?.address)
                    .connectGatt(this.context, false, BleGattCallback(context, mAdapter!!.getPosition(newDevice)))
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastManager.getInstance(this.activity).registerReceiver(mBroadcastReceiver, IntentFilter(BleGattCallback.ACTION_BATTERY))

        adapter = BluetoothAdapter.getDefaultAdapter()


        deviceItemList = ArrayList()
        /*val pairedDevices = adapter?.bondedDevices
        if (pairedDevices!!.size > 0) {
            for (device in pairedDevices) {
                Log.d("paired device", "address to ${device.address}")

                val newDevice = DeviceItem(device.name, device.address, false, 0)
                deviceItemList!!.add(newDevice)
            }
        }*/

        // If there are no devices, add an item that states so. It will be handled in the view.
//        if (deviceItemList!!.size == 0)
//            deviceItemList!!.add(DeviceItem("No Devices", "", false, 0))
        mAdapter = DeviceListAdapter(context, R.layout.device_list_item, deviceItemList!!)
    }

    // handler for received data from service
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Battery: " + intent.action)
            Log.d(TAG, "Battery: " + BleGattCallback.ACTION_BATTERY)
            if (intent.action == BleGattCallback.ACTION_BATTERY) {
                val battery = intent.getIntExtra(BleGattCallback.EXTRA_PARAM_BATTERY, -1)
                val position = intent.getIntExtra(BleGattCallback.EXTRA_PARAM_POSITION, -1)
                Log.d(TAG, "Battery: " + battery.toString())
                if(position != -1) mAdapter?.update(battery, position)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_blank, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        list.adapter = mAdapter
        // Set OnItemClickListener so we can be notified on item clicks
        list!!.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d("DEVICELIST", "onItemClick position: " + position +
                " id: " + id + " name: " + deviceItemList!![position].deviceName + "\n"+ deviceItemList!![position].address)
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            if(deviceItemList != null) {
                deviceItemList!![position].address?.let { mListener?.connect(it, position) }
            }

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
        LocalBroadcastManager.getInstance(this.activity).unregisterReceiver(mBroadcastReceiver)
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
        fun hide()
        fun connect(address: String?, position: Int)
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
