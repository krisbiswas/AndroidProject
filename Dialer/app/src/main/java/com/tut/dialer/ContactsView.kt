package com.tut.dialer

import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactsView : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var contactList: ArrayList<Contact>
    lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_view)

        contactList = ArrayList()
        contactAdapter = ContactAdapter(contactList)
        retrieveContacts()
//        contactList.add(Contact("Kris","1234567890"))
//
        recyclerView = findViewById(R.id.contactList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter

        findViewById<Button>(R.id.dialer).setOnClickListener{
            finish()
        }
    }

    private fun retrieveContacts() {
        val contentResolver = contentResolver
        val cursor =
            contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            do {
//                arrayList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
//                println(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)))

                val phoneNo = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                )
//                println("$name:-> $phoneNo")
                contactList.add(Contact(name, phoneNo))
            } while (cursor.moveToNext())
            contactAdapter.notifyDataSetChanged()
        }
        cursor.close()
    }
}