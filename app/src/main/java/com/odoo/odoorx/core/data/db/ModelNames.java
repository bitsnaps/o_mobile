package com.odoo.odoorx.core.data.db;


import com.odoo.odoorx.rxshop.BuildConfig;

public class ModelNames {

    public static String USER = "res.users";
    public static String COMPANY = "res.company";
    public static String PARTNER = "res.partner";
    public static String PRODUCT_TEMPLATE = "product.template";
    public static String PRODUCT = "product.product";
    public static String PRODUCT_IMAGE = "product.image";
    public static String CATEGORY = "product.category";
    public static String ACCOUNT = "account.account";
    public static String ACCOUNT_BANK_STATEMENT = "account.bank.statement";
    public static String ACCOUNT_BANK_STATEMENT_LINE = "account.bank.statement.line";
    public static String STOCK_LOCATION = "stock.location";
    public static String POS_CONFIG = "pos.config";
    public static String POS_ORDER = "pos.order";
    public static String POS_ORDER_LINE = "pos.order.line";
    public static String POS_SESSION = "pos.session";
    public static String PRODUCT_PRICELIST = "product.pricelist";
    public static String PURCHASE_ORDER = "purchase.order";
    public static String PURCHASE_ORDER_LINE = "purchase.order.line";
    public static String STOCK_PICKING = "stock.picking";
    public static String STOCK_PICKING_TYPE = "stock.picking_type";
    public static String UOM = "uom.uom";


    public static String getAuthority(String modelName){
       return BuildConfig.APPLICATION_ID + ".base.provider.content.sync." + modelName.replace(".", "_");
    }

}
