package com.odoo.data.abirex;

public class Columns {

    public static String id = "_id";
    public static String server_id = "id";
    public static String code = "code";
    public static String name = "name";
    public static String description = "description";
    public static String active = "active";


    public static class ProductTemplateCol extends Columns{
        public static String productType = "product_type";
    }

    public static class Currency extends Columns{
        public static String symbol = "symbol";
        public static String rate = "rate";
    }

    public static class Partner extends Columns{
        public static String partner_id = "partner_id";
        public static String customer = "customer";
        public static String employee = "employee";
        public static String supplier = "supplier";
        public static String display_name = "display_name";
    }

    public static class PosOrder extends Columns {
        public static String partner_id = "partner_id";
        public static String session_id = "session_id";
        public static String user_id = "user_id";
        public static String company_id = "company_id";
        public static String currency_rate = "currency_rate";
        public static String sequence_no = "sequence_no";
        public static String amount_tax = "amount_tax";
        public static String amount_paid = "amount_paid";
        public static String amount_return = "amount_return";
        public static String order_date = "date_order";
        public static String amount_total = "amount_total";
        public static String price_list_id = "pricelist_id";
        public static String state = "state";
    }

    public static class PosOrderLine extends Columns {
        public static String notice = "notice";
        public static String company_id = "company_id";
        public static String product_id = "product_id";
        public static String price_unit = "price_unit";
        public static String qty = "qty";
        public static String price_subtotal =  "price_subtotal";
        public static String discount =  "discount";
        public static String price_subtotal_incl =  "price_subtotal_incl";
        public static String order_id = "order_id";
    }

    public static class PosSession extends Columns {
        public static String user_id = "user_id";
        public static String config_id = "config_id";
        public static String start_at = "start_at";
        public static String stop_at = "stop_at";
        public static String state = "state";
        public static String sequence_no = "sequence_no";
        public static String login_no = "login_no";
        public static String rescue = "rescue";

        public static String[] asArray(){
            return new String[] {id, name, user_id, config_id, start_at, stop_at, state, sequence_no, login_no, rescue};
        }

    }

    public static class SyncModel extends Columns {
        public static String model = "model";
        public static String authority = "authority";
        public static String status = "status";
        public static String status_detail = "status_detail";
        public static String count = "count";
        public static String server_count = "server_count";
        public static String last_synced = "last_synced";
        public static String must_sync_relations = "must_sync_relations";
        public static String sync_limit = "sync_limit";
    }

    public static class SyncProcess extends Columns {
        public static String sync_model = "sync_model";
        public static String parent = "parent";
        public static String children = "children";
        public static String response = "response";
        public static String server_count = "server_count";
        public static String saved_count = "saved_count";
        public static String status = "status";
        public static String status_detail = "status_detail";
        public static String last_updated = "last_synced";
        public static String last_synced = "last_synced";
    }
}
