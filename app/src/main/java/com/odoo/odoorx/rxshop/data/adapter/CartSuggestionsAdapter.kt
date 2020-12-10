package com.odoo.odoorx.rxshop.data.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson
import com.odoo.odoorx.rxshop.R
import com.odoo.rxshop.listeners.OnItemClickListener
import com.odoo.rxshop.activity.shopping.SearchToolbarLight
import com.odoo.rxshop.viewholder.CartSuggestionViewHolder
import com.odoo.odoorx.core.config.OConstants
import com.odoo.odoorx.core.data.dto.Product

import java.io.Serializable
import java.util.ArrayList
import java.util.Collections

class CartSuggestionsAdapter(context: Context, var onItemClickListener: OnItemClickListener<Product>, var onQueryComplete: SearchToolbarLight.OnQueryComplete) : RecyclerView.Adapter<CartSuggestionViewHolder>() {

    private var items: List<Product> = ArrayList()
    private val prefs: SharedPreferences

    private//if (json.equals(""))
    //        SearchObject searchObject = new Gson().fromJson(json, SearchObject.class);
    //        if(searchObject == null || searchObject.items == null) return new ArrayList<>();
    //        return searchObject.items;
    val searchHistory: MutableList<String>
        get() {
            val json = prefs.getString(SEARCH_HISTORY_KEY, "")
            return ArrayList()
        }

    init {
        prefs = context.getSharedPreferences("PREF_RECENT_SEARCH", Context.MODE_PRIVATE)
        //this.items = getSearchHistory();
        Collections.reverse(this.items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartSuggestionViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_add_to_cart, parent, false)
        return CartSuggestionViewHolder(v)
    }

    override fun onBindViewHolder(holder: CartSuggestionViewHolder, position: Int) {
        val product = items[position]
        holder.name.text = product.name
        holder.price.text = OConstants.CURRENCY_SYMBOL + product.price.toString()
        holder.uom.text = product.productTemplate.uom!!.name
        holder.add.setOnClickListener { v -> onItemClickListener.onItemClick(v, product , position) }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return items.size
    }

    fun resetItems(items: List<Product>) {
        this.items = items
        onQueryComplete.queryComplete(items.isEmpty())
        notifyDataSetChanged()
    }

    fun refreshItems() {
        this.items = items
        Collections.reverse(this.items)
        notifyDataSetChanged()
    }

    private inner class SearchObject(items: MutableList<String>) : Serializable {

        var items: MutableList<String> = ArrayList()

        init {
            this.items = items
        }
    }

    /**
     * To save last state request
     */
    fun addSearchHistory(s: String) {
        val searchObject = SearchObject(searchHistory)
        if (searchObject.items.contains(s)) searchObject.items.remove(s)
        searchObject.items.add(s)
        if (searchObject.items.size > MAX_HISTORY_ITEMS) searchObject.items.removeAt(0)
        val json = Gson().toJson(searchObject, SearchObject::class.java)
        prefs.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    companion object {

        private val SEARCH_HISTORY_KEY = "_SEARCH_HISTORY_KEY"
        private val MAX_HISTORY_ITEMS = 5
    }


}