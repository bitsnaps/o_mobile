package com.ehealthinformatics.rxshop.activity.shopping;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehealthinformatics.odoorx.rxshop.R;

public class FragmentPayment extends Fragment {

    public FragmentPayment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment_type, container, false);

        return root;
    }
}