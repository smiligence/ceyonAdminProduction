package com.smiligence.techAdmin.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smiligence.techAdmin.R;
import com.smiligence.techAdmin.bean.ItemDetails;

import java.util.ArrayList;

public class ItemReportsAdapter  extends ArrayAdapter<ItemDetails> {
    public Activity context;

    public ArrayList<ItemDetails> billreportsList;

    TextView itemName, itemqty, No_of_bills, Date_time;

    public ItemReportsAdapter(Activity context, ArrayList<ItemDetails> billreportsList) {
        super(context, R.layout.item_report_list, billreportsList);
        this.context = context;
        this.billreportsList = billreportsList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listview = inflater.inflate(R.layout.item_report_list, null, true);

        itemName = (TextView) listview.findViewById(R.id.itemname);
        itemqty = (TextView) listview.findViewById(R.id.itemqty);
        No_of_bills = listview.findViewById(R.id.noofbills);

        if(billreportsList != null && !billreportsList.isEmpty()){

            ItemDetails itemDetails = billreportsList.get(position);
            itemName.setText(itemDetails.getItemName());
           // No_of_bills.setText(String.valueOf(itemDetails.getBillCount()));
            itemqty.setText(String.valueOf(itemDetails.getItemBuyQuantity()));
          //  Date_time.setText(itemDetails.getItemCatagory());
        }

        return listview;
    }
}

