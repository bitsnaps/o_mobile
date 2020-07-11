package com.ehealthinformatics.rxshop.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ehealthinformatics.odoorx.rxshop.R;
import com.ehealthinformatics.odoorx.rxshop.data.adapter.SimpleListAdapter;
import com.ehealthinformatics.odoorx.core.data.db.Columns;
import com.ehealthinformatics.rxshop.activity.OdooActivity;
import com.ehealthinformatics.rxshop.activity.shopping.PaymentLine;
import com.ehealthinformatics.rxshop.listeners.OnItemClickListener;
import com.ehealthinformatics.odoorx.core.base.utils.IntentUtils;
import com.ehealthinformatics.odoorx.core.data.dto.AccountBankStatement;
import com.ehealthinformatics.odoorx.core.data.dto.PosOrder;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DialogUtils {

    public static void dialogDatePickerLight(Context context, FragmentManager fragmentManager,final View v) {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(

                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Long date = calendar.getTimeInMillis();
                        ((EditText) v).setText(Tools.getFormattedDateShort(date));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(ContextCompat.getColor(context, R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(fragmentManager, "Expiration Date");
    }


    public static void showPaymentDialog(final Context context, Boolean paymentSuccessful){

        int  color;
        int btnColor;
        String message;
        String dialogTitle;
        String command;


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_confirm_payment);
        LinearLayout llConfirmation = dialog.findViewById(R.id.ll_confirmation);
        Button btnClose = dialog.findViewById(R.id.bt_close);
        TextView tvCheckoutStatus = dialog.findViewById(R.id.tv_checkout_content);
        TextView tvCheckoutDialogTitle = dialog.findViewById(R.id.tv_checkout_dialog_title);

        if(paymentSuccessful){
            color = ContextCompat.getColor(context, R.color.light_green_400);
            btnColor = R.drawable.bt_rounded_green;
            message = "Your order has been sent successfully";
            dialogTitle = "Order sent";
        } else {
            color = ContextCompat.getColor(context, R.color.red_A400);
            btnColor = R.drawable.bt_rounded_red;
            message = "Your order was not sent successfully, Pls try again";
            dialogTitle = "Payment unsuccessful";
        }
        llConfirmation.setBackgroundColor(color);
        btnClose.setBackgroundResource(btnColor);

        btnClose.setText("View Order");
        tvCheckoutDialogTitle.setText(dialogTitle);
        tvCheckoutStatus.setText(message);

        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startActivity(context, OdooActivity.class, null);
                Toast.makeText(context, ((AppCompatButton)v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void showPaymentTypeDialog(final Context context, PosOrder posOrder){

        int  color;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.layout_payment_types);
        LinearLayout llConfirmation = dialog.findViewById(R.id.ll_payment_types);

        color = ContextCompat.getColor(context, R.color.light_green_400);

        llConfirmation.setBackgroundColor(color);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        loadPaymentTypes(context, llConfirmation, posOrder);
    }

    public static void showEnterTextDialog(final Context context, String defaultText, final View.OnClickListener onConfirm){

        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.layout_enter_text);
        ImageButton ibNavigateBack = dialog.findViewById(R.id.ib_text_dialog_navigate_back);
        final EditText etText = dialog.findViewById(R.id.et_text_dialog_text);
        final Button btnSave = dialog.findViewById(R.id.btn_text_dialog_save);
        final Button btnClear = dialog.findViewById(R.id.btn_dialog_text_clear);
        View.OnClickListener wrappedOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == btnSave.getId()){
                    onConfirm.onClick(etText);
                    dialog.dismiss();
                }
                if(v.getId() == R.id.ib_text_dialog_navigate_back){
                    dialog.dismiss();
                }
                if(v.getId() == R.id.btn_dialog_text_clear){
                    etText.setText("");
                }
            }
        };
        etText.setText(defaultText);
        etText.requestFocus();
        btnSave.setOnClickListener(wrappedOnClick);
        btnClear.setOnClickListener(wrappedOnClick);
        ibNavigateBack.setOnClickListener(wrappedOnClick);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.horizontalMargin = 0;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public static void showChooseImageDialog(final Context context, final View.OnClickListener viewClicked){

        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.layout_choose_image);
        LinearLayout llTakePicture = dialog.findViewById(R.id.ll_take_picture);
        LinearLayout llBrowseImage = dialog.findViewById(R.id.ll_browse_image);
        View.OnClickListener wrappedOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(viewClicked != null)
                    viewClicked.onClick(v);
            }
        };
        llTakePicture.setOnClickListener(wrappedOnClick);
        llBrowseImage.setOnClickListener(wrappedOnClick);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.horizontalMargin = 0;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void showChooseItemDialog(final Context context, final SimpleListAdapter simpleListAdapter){

        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_layout_search_item);
        final RecyclerView recyclerView = dialog.findViewById(R.id.rv_simple_select);
        ImageView ivSearchDialogBack = dialog.findViewById(R.id.ib_search_dialog_navigate_back);
        EditText etSearchFilter = dialog.findViewById(R.id.et_search_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(simpleListAdapter);
        simpleListAdapter.addListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, Object item, int pos) {
                dialog.dismiss();
            }
        });

        ivSearchDialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        etSearchFilter.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        simpleListAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                    }
                }
        );

        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.FILL;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.horizontalMargin = 0;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void showColorsDialog(final Context context, final View... views){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.layout_product_colors);
        LinearLayout llConfirmation = dialog.findViewById(R.id.ll_product_colors);
        dialog.findViewById(R.id.ib_color_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        GridLayout gl = dialog.findViewById(R.id.gc_colors);
        int cnt = gl.getChildCount();
        while (cnt-- > 0) {
            gl.getChildAt(cnt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ColorDrawable background = (ColorDrawable)v.getBackground();
                    int viewsCount = views.length;
                    while(viewsCount-- > 0){
                        views[viewsCount].setBackgroundColor(background.getColor());
                        if(views[viewsCount].getId() == R.id.ll_summary_in_image){
                            views[viewsCount].setBackgroundColor(manipulateColor(background.getColor(), 0.8F));
                        }
                    }
                    dialog.dismiss();
                }
            });
        }

        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        //loadPaymentTypes(context, llConfirmation, posOrder);
    }

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }

    private static void loadPaymentTypes (final Context context, View parent, final PosOrder posOrder) {

        if (!posOrder.getSession().getConfig().getPaymentJournals().isEmpty()) {
            List<AccountBankStatement> paymentStatements = posOrder.getAccountBankStatements();
            List<CharSequence> paymentNames = new ArrayList();
            for (AccountBankStatement paymentStatement : paymentStatements)paymentNames.add(paymentStatement.getJournal().getName());
            int index = 0;
            for (final AccountBankStatement statement : posOrder.getSession().getStatements()) {
                Button buttonPayment = null;
                if(index == 0) {
                    buttonPayment = parent.findViewById(R.id.btnPaymentType0);
                }
                if(index == 1) {
                    buttonPayment = parent.findViewById(R.id.btnPaymentType1);
                }
                if(index == 2) {
                    buttonPayment = parent.findViewById(R.id.btnPaymentType2);
                }
                if(index == 3) {
                    buttonPayment = parent.findViewById(R.id.btnPaymentType3);
                }
                if(index == 4) {
                    buttonPayment = parent.findViewById(R.id.btnPaymentType4);
                }
                if(index == 5) {
                    buttonPayment = parent.findViewById(R.id.btnPaymentType5);
                }
                buttonPayment.setText(statement.getJournal().getName().substring(0, 3));
                buttonPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle data = new Bundle();
                        data.putInt(Columns.PosOrderLine.order_id, posOrder.getId());
                        data.putInt(Columns.AccountBankStatementLine.statement_id, statement.getId());
                        data.putBoolean("PAYMENT_LINES", true);
                        IntentUtils.startActivity(context, PaymentLine.class, data);
                    }
                }); {

                }
                index++;
            }

        }
    }

    private void showStateDialog(final View v) {
        final  String[] array = new String[]{"Arizona", "California", "Florida", "Massachusetts", "New York", "Washington"};
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("State");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                ((EditText) v).setText(array[i]);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showCountryDialog(final View v) {
        final  String[] array = new String[]{"United State", "Germany", "United Kingdom", "Australia", "Nigeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Country");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                ((EditText) v).setText(array[i]);
                dialog.dismiss();
            }
        });

        builder.show();
    }

}
