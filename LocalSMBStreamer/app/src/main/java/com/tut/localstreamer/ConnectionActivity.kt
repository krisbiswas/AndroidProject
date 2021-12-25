package com.tut.localstreamer

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.HandlerThread
import android.os.Looper
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.net.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.os.Handler as ThreadHandler


class ConnectionActivity : AppCompatActivity(), CustomListAdaptor.OnItemClickListener{

    private val REQUEST_BROWSING = 100
    private lateinit var listData: MutableList<String>
    private lateinit var listView: RecyclerView
    private lateinit var scanExecutor: ExecutorService
    private val recentConnections = HashMap<String, Cred>()

    data class Cred(val username: String, val password: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        // This Recycler view will be in fragments
        listData = ArrayList()
        listView = findViewById(R.id.view_deviceList)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = CustomListAdaptor(listData, this)

        findViewById<FloatingActionButton>(R.id.btn_scan_devices).setOnClickListener{
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            var ipSubnet = intToInetAddress(wifiManager.connectionInfo.ipAddress).toString()
            ipSubnet = ipSubnet.substring(1, ipSubnet.lastIndexOf('.')+1)
//            scanServers(ipSubnet)
            scanServers(ipSubnet)
        }
    }

    override fun onStart() {
        super.onStart()
        println("Explorer Active Threads: ${HandlerThread.activeCount()}")
    }

    private fun intToInetAddress(hostAddress: Int): InetAddress? {
        val addressBytes = byteArrayOf(
            (0xff and hostAddress).toByte(),
            (0xff and (hostAddress shr 8)).toByte(),
            (0xff and (hostAddress shr 16)).toByte(),
            (0xff and (hostAddress shr 24)).toByte()
        )
        return try {
            InetAddress.getByAddress(addressBytes)
        } catch (e: UnknownHostException) {
            throw AssertionError()
        }
    }

    fun isSambaOpen(ipAddress: String): Boolean{
        var isOpen = false
        for (port in arrayOf(139, 445)) {
            isOpen = try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ipAddress, port), 500)
//                socket.keepAlive
                socket.close()
                println("Server Connection Info: Host $ipAddress is running samba server on port $port")
                true
            } catch (ce: ConnectException) {
//                println("Server Info: Failed to connect with $ipAddress")
                false
            } catch (toe: SocketTimeoutException){
//                println("Server Info: Failed to connect with $ipAddress")
                false
            }
        }
        return isOpen
    }

    fun listContains(new_server: String): Boolean{
        for (server in listData){
            if(server.equals(new_server)){
                return true
            }
        }
        return false
    }

    private fun scanServers(subnet: String){
        val handler = ThreadHandler(Looper.getMainLooper())
        scanExecutor = Executors.newSingleThreadExecutor()
        scanExecutor.execute{
            for (idx in 2..100) {
                if(Thread.interrupted()){
                    break
                }
                if(isSambaOpen("$subnet$idx")){
                    if(!listContains("$subnet$idx")){
                        listData.add("$subnet$idx")
                        handler.post {
                            listView.adapter?.notifyDataSetChanged()
                        }
                    }
                }else{
                    if(listContains("$subnet$idx")){
                        listData.remove("$subnet$idx")
                        handler.post {
                            listView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(pos: Int) {
        scanExecutor.shutdownNow()
        val server = listData.elementAt(pos)
        if(recentConnections.containsKey(server)){
            recentConnections[server]?.let {
                Toast.makeText(this,"Logging in with username ${it.username}", Toast.LENGTH_LONG).show()
                startExplorer(server, it.username, it.password)
            }
        }else {
//            Uncomment getUserCredentials and remove startExplorer for normal usage
            startExplorer(server,"krisb","Kb0@gmail.com")
//            getUserCredentials(server).show()
        }
    }

    private fun getUserCredentials(server: String): AlertDialog {
        val dialogView = layoutInflater.inflate(R.layout.layout_user_credential_input, null)
        return AlertDialog.Builder(this)
            .setTitle("User Login")
            .setMessage("Please Enter your servers login credentials.")
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("ENTER",
                DialogInterface.OnClickListener { dialog, id ->
                    val username = dialogView.findViewById<EditText>(R.id.et_username).text.toString()
                    val password = dialogView.findViewById<EditText>(R.id.et_password).text.toString()
                    if(username.isNotEmpty()) {
                        startExplorer(server, username, password)
                        recentConnections[server] = Cred(username, password)
                    }else{
                        startExplorer(server, "Guest", password)
                    }
                })
            .setNegativeButton("CANCEL",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
            .create()
    }

    private fun startExplorer(server: String, username: String, password: String){
        val surf = Intent(applicationContext, ServerExplorer2Activity::class.java)
        surf.putExtra("username", username)
        surf.putExtra("password", password)
        surf.putExtra("host", server)
        startActivityForResult(surf, REQUEST_BROWSING)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_CANCELED){
            if (data != null) {
                println("Could not connect to "+data.getStringExtra("host"))
                recentConnections.remove(data.getStringExtra("host"))
            }
        }else if(resultCode == Activity.RESULT_OK){
            println("Connection Successful")
        }
    }

    /*
        private fun scanServers(subnet: String){
            val urlProtocol = "smb://"
            val handler = ThreadHandler(Looper.getMainLooper())
            scanExecutor = Executors.newSingleThreadExecutor()
            scanExecutor.submit {
                for (idx in 2..100) {
                    if(Thread.interrupted()){
                        break
                    }
                    val server = SmbFile("$urlProtocol$subnet$idx")
                    if(isSambaOpen("$subnet$idx")){
                        if(!listContains(server)){
                            listData.add(server)
                            handler.post {
                                listView.adapter?.notifyDataSetChanged()
                            }
                        }
                    }else{
                        if(listContains(server)){
                            listData.remove(server)
                            handler.post {
                                listView.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    */

}
