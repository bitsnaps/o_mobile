package com.odoo.addons.abirex.customer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.base.addons.abirex.model.Product;
import com.odoo.data.LazyList;


public class ProductCategoryListAdapter extends RecyclerView.Adapter<ProductCategoryListAdapter.ProductCategoryViewHolder> {

    private LazyList<Product> productLazyList;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ProductCategoryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView mCardView;
        TextView name;
        TextView type;
        ImageView image;

        public ProductCategoryViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cvProduct);
            name = (TextView) itemView.findViewById(R.id.tv_product_namr);
            type = (TextView) itemView.findViewById(R.id.tv_product_type);
            image = (ImageView) itemView.findViewById(R.id.tv_product_image);
        }
    }

//    public class CustomerProductViewHolder extends RecyclerView.ViewHolder {
//        public TextView title, year, genre;
//
//        public CustomerProductViewHolder(View view) {
//            super(view);
//            title = (TextView) view.findViewById(R.id.title);
//            genre = (TextView) view.findViewById(R.id.genre);
//            year = (TextView) view.findViewById(R.id.year);
//        }
//    }

    @Override
    public ProductCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_customer_product_list_item, parent, false);

        return new ProductCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductCategoryViewHolder holder, int position) {
        Product product = productLazyList.get(position);

        holder.name.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return productLazyList.size();
    }
}
