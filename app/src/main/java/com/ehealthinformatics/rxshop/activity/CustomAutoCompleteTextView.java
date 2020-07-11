package com.ehealthinformatics.rxshop.activity;

import java.util.HashMap;

import android.content.Context;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;

    /** Customizing AutoCompleteTextView to return Country Name
     *  corresponding to the selected item
     */
    public class CustomAutoCompleteTextView extends AppCompatAutoCompleteTextView {

        public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        /** Returns the country name corresponding to the selected item */
        @Override
        protected CharSequence convertSelectionToString(Object selectedItem) {
            /** Each item in the autocompetetextview suggestion list is a hashmap object */
            HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
            return hm.get("txt");
        }
    }