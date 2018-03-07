package com.mathildeguillossou.thebluebattery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * @author mathildeguillossou on 26/02/2018
 */
class DeviceListAdapter(context: Context, var resource: Int, private var list: ArrayList<DeviceItem>) :
        ArrayAdapter<DeviceItem>(context, resource, list) {

    /**
     * Holder for the list items.
     */
    private inner class Holder {
        internal var name: TextView? = null
        internal var address: TextView? = null
        internal var battery: TextView? = null
    }

    private var vi: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var holder: Holder
        var retView: View

        val (deviceName, address, _, battery) = getItem(position)


        if(convertView == null) {
            retView = this.vi.inflate(resource, null)
            holder = Holder()

            holder.name = retView.findViewById(R.id.titleTextView)
            holder.address = retView.findViewById(R.id.macAddress)
            holder.battery = retView.findViewById(R.id.battery)
            retView.tag = holder

        } else {
            holder = convertView.tag as Holder
            retView = convertView
        }

        holder.name?.text = deviceName
        holder.address?.text = address
        holder.battery?.text = battery.toString()

        return retView
    }

    fun update(battery : Int, position: Int) {
        getItem(position).battery = battery
        notifyDataSetChanged()
    }

    fun list():  ArrayList<DeviceItem> {
        return list
    }
}