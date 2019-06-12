package com.odoo.base.addons.abirex.purchase;


import com.odoo.data.LazyList;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PurchaseOrderDate {
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat dayFormat;
    private Date purchaseDate;
    private int noOfVendors;
    private float totalAmount;
    private int noOfPurchases;

    private List<PurchaseOrder> purchaseOrders;

    public PurchaseOrderDate() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public PurchaseOrderDate(Date purchaseDate, int noOfVendors, float totalAmount, int noOfPurchases) {
        this.purchaseDate = purchaseDate;
        this.noOfVendors = noOfVendors;
        this.totalAmount = totalAmount;
        this.noOfPurchases = noOfPurchases;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public List<PurchaseOrder> getPurchaseOrderDaos() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(LazyList<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) throws ParseException {
        this.purchaseDate = simpleDateFormat.parse(purchaseDate);
    }

    public void setNoOfVendors(int noOfVendors) {
        this.noOfVendors = noOfVendors;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setNoOfPurchases(int noOfPurchases) {
        this.noOfPurchases = noOfPurchases;
    }

    public int getNoOfVendors(){
        return noOfVendors;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public int getNoOfPurchases() {
        return noOfPurchases;
    }


    public String getTotalAmountString() {
        DecimalFormat formatter;
        float totalAmount = getTotalAmount();
        if (totalAmount<=99999)
            formatter = new DecimalFormat("###,###,##0.00");
        else
            formatter = new DecimalFormat("#,##,##,###.00");

        return "₦ "+formatter.format(totalAmount);
    }
}
