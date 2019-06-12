package com.odoo.base.addons.abirex.purchase;

import com.odoo.core.orm.ODataRow;

import java.util.Date;

public class PurchaseOrder {
    public PurchaseOrder(){

    }
    public PurchaseOrder(String name, String origin, String venderRef, Date orderDate, Date approvalDate, Integer vendorId, String currencyId, String state, Integer companyId, Integer userId, Float amountUntaxed, Float amountTax, Float amountTotal) {
        this.name = name;
        this.origin = origin;
        this.venderRef = venderRef;
        this.orderDate = orderDate;
        this.approvalDate = approvalDate;
        this.vendorId = vendorId;
        this.currencyId = currencyId;
        this.state = state;
        this.companyId = companyId;
        this.userId = userId;
        this.amountUntaxed = amountUntaxed;
        this.amountTax = amountTax;
        this.amountTotal = amountTotal;
    }

    String name; // = newString("Name", OVarchar.class).setSize(100).setRequired();
    String origin; // = newString("Origin", OVarchar.class);
    String venderRef; // = newString("Vendor Reference", OVarchar.class);
    Date orderDate; // = newString("Order Date", ODateTime.class);
    Date approvalDate; // = newString("Date Approved", ODateTime.class);
    Integer vendorId; // = newString("Partner Id", ResPartner.class, RelationType.ManyToOne);
    String currencyId; // = newString("Currency", ResCurrency.class, RelationType.ManyToOne);
    String state; // = newString("Vendor Reference", OSelection.class)

    Integer companyId; // = newString("Company", ResCompany.class, RelationType.ManyToOne);
    Integer userId; // = newString(null, ResUsers.class, RelationType.ManyToOne);

    Float amountUntaxed; // = newString("Untaxed Amount", OFloat.class);
    Float amountTax; // = newString("Taxes", OFloat.class);
    Float amountTotal; // = newString("Total Amount", OFloat.class);


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getVenderRef() {
        return venderRef;
    }

    public void setVenderRef(String venderRef) {
        this.venderRef = venderRef;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getAmountUntaxed() {
        return amountUntaxed;
    }

    public void setAmountUntaxed(Float amountUntaxed) {
        this.amountUntaxed = amountUntaxed;
    }

    public Float getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(Float amountTax) {
        this.amountTax = amountTax;
    }

    public Float getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(Float amountTotal) {
        this.amountTotal = amountTotal;
    }

    public PurchaseOrder fromRow(ODataRow row){
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        return purchaseOrder;
    }

}
