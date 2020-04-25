package com.ehealthinformatics.data.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ehealthinformatics.data.viewmodel.ProductListViewModel;
import com.ehealthinformatics.R;
import com.ehealthinformatics.data.dto.Product;

import java.util.ArrayList;

public class ProductSugestionsAdapter extends ArrayAdapter<Product> implements Filterable {

    private final Context context;
    private final ProductListViewModel productListViewModel;
    private final ArrayList<Product> products;
    private int DEFAULT_QTY = 1;

    public ProductSugestionsAdapter(@NonNull Context context, ProductListViewModel productListViewModel) {
        super(context, R.layout.layout_list_item_order_pos_date);
        this.context = context;
        this.productListViewModel = productListViewModel;
        this.products = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position){
        return products.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_list_item_order_pos_date, null, true);
        TextView tvProductName = (TextView) rowView.findViewById(R.id.tv_product_name);
        TextView tvPrice = (TextView) rowView.findViewById(R.id.tv_pos_order_total);
        TextView tvTotalAmount = (TextView) rowView.findViewById(R.id.tv_total_amount);
        //Set Values
        Product product = products.get(position);
        tvProductName.setText(product.getName());
        tvPrice.setText(DEFAULT_QTY + "UoM");
        tvTotalAmount.setText((product.getPrice() * DEFAULT_QTY) + "");
        return rowView;
    }


}
