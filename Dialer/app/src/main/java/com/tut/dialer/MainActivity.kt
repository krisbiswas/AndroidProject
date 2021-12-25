package com.tut.dialer

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    var inputNum: String = ""
    lateinit var etNumInput: EditText
    val permissions = arrayOf(android.Manifest.permission.CALL_PHONE,
        android.Manifest.permission.READ_CALL_LOG,
        android.Manifest.permission.WRITE_CALL_LOG,
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.WRITE_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if ((checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) ||
            (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||
            (checkSelfPermission(android.Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||
            (checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) ||
            (checkSelfPermission(android.Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(permissions, 100)
        }

        etNumInput = findViewById(R.id.etNumInput)
        dialButtonListeners()
        findViewById<ImageButton>(R.id.del_num).setOnClickListener {
            if(!etNumInput.text.toString().isEmpty()) {
                inputNum = inputNum.substring(0,inputNum.length-1)
                etNumInput.setText(inputNum)
            }
        }
        findViewById<Button>(R.id.contacts).setOnClickListener {
            if((checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) or
                (checkSelfPermission(android.Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {
                val openContactsView = Intent(this, ContactsView::class.java)
                startActivity(openContactsView)
            }else{
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.WRITE_CONTACTS), 100)
            }
        }

		findViewById<ImageButton>(R.id.btn_call).setOnClickListener {
			if(inputNum.isNotEmpty()){
                if(checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    callPhone(inputNum)
                }else{
                    requestPermissions(arrayOf(android.Manifest.permission.CALL_PHONE), 100)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if (grantResults.isNotEmpty()) {
                for (grant in grantResults){
                    if(grant == PackageManager.PERMISSION_DENIED){
                        when(permissions[grantResults.indexOf(grant)]){
                            android.Manifest.permission.CALL_PHONE -> Toast.makeText(this,
                                "Permission are required to make calls\nPlz allow in settings", Toast.LENGTH_LONG).show()
                            android.Manifest.permission.WRITE_CONTACTS -> Toast.makeText(this,
                                "Permission are required to manage contacts\n" +
                                        "Plz allow in settings", Toast.LENGTH_LONG).show()
                            android.Manifest.permission.READ_CONTACTS -> Toast.makeText(this,
                                "Permission are required to read contacts\n" +
                                        "Plz allow in settings", Toast.LENGTH_LONG).show()
                            else->{
//                        Toast.makeText(this,
//                            "Permission are required to make calls and manage contacts\n" +
//                                    "Plz allow in settings", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addInput(btn: String){
        inputNum += btn
        etNumInput.setText(inputNum)
    }

	private fun callPhone(phoneNum: String) {
		val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNum"))
		startActivity(intent)
	}

    private fun dialButtonListeners(){
        findViewById<ImageButton>(R.id.num0).setOnClickListener{
            addInput("0")
        }
        findViewById<ImageButton>(R.id.num1).setOnClickListener{
            addInput("1")
        }
        findViewById<ImageButton>(R.id.num2).setOnClickListener{
            addInput("2")
        }
        findViewById<ImageButton>(R.id.num3).setOnClickListener{
            addInput("3")
        }
        findViewById<ImageButton>(R.id.num4).setOnClickListener{
            addInput("4")
        }
        findViewById<ImageButton>(R.id.num5).setOnClickListener{
            addInput("5")
        }
        findViewById<ImageButton>(R.id.num6).setOnClickListener{
            addInput("6")
        }
        findViewById<ImageButton>(R.id.num7).setOnClickListener{
            addInput("7")
        }
        findViewById<ImageButton>(R.id.num8).setOnClickListener{
            addInput("8")
        }
        findViewById<ImageButton>(R.id.num9).setOnClickListener{
            addInput("9")
        }
    }
}