package com.tut.fragmentSMS

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tut.fragmentSMS.details_holder.DetailsholderContent
import com.tut.fragmentSMS.msg_holder.MsgholderContent


class MainActivity : AppCompatActivity() {
    var msg_list: MutableMap<String, MutableList<String>>? = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
            getMessages()
        }else{
            val perms = Array(1) {Manifest.permission.READ_SMS}
            requestPermissions(perms, 100)
        }

        msgsPane()
    }

    private fun msgsPane(){
        for (addr in msg_list?.keys!!){
//            println(addr+": "+msg_list!![addr])
            MsgholderContent.addItem(
                MsgholderContent.MsgholderItem(
                    addr,
                    msg_list!![addr]!![0]
                )
            )
            for (msg in msg_list!![addr]!!){
                DetailsholderContent.addItem(
                    DetailsholderContent.DetailholderItem(msg)
                )
            }
            DetailsholderContent.ITEMS.reverse()
        }
    }

    private fun getMessages(){
        println("Getting messages")
        val message = Uri.parse("content://sms/")
        val cr: ContentResolver = contentResolver

        val c = cr.query(message, null, null, null, null)
        startManagingCursor(c)
        val totalSMS = c!!.count

        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {
//                println("Date: "+c!!.getString(c!!.getColumnIndexOrThrow("date")))
                val address = c.getString(c.getColumnIndexOrThrow("address"))
                val msg_id = c.getString(c.getColumnIndexOrThrow("_id")).plus(": ")
                val msg_body = c.getString(c.getColumnIndexOrThrow("body")).trim()
//                println("subject: "+c.getString(c.getColumnIndex("subject")))

                if (msg_list?.containsKey(address) == true){
                    msg_list?.get(address)?.add(msg_body)
                }else {
                    msg_list?.put(address, mutableListOf(msg_body))
                }
//                println(msg_list)
                c.moveToNext()
            }
        }

        c.close()
    }
}