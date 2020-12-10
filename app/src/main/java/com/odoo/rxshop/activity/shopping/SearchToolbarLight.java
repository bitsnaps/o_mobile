package com.odoo.rxshop.activity.shopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.odoo.rxshop.listeners.OnItemClickListener;
import com.odoo.odoorx.core.base.utils.OAppBarUtils;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.viewmodel.ProductListViewModel;
import com.odoo.odoorx.rxshop.R;
import com.odoo.rxshop.utils.ViewAnimation;
import com.odoo.odoorx.rxshop.data.adapter.CartSuggestionsAdapter;
import com.odoo.odoorx.core.data.dto.Product;
import com.odoo.odoorx.rxshop.base.support.OdooCompatActivity;
import com.odoo.odoorx.core.data.db.Columns;

import java.util.ArrayList;
import java.util.List;


public class SearchToolbarLight extends OdooCompatActivity {

    private Toolbar toolbar;
    private EditText et_search;
    private ImageButton bt_clear;

    private ProgressBar progress_bar;
    private LinearLayout lyt_no_result;

    private RecyclerView recyclerSuggestion;
    private CartSuggestionsAdapter mAdapterSuggestion;
    private LinearLayout lyt_suggestion;
    private ProductListViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_toolbar_light);
        initToolbar();
        initComponent();
        initDataLoad();
    }

    private void initToolbar() {
        OAppBarUtils.setAppBar(this, R.id.toolbar, true);
    }

    public interface OnQueryComplete{
         void queryComplete(Boolean isEmpty);
    }

    private void initComponent() {
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_result = (LinearLayout) findViewById(R.id.lyt_no_result);

        lyt_suggestion = (LinearLayout) findViewById(R.id.lyt_suggestion);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(textWatcher);

        bt_clear = (ImageButton) findViewById(R.id.bt_clear);
        bt_clear.setVisibility(View.GONE);
        recyclerSuggestion = (RecyclerView) findViewById(R.id.rv_suggestion);

        recyclerSuggestion.setLayoutManager(new LinearLayoutManager(this));
        recyclerSuggestion.setHasFixedSize(true);

        //set data and list adapter suggestion
        mAdapterSuggestion = new CartSuggestionsAdapter(this,
                new OnItemClickListener<Product>() {
                    @Override
                    public void onItemClick(View view, Product product, int pos) {
                        et_search.setText(product.getName());
                        ViewAnimation.collapse(lyt_suggestion);
                        hideKeyboard();
                        searchAction(product);
                    }
                },
                new OnQueryComplete(){

                    @Override
                    public void queryComplete(Boolean isEmpty) {
                        lyt_no_result.setVisibility(isEmpty ? View.VISIBLE : View.INVISIBLE);
                    }
                }
                );
        recyclerSuggestion.setAdapter(mAdapterSuggestion);
        showSuggestionSearch();

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    //searchAction(null);
                    return true;
                }
                return false;
            }
        });

        et_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showSuggestionSearch();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });
    }

    private void initDataLoad(){
        viewModel = ViewModelProviders.of(this).get(ProductListViewModel.class);
        viewModel.getData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> products) {
                List<String> productNames = new ArrayList<>();
                mAdapterSuggestion.resetItems(products);
            }
        });
    }


    private void showSuggestionSearch() {

        ViewAnimation.expand(lyt_suggestion);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            if (c.toString().trim().length() == 0) {
                bt_clear.setVisibility(View.GONE);
            } else {
                bt_clear.setVisibility(View.VISIBLE);
            }
            if(c.length() >= 3)
            viewModel.searchFilter(c.toString(), QueryFields.all());
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchAction(Product product) {
        progress_bar.setVisibility(View.VISIBLE);
        ViewAnimation.collapse(lyt_suggestion);
        lyt_no_result.setVisibility(View.GONE);

        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress_bar.setVisibility(View.GONE);
                    lyt_no_result.setVisibility(View.VISIBLE);
                }
            }, 2000);
            mAdapterSuggestion.addSearchHistory(product.getName());
            Intent intent = new Intent();

            intent.putExtra(Columns.PosOrderLine.product_id, product.getId());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Please fill search input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
