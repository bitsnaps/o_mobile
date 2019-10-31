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
import com.odoo.base.addons.abirex.dto.PosOrderLine;

import java.util.List;

public class SalesAdapter extends ArrayAdapter<PosOrderLine> {

    private final Activity context;
    private final List<PosOrderLine> posOrderLines;


    public SalesAdapter(@NonNull Activity context, List<PosOrderLine> posOrderLines) {
        super(context, 0, posOrderLines);
        this.context = context;
        this.posOrderLines = posOrderLines;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.layout_list_item_order_pos_date, null, true);
        TextView tvProductName = (TextView) rowView.findViewById(R.id.tv_product_name);
        TextView tvPrice = (TextView) rowView.findViewById(R.id.tv_price);
        TextView tvTotalAmount = (TextView) rowView.findViewById(R.id.tv_total_amount);
        //Set Values
        PosOrderLine posOrderLine = posOrderLines.get(position);
        tvProductName.setText(posOrderLine.getProduct().getName());
        tvPrice.setText(posOrderLine.getUnitPrice()+"");
        tvTotalAmount.setText(posOrderLine.getSubTotalWithTax()+"");
        return rowView;
    }
}
