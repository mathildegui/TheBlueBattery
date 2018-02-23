package com.mathildeguillossou.thebluebattery;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * @author mathildeguillossou on 01/11/2017
 */

public class DeviceListAdapter extends ArrayAdapter<DeviceItem> {

    private Context context;

    public DeviceListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View line;
        @NonNull
        DeviceItem item = getItem(position);

        TextView macAddress;
        View viewToUse;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        viewToUse = mInflater.inflate(R.layout.device_list_item, null);
        holder = new ViewHolder();
        holder.titleText =  viewToUse.findViewById(R.id.titleTextView);
        viewToUse.setTag(holder);

        holder.titleText.setTextColor(context.getResources().getColor(android.R.color.black));

        macAddress =  viewToUse.findViewById(R.id.macAddress);
        line =  viewToUse.findViewById(R.id.line);

        String text;
        if(item.deviceName != null) text = item.deviceName;
        else text = item.address;
        holder.titleText.setText(text);
        macAddress.setText(item.address);

        if (item.deviceName != null && item.deviceName.equals("No Devices")) {
            macAddress.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            holder.titleText.setLayoutParams(params);
        }
        return viewToUse;
    }


}
