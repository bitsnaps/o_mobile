package com.ehealthinformatics.odoorx.rxshop.data.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ehealthinformatics.odoorx.rxshop.R;
import com.ehealthinformatics.rxshop.listeners.OnItemClickListener;
import com.ehealthinformatics.rxshop.viewholder.SimpleListViewHolder;
import com.ehealthinformatics.odoorx.core.data.dto.SimpleItem;

import java.util.ArrayList;
import java.util.List;

public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListViewHolder> implements Filterable {

    private final Activity context;
    private final List<SimpleItem> simpleItems;
    private List<SimpleItem> simpleItemsFiltered;
    private final List<OnItemClickListener> onItemClickListeners = new ArrayList();

    public SimpleListAdapter(@NonNull Activity context, List<SimpleItem> simpleItems, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.simpleItems = simpleItems;
        this.simpleItemsFiltered = simpleItems;
        this.onItemClickListeners.add(onItemClickListener);
    }

    @NonNull
    @Override
    public SimpleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_simple_item,  parent,false);
        return  new SimpleListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleListViewHolder holder, final int position) {
        final SimpleItem simpleItem = simpleItemsFiltered.get(position);
        holder.getLabel().setText(simpleItem.getLabel());
        holder.getLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = onItemClickListeners.size();
                while(size-- > 0)
                onItemClickListeners.get(size).onItemClick(v, simpleItem, position);
            }
        });
        holder.getLabel().setTag(simpleItem.getValue());

    }

    @Override
    public int getItemCount() {
        return simpleItemsFiltered.size();
    }

    public void addListener(OnItemClickListener onItemClickListener) {
        onItemClickListeners.add(onItemClickListener);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    simpleItemsFiltered = simpleItems;
                } else {
                    List<SimpleItem> filteredList = new ArrayList<>();
                    for (SimpleItem row : simpleItems) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getLabel().toLowerCase().contains(charString.toLowerCase()) || row.getValue().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    simpleItemsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = simpleItemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                simpleItemsFiltered = (ArrayList<SimpleItem>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
}
