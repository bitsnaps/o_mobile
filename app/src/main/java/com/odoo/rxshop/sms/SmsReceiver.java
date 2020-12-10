package com.odoo.rxshop.sms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.text.Html;
import android.text.SpannableString;

import com.odoo.RxShop;
import com.odoo.odoorx.rxshop.R;
import com.odoo.rxshop.activity.pos.PosOrderEdit;
import com.odoo.odoorx.core.config.OConstants;
import com.odoo.odoorx.core.data.dao.PosOrderDao;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.dto.PosOrder;
import com.odoo.odoorx.core.data.dto.PosOrderLine;

/**
 * Needed to make default sms app for testing
 */

public class SmsReceiver extends BroadcastReceiver {

    private PosOrderDao posOrderDao;

    public SmsReceiver(){
        this.posOrderDao =  RxShop.getDao(PosOrderDao.class);
    }



        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] smsExtra = (Object[]) intent.getExtras().get("pdus");
            String body = "";
            String phone = "";

            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                body += sms.getMessageBody();
                phone = sms.getOriginatingAddress();
            }
            interpreteAndShow(context, posOrderDao, phone, body);

        }

        public static void interpreteAndShow(Context context, PosOrderDao posOrderDao, String phone, String SMSMessage){
            if(SMSMessage.startsWith("Rx:")){
                PosOrder  posOrder = posOrderDao.neeSMSOrder(phone, SMSMessage.substring(3), QueryFields.all());

                String notificationMessage  = "";
                notificationMessage += "From: " + posOrder.getName() + "<br>";
                int counter =  1;
                for(PosOrderLine posOrderLine: posOrder.getLines()){
                    notificationMessage += "(" + counter++ + ") " + posOrderLine.getProduct().getName() + " ";
                    notificationMessage += OConstants.CURRENCY_SYMBOL + posOrderLine.getUnitPrice() + "X";
                    notificationMessage += posOrderLine.getQuantity() + " ";
                    notificationMessage += "<br>";
                }
                notificationMessage =  notificationMessage + "<b> TOTAL: " + posOrder.getAmountTotal() + "</b>";
                SpannableString formattedBody = new SpannableString(
                Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? Html.fromHtml(notificationMessage)
                : Html.fromHtml(notificationMessage, Html.FROM_HTML_MODE_LEGACY));

                Intent intent = new Intent(context, PosOrderEdit.class);
                // use System.currentTimeMillis() to have a unique ID for the pending intent
                PendingIntent contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
                Notification notification = new Notification.Builder(context)
                        .setContentText(notificationMessage)
                        .setContentTitle(RxShop.APPLICATION_NAME + " : New Order")
                        .setSmallIcon(R.drawable.ic_shopping_cart_black_24dp)
                        .setContentIntent(contentIntent)
                        .setStyle(new Notification.BigTextStyle().bigText(formattedBody))
                        .build();
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(1, notification);

//                NotificationCompat.Builder builder = new NotificationCompat.Builder(
//                        context);


//                Notification notification2 = builder.setContentIntent(contentIntent)
//                        .setSmallIcon(R.drawable.ic_shopping_cart_black_24dp).setTicker(title).setWhen(0)
//                        .setAutoCancel(true).setContentTitle(title)
//                        .setStyle(new NotificationCompat.BigTextStyle().setSummaryText(notificationMessage))
//                        .setContentText(notificationMessage).build();
//
//                NotificationManager notificationManager =
//                        (NotificationManager) systemService;
//                notificationManager.notify(0, notification2);
            }
        }



}

