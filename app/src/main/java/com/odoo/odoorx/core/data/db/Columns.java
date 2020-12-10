package com.odoo.odoorx.core.data.db;

public class Columns {

    public static String id = "_id";
    public static String server_id = "id";
    public static String code = "code";
    public static String name = "name";
    public static String description = "description";
    public static String active = "active";
    public static String FALSE = "false";

    public static class AccountJournal extends Columns{
        public static String reference = "reference";
        public static String default_credit_account_id = "default_credit_account_id";
        public static String default_debit_account_id = "default_debit_account_id";
    }

//TODO: Move common foreign keys to columnns
    public static class AccountBankStatementCol extends Columns{
        public static String pos_session_id = "pos_session_id";
        public static String account_id = "account_id";
        public static String reference = "reference";
        public static String date = "date";
        public static String date_done = "date_done";
        public static String balance_start = "balance_start";
        public static String balance_end_real = "balance_end_real";
        public static String accounting_date = "accounting_date";
        public static String state = "state";
        public static String currency_id = "currency_id";
        public static String journal_id = "journal_id";
        public static String journal_type = "journal_type";
        public static String company_id = "company_id";
        public static String total_entry_encoding = "total_entry_encoding";
        public static String balance_end = "balance_end";
        public static String difference = "difference";
        public static String move_line_count = "move_line_count";
        public static String all_lines_reconciled = "all_lines_reconciled";
        public static String user_id = "user_id";
        public static String cashbox_start_id = "cashbox_start_id";
        public static String cashbox_end_id = "cashbox_end_id";
        public static String is_difference_zero = "is_difference_zero";

        public enum State {

            opened("open"),
            closed("confirm");

            private static String OPENED = opened.value;
            private static String CLOSED = closed.value;

            private String value;
            State(String state){
                this.value = state;
            }

            public String toString(){
                return state;
            }

            public static State valueOfString(String  value){
                if(value.equals(OPENED)) return opened;
                if(value.equals(CLOSED)) return closed;
                return null;
            }
            public String  getValue(){
                return value;
            }
        }
    }

    public static class AccountBankStatementLine extends Columns{

        public static String date = "date";
        public static String amount = "amount";
        public static String journal_currency_id = "journal_currency_id";
        public static String partner_id = "partner_id";
        public static String account_number = "account_number";
        public static String bank_account_id = "bank_account_id";
        public static String account_id = "account_id";
        public static String statement_id = "statement_id";
        public static String journal_id = "journal_id";
        public static String partner_name = "partner_name";
        public static String ref = "ref";
        public static String note = "note";
        public static String sequence = "sequence";
        public static String company_id = "company_id";
        public static String journal_entry_ids = "journal_entry_ids";
        public static String amount_currency = "amount_currency";
        public static String currency_id = "currency_id";
        public static String journal_entry_name = "journal_entry_name";
        public static String move_name = "move_name";
        public static String pos_statement_id = "pos_statement_id";

        public static enum State {
            opened("open"),
            closed("confirm");
            private String state;
            State(String state){
                this.state = state;
            }

            public String toString(){
                return state;
            }
        }
    }

    public static class ProductTemplateCol extends Columns{
        public static String product_type = "product_type";
        public static String uom_id = "uom_id";
        public static String category_id = "categ_id";
        public static String is_medicine = "is_medicine";
        public static String product_image_ids = "product_image_ids";
    }

    public static class ProductImageCol extends Columns {
        public static String product_tmpl_id = "product_tmpl_id";
        public static String image = "image";
    }

        public static class ProductCol extends Columns{
        public static String medicine_id = "medicine_id";
        public static String product_tmpl_id = "product_tmpl_id";
        public static String uom_id = "uom_id";
        public static String active = "active";
        public static String image = "image";
        public static String image_small = "image_small";
        public static String image_medium = "image_medium";
        public static String cost_price = "cost_price";
        public static String lst_price = "lst_price";
        public static String qty_available = "qty_available";
        public static String default_code = "default_code";
        public static String code = "code";
    }

