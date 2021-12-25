package com.tut.badge;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

class Message {

    final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
    @SuppressWarnings("unused")
    private ContentResolver resolver;

    Message(ContentResolver ConResolver){
        resolver = ConResolver;
    }

    public String getMessage(int batas) {
        Cursor cur = resolver.query(SMS_INBOX, null, null, null,null);
        String sms = "Message >> \n";
        int hitung = 0;
        while (cur.moveToNext()) {
            sms += "From :" + cur.getString(2) + " : " + cur.getString(11)+"\n";
            if(hitung == batas)
                break;
            hitung++;
        }
        return sms;
    }

    public int getMessageCountUnread(){
        Cursor c = resolver.query(SMS_INBOX, null, "read = 0", null, null);
        int unreadMessagesCount = c.getCount();
        c.deactivate();
        return unreadMessagesCount;
    }

    public String getMessageAll(){
        Cursor cur = resolver.query(SMS_INBOX, null, null, null,null);
        String sms = "Message >> \n";
        while (cur.moveToNext()) {
            sms += "From :" + cur.getString(2) + " : " + cur.getString(11)+"\n";
        }
        return sms;
    }

    public String getMessageUnread() {
        Cursor cur = resolver.query(SMS_INBOX, null, null, null,null);
        String sms = "Message >> \n";
        int hitung = 0;
        while (cur.moveToNext()) {
            sms += "From :" + cur.getString(2) + " : " + cur.getString(11)+"\n";
            if(hitung == getMessageCountUnread())
                break;
            hitung++;
        }
        return sms;
    }
/*
    public void setMessageStatusRead() {
        ContentValues values = new ContentValues();
        values.put("read",true);
        resolver.update(SMS_INBOX,values, "_id="+SmsMessageId, null);
    }*/

}
