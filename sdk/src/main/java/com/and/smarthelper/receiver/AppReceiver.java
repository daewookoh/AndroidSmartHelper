package com.and.smarthelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bestmafen.smablelib.component.SmaManager;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class AppReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing
    private static String stateStr;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("TTT", intent.toString());


       if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
           Log.d("TTT", "SMS_RECEIVED");
            if (SmaManager.getInstance().getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION)) {

                Bundle data  = intent.getExtras();
                Object[] pdus = (Object[]) data.get("pdus");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
                String sender = smsMessage.getDisplayOriginatingAddress();
                String phoneNumber = smsMessage.getDisplayOriginatingAddress();
                String senderNum = phoneNumber;
                String messageBody = smsMessage.getMessageBody();

                String number = getContactName(senderNum, context);
                SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, number, messageBody);

            }
        } else{

            stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

           Log.d("TTT", stateStr);

            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }


    public void checkRepeat(final String num, Context context){

        //SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences pref = context.getSharedPreferences("shared_pref", MODE_PRIVATE);
        String phone_repeat = pref.getString("phone_repeat","0");

        int repeat_cnt = Integer.valueOf(phone_repeat)/3;

        Log.d("TTT", phone_repeat);

        for(int i=1; i<repeat_cnt; i++) {
            int second = 6000*i;

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if (SmaManager.getInstance().getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL) && num != null) {
                                if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                                    SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, num, "Incoming Call");
                                }
                            }
                        }
                    },
                    second);

        }

        int second2 = 6000*repeat_cnt;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (SmaManager.getInstance().getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL) && num != null) {
                            if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                                //SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.CALL_IDLE);
                            }
                        }
                    }
                },
                second2);
    }

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {

        Log.d("TTT", String.valueOf(state));
        number = getContactName(number, context);

        //if(lastState == state || number==null || number.isEmpty()){
        if(number==null || number.isEmpty()){
            //No change, debounce extras
            //return;
        }

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d("TTT", "CALL_STATE_RINGING");
                SmaManager.getInstance().isCalling=true;
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;

                if (SmaManager.getInstance().getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL) && number!=null ) {

                    SmaManager.getInstance().write(SmaManager.Cmd.SET, SmaManager.Key.INTO_TAKE_PHOTO, false);
                    SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, number, "Incoming Call");
                    checkRepeat(number, context);

                }
                //onIncomingCallReceived(context, number, callStartTime);
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                SmaManager.getInstance().isCalling=false;
                Log.d("TTT", "CALL_STATE_OFFHOOK");
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    //onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                else
                {
                    isIncoming = true;
                    callStartTime = new Date();
                    SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.CALL_OFF_HOOK);
                    //onIncomingCallAnswered(context, savedNumber, callStartTime);
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                SmaManager.getInstance().isCalling=false;
                Log.d("TTT", "CALL_STATE_IDLE");
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    //onMissedCall(context, savedNumber, callStartTime);
                }
                else if(isIncoming){
                    //onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.CALL_IDLE);
                }
                else{
                    //onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }

        Log.d("TTT", String.valueOf(isIncoming));
        lastState = state;
    }

    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri= Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        Log.d("TTT", "getContactName" + phoneNumber + contactName);

        if(contactName.isEmpty() || contactName.length()<1) {
            return phoneNumber;
        }
        else {
            return contactName;
        }
    }
}