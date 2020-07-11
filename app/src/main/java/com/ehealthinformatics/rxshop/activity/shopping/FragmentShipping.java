package com.ehealthinformatics.rxshop.activity.shopping;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ehealthinformatics.odoorx.rxshop.R;
import com.ehealthinformatics.odoorx.core.data.dto.Customer;

public class FragmentShipping extends Fragment {


    Customer customer;
    EditText name;
    EditText email;
    EditText phone;
    EditText address;
    EditText state;
    EditText area;
    EditText locality;
    EditText country;

    public FragmentShipping(Customer customer) {
        this.customer = customer;
    }

    private void initControls(View view){
        name = view.findViewById(R.id.et_name);
        email = view.findViewById(R.id.et_email);
        phone = view.findViewById(R.id.et_phone);
        address = view.findViewById(R.id.et_ship_street);
        state = view.findViewById(R.id.et_ship_state);
        country = view.findViewById(R.id.et_ship_country);
        area = view.findViewById(R.id.et_ship_area);
        locality = view.findViewById(R.id.et__ship_locality);

        name.setText(customer.getDisplayName());
        email.setText(customer.getEmail());
        phone.setText(customer.getPhone());
        address.setText(customer.getAddress());
        state.setText(customer.getState().getName());
        country.setText(customer.getState().getCountry().getName());
        locality.setText(customer.getLocality());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shipping, container, false);
        initControls(root);
        initListeners(root);
        return root;
    }

    private void initListeners(View view){
        (view.findViewById(R.id.et_ship_state)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStateDialog(v);
            }
        });

        (view.findViewById(R.id.et_ship_country)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryDialog(v);
            }
        });
    }


    private void showCountryDialog(final View v) {
        final String[] array = new String[]{
                "United State", "Germany", "United Kingdom", "Australia"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Country");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    private void showStateDialog(final View v) {
        final String[] array = new String[]{
                "Arizona", "California", "Florida", "Massachusetts", "New York", "Washington"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("State");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}