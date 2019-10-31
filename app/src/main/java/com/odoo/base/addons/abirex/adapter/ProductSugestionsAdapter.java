package com.odoo.base.addons.abirex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.base.addons.abirex.dao.ProductDao;
import com.odoo.base.addons.abirex.dto.Product;
import com.odoo.data.LazyList;

import java.util.ArrayList;
import java.util.List;

public class ProductSugestionsAdapter extends ArrayAdapter<Product> implements Filterable {

    private final Context context;
    private final ProductDao productDao;
    private final ArrayList<Product> products;
    private int DEFAULT_QTY = 1;


    public ProductSugestionsAdapter(@NonNull Context context, ProductDao productDao) {
        super(context, R.layout.layout_list_item_order_pos_date);
        this.context = context;
        this.productDao = productDao;
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
        TextView tvPrice = (TextView) rowView.findViewById(R.id.tv_price);
        TextView tvTotalAmount = (TextView) rowView.findViewById(R.id.tv_total_amount);
        //Set Values
        Product product = products.get(position);
        tvProductName.setText(product.getName());
        tvPrice.setText(DEFAULT_QTY + "UoM");
        tvTotalAmount.setText((product.getPrice() * DEFAULT_QTY) + "");
        return rowView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new ProductFilter(this, products);
    }


    private class ProductFilter extends Filter {

        ProductSugestionsAdapter productSugestionsAdapter;
        List<Product> originalList;
        List<Product> filteredList;
        Loader<LazyList<Product>> productLoader;
        Loader.OnLoadCompleteListener<LazyList<Product>> onLoadCompleteListener;
        Loader.OnLoadCanceledListener<LazyList<Product>> onLoadCanceledListener;

        public ProductFilter(ProductSugestionsAdapter productSugestionsAdapter, List<Product> originalList) {
            super();
            this.productSugestionsAdapter = productSugestionsAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
            onLoadCompleteListener = new Loader.OnLoadCompleteListener<LazyList<Product>>(){

                @Override
                public void onLoadComplete(Loader<LazyList<Product>> loader, LazyList<Product> dbProducts) {
                    products.clear();
                    for(int i = 0; i < dbProducts.size(); i++){
                        Product product = dbProducts.get(i);
                        products.add(product);
                    }
                }
            };

            onLoadCanceledListener = new Loader.OnLoadCanceledListener<LazyList<Product>>() {
                @Override
                public void onLoadCanceled(Loader<LazyList<Product>> loader) {
                    products.clear();
                }
            };
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if(constraint == null ||  constraint.toString().isEmpty()) { return results; }
            String filterPattern = constraint.toString().toLowerCase().trim();
            if(productLoader != null && productLoader.isStarted()){
               productLoader.cancelLoad();
            }
            productLoader = productDao.searchByName(filterPattern);
            productLoader.startLoading();
            productLoader.registerListener(0, onLoadCompleteListener);

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                for (int i = 0; i < originalList.size(); i++) {
                    Product product = originalList.get(i);
                    if (product.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(product);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results != null && results.count > 0){
                notifyDataSetChanged();
            }else{
                notifyDataSetInvalidated();
            }
        }
    }

}