    public static class Currency extends Columns{
        public static String symbol = "symbol";
        public static String rate = "rate";
    }

    public static class Company extends Columns{
        public static String currency_id = "currency_id";
        public static String partner_id = "partner_id";
    }

    public static class Partner extends Columns{
        public static String company_id = "company_id";
        public static String partner_id = "partner_id";
        public static String customer = "customer";
        public static String employee = "employee";
        public static String supplier = "supplier";
        public static String display_name = "display_name";
        public static String email = "email";
        public static String phone = "phone";
        public static String address = "address";
        public static String state_id = "state_id";
        public static String country_id = "country_id";
        public static String locality = "locality";
    }


    public static class PosConfig extends Columns{
        public static String journal_id = "journal_id";
        public static String company_id = "company_id";
        public static String journal_ids = "journal_ids";
        public static String price_list_id = "pricelist_id";
    }


    public static class PriceList extends Columns{
        public static String currency_id = "currency_id";
    }


    public static class PosOrder extends Columns {
        public static String partner_id = "partner_id";
        public static String session_id = "session_id";
        public static String user_id = "user_id";
        public static String company_id = "company_id";
        public static String currency_rate = "currency_rate";
        public static String sequence_no = "sequence_number";
        public static String amount_tax = "amount_tax";
        public static String amount_paid = "amount_paid";
        public static String amount_return = "amount_return";
        public static String order_date = "date_order";
        public static String amount_total = "amount_total";
        public static String pricelist_id = "pricelist_id";
        public static String state = "state";

        public static class State {
            public static String DRAFT = "draft";
            public static  String CANCEL = "cancel";
            public static  String PAID = "paid";
            public static  String DONE = "done";
            public static  String INVOICED = "invoiced";
        }
    }


    public static class AccountInvoice extends Columns {
        public static String origin = "origin";
        public static String partner_id = "partner_id";
        public static String user_id = "user_id";
        public static String company_id = "company_id";
        public static String amount_tax = "amount_tax";
        public static String amount_paid = "amount_paid";
        public static String amount_return = "amount_return";
        public static String order_date = "date_order";
        public static String due_date = "due_date";
        public static String invoice_date = "date_invoice";
        public static String amount_total = "amount_total";
        public static String state = "state";
        public static final String type = "type";
        public static final String reference = "reference";
        public static final String sent = "sent";
        public static final String number = "number";
        public static final String account_id = "account_id";
        public static final String currency_id = "currency_id";
        public static final String price_list_id = "price_list_id";
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
        public static String sequence_no = "sequence_number";
        public static String login_no = "login_number";
        public static String rescue = "rescue";


        public static String[] asArray(){
            return new String[] {id, name, user_id, config_id, start_at, stop_at, state, sequence_no, login_no, rescue};
        }

        public static enum State {

            opened("opened"),
            opening_control("opening_control"),
            closing_control("closing_control"),
            closed("closed");

            private String state;
            State(String state){
                this.state = state;
            }

            public String toString(){
                return state;
            }
        }

    }

    public static class SyncModel extends Columns {
        public static String model_name = "model_name";
        public static String log_name = "log_name";
        public static String authority = "authority";
        public static String sync_mode = "sync_mode";
        public static String status = "status";
        public static String status_detail = "status_detail";
        public static String local_count = "local_count";
        public static String push_to_server_ids = "push_to_server_ids";
        public static String pull_to_device_ids = "pull_to_device_ids";
        public static String server_count = "server_count";
        public static String percentage_synced = "percentage_synced";
        public static String last_synced = "last_synced";
        public static String process_updated = "process_updated";
        public static String must_sync_relations = "must_sync_relations";
        public static String sync_limit = "sync_limit";
        public static String sync_depth = "sync_depth";
        public static String parent_id = "parent_id";

        public enum Mode{

            QUICK_SYNC,
            QUICK_CREATE,
            SYSTEM_TRIGGERED,
            PARENT_TRIGGERED,
            REFRESH_TRIGGERED;
        }
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
