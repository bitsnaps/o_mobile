package com.odoo.base.addons.abirex.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.base.addons.abirex.model.OrderLine;

import java.util.ArrayList;
import java.util.List;

public class SalesAdapter extends ArrayAdapter<OrderLine> {

    private final Activity context;
    private final List<OrderLine> orderLines;


    public SalesAdapter(@NonNull Activity context, List<OrderLine> orderLines) {
        super(context, R.layout.pos_order_date_row_item);
        this.context = context;
        this.orderLines = orderLines;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.pos_order_date_row_item, null, true);
        TextView tvProductName = (TextView) rowView.findViewById(R.id.tv_product_name);
        TextView tvPrice = (TextView) rowView.findViewById(R.id.tv_price);
        TextView tvTotalAmount = (TextView) rowView.findViewById(R.id.tv_total_amount);
        //Set Values
        OrderLine orderLine = orderLines.get(position);
        tvProductName.setText(orderLine.getProduct().getName());
        tvPrice.setText(orderLine.getUnitPrice()+"");
        tvTotalAmount.setText(orderLine.getSubTotalWithTax()+"");
        return rowView;
    }
}
