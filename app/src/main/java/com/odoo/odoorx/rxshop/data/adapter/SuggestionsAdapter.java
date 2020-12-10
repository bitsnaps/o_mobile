package com.odoo.odoorx.rxshop.data.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.odoo.odoorx.rxshop.R;
import com.odoo.rxshop.listeners.OnItemClickListener;
import com.odoo.rxshop.viewholder.SuggestionViewHolder;
import com.odoo.odoorx.core.data.dto.Suggestion;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionViewHolder> {

    private static final String SEARCH_HISTORY_KEY = "C_SEARCH_HISTORY_KEY";
    private static final int MAX_HISTORY_ITEMS = 5;

    private List<Suggestion> items;
    private OnItemClickListener<Suggestion> onItemClickListener;
    private SharedPreferences prefs;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SuggestionsAdapter(Context context,OnItemClickListener<Suggestion> onItemClickListener) {
        prefs = context.getSharedPreferences("PREF_RECENT_SEARCH", Context.MODE_PRIVATE);
        this.items = getSearchHistory();
        Collections.reverse(this.items);
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_suggestion, parent, false);
        SuggestionViewHolder vh = new SuggestionViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {
        final Suggestion s = items.get(position);
        final int pos = position;
        holder.getName().setText(s.getName());
        holder.getParentLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onItemClickListener.onItemClick(v, s, pos);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void refreshItems() {
        this.items = getSearchHistory();
        Collections.reverse(this.items);
        notifyDataSetChanged();
    }

    public void resetItems(List<Suggestion> suggestions) {
        this.items = suggestions;
        notifyDataSetChanged();
    }

    private class SearchObject implements Serializable {
        public SearchObject(List<Suggestion> items) {
            this.items = items;
        }

        public List<Suggestion> items;
    }

    /**
     * To save last state request
     */
    public void addSearchHistory(Suggestion s) {
        SearchObject searchObject = new SearchObject(getSearchHistory());
        if (searchObject.items.contains(s)) searchObject.items.remove(s);
        searchObject.items.add(s);
        if (searchObject.items.size() > MAX_HISTORY_ITEMS) searchObject.items.remove(0);
        String json = new Gson().toJson(searchObject, SearchObject.class);
        prefs.edit().putString(SEARCH_HISTORY_KEY, json).apply();
    }

    private List<Suggestion> getSearchHistory() {
        String json = prefs.getString(SEARCH_HISTORY_KEY, "");
        if (json.equals("")) return new ArrayList<>();
        SearchObject searchObject = new Gson().fromJson(json, SearchObject.class);
        if(searchObject == null || searchObject.items == null) return new ArrayList<>();
        return searchObject.items;
    }
}