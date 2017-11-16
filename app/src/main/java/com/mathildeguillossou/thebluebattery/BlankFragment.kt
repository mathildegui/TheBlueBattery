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
import android.widget.*
import android.widget.TextView
import android.widget.AdapterView
import kotlinx.android.synthetic.main.fragment_blank.*
import java.util.*
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.ContentValues.TAG


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BlankFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment(), AdapterView.OnItemClickListener {

    var adapter: BluetoothAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null
    private var mAdapter: ArrayAdapter<DeviceItem>? = null
    private var deviceItemList: ArrayList<DeviceItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = BluetoothAdapter.getDefaultAdapter()


        deviceItemList = ArrayList()
        val pairedDevices = adapter?.bondedDevices
        if (pairedDevices!!.size > 0) {
            for (device in pairedDevices) {
                Log.d("address to ", device.address)
                if(device.address == "00:11:67:2C:E8:F7") {
                    Log.d("connec to ", "00:11:67:2C:E8:F7")
                    //ConnectThread(device, UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")).connect()
                } else {
                    Log.d("NO connec to", "00:11:67:2C:E8:F7")
                }


                val newDevice = DeviceItem(device.name, device.address, false)
                deviceItemList!!.add(newDevice)

            }
        }

        // If there are no devices, add an item that states so. It will be handled in the view.
        if (deviceItemList!!.size == 0) {
            deviceItemList!!.add(DeviceItem("No Devices", "", false))
        }

        Log.d("DEVICELIST", "DeviceList populated\n")

        mAdapter = DeviceListAdapter(context, deviceItemList)

        Log.d("DEVICELIST", "Adapter created\n")
    }

    private val bReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d("action", action)
            if (BluetoothDevice.ACTION_FOUND == action) {
                Log.d("DEVICELIST", "Bluetooth device found\n")
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Create a new device item
                val newDevice = DeviceItem(device.name, device.address, false)
                // Add it to our adapter
                mAdapter?.add(newDevice)
                mAdapter?.notifyDataSetChanged()


                if(device.address.equals("00:11:67:2C:E8:F7")) {
                    //ConnectThread(device, UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")).connect()
                }
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


        scan.setOnClickListener({
            mAdapter!!.clear()
            Log.d("setOnClickListener", "click")
            val filter = IntentFilter()
            filter.addAction(BluetoothDevice.ACTION_FOUND)
            activity.registerReceiver(bReciever, filter)

            adapter?.startDiscovery()


        })

        /*scan.setOnCheckedChangeListener { _ , isChecked ->
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            if (isChecked) {
                mAdapter!!.clear()
                activity.registerReceiver(bReciever, filter)
                adapter?.startDiscovery()
            } else {
                activity.unregisterReceiver(bReciever)
                adapter?.cancelDiscovery()
            }
        }*/
    }

    fun getbattery(address: String) {
        activity.startService(Intent(activity, BLEService::class.java).putExtra("address", address))
        /*val batteryService = mBluetoothGatt.getService(Battery_Service_UUID)
        if (batteryService == null) {
            Log.d(TAG, "Battery service not found!")
            return
        }

        val batteryLevel = batteryService!!.getCharacteristic(Battery_Level_UUID)
        if (batteryLevel == null) {
            Log.d(TAG, "Battery level not found!")
            return
        }
        mBluetoothGatt.readCharacteristic(batteryLevel)
        Log.v(TAG, "batteryLevel = " + mBluetoothGatt.readCharacteristic(batteryLevel))*/
    }


    override
    fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        Log.d("DEVICELIST", "onItemClick position: " + position +
                " id: " + id + " name: " + deviceItemList!![position].deviceName + "\n")
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener!!.onFragmentInteraction(deviceItemList!![position].deviceName)


            getbattery(deviceItemList!![position].address)

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
        fun onFragmentInteraction(id: String)
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
