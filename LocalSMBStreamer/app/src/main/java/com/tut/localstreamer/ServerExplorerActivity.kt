package com.tut.localstreamer
/*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbAuthException
import jcifs.smb.SmbException
import jcifs.smb.SmbFile
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashSet

class ServerExplorerActivity : AppCompatActivity(), CustomListAdaptor.OnItemClickListener {

    private lateinit var host: String
    private lateinit var auth: NtlmPasswordAuthentication

    val dirTree = Stack<MutableSet<SmbFile>>()
    lateinit var listData: MutableSet<SmbFile>
    lateinit var listView: RecyclerView
    var executor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_explorer)

        // Show Loading Screen until connected with authorisation details

        listData = HashSet()
        listView = findViewById(R.id.view_deviceList)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = CustomListAdaptor(listData, this)
    }

    override fun onStart() {
        super.onStart()
        host = intent.getStringExtra("host")!!
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password").orEmpty()
        auth = NtlmPasswordAuthentication(null, username, password)
//        println("username: $username\npassword$password\nhost_ip$host")
        connectServer(SmbFile("smb://$host/", auth))
    }

    private fun connectServer(smb_req: SmbFile){
        executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            auth.name
            var domains: Array<SmbFile>?
            try {
                if(smb_req.isDirectory){
                    domains = smb_req.listFiles()
                    if (domains != null) {
                        listData.addAll(domains)
                        handler.post {
                            listView.adapter?.notifyDataSetChanged()
                        }
                    }
                }else if(smb_req.isFile){
                    println("SMB Info: ${smb_req.name} is a file")
                }

            } catch(authExcep: SmbAuthException){
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG)
                    .show()
            }catch (e1: SmbException) {
                println("SmbException Caught")
                e1.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        listData.clear()
        if(!dirTree.empty()){
            listData.addAll(dirTree.pop())
            listView.adapter?.notifyDataSetChanged()
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onItemClick(pos: Int) {
        executor.shutdownNow()
        val clickedDir = listData.elementAt(pos)
        if(clickedDir.isDirectory){
            dirTree.push(HashSet(listData))
            listData.clear()
        }
        connectServer(clickedDir)
    }

//    fun playStream(){
//        executor = Executors.newSingleThreadExecutor()
//        executor.execute{
//            val ssocket = ServerSocket(80)
//            println("stream Address: "+ssocket.localSocketAddress)
////            ssocket.bind()
//        }
//    }
}*/