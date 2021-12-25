package com.tut.localstreamer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.mssmb2.SMBApiException
import com.hierynomus.protocol.commons.EnumWithValue
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.DiskShare
import com.hierynomus.smbj.share.File
import com.rapid7.client.dcerpc.mssrvs.ServerService
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo0
import com.rapid7.client.dcerpc.transport.SMBTransportFactories
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashSet

class ServerExplorer2Activity : AppCompatActivity(), CustomListAdaptor.OnItemClickListener {

    private val REQUEST_CODE_PLAY = 100
    private val RESULT_CODE_PLAY_SUCCESS = 101

    private lateinit var host: String
    private lateinit var auth: AuthenticationContext

    private val client = SMBClient(
//        SmbConfig.builder()
//        .withTimeout(180,TimeUnit.SECONDS)
//        .withSoTimeout(180,TimeUnit.SECONDS)
//        .build()
    )
    private lateinit var connection: Connection
    private lateinit var session: Session
    private var currentPath = ""
    private var openShare = ""
    private val dirTree = Stack<MutableList<String>>()
    private lateinit var listSubDir: MutableList<String>
    private lateinit var listView: RecyclerView
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var fileLoader: FileLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_explorer2)
//        Load Saved data/informations about shared files/directory in the bundle

        host = intent.getStringExtra("host")!!
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password").orEmpty()
        auth = AuthenticationContext(username, password.toCharArray(), "")//(null, username, password)

        listSubDir = ArrayList()
        listView = findViewById(R.id.view_deviceList)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = CustomListAdaptor(listSubDir, this)

        connectServer(host)
    }

    override fun onStart() {
        super.onStart()
        println("Explorer Server Active Threads: ${HandlerThread.activeCount()}")
    }

    override fun onDestroy() {
        closeConnection()
        super.onDestroy()
    }

    private fun connectServer(host: String){
        executor = Executors.newSingleThreadExecutor()
        val handler = Handler(mainLooper)
        executor.execute {
            try{
                connection = client.connect(host)
                session = connection.authenticate(auth)
                if(connection.isConnected){
                    val transport = SMBTransportFactories.SRVSVC.getTransport(session)
                    val serverService = ServerService(transport)
                    val smbShares: List<NetShareInfo0> = serverService.shares0
                    val shareDirs = HashSet<String>()
                    for (share in smbShares) {
                        shareDirs.add(share.netName)
                    }
                    listSubDir.addAll(shareDirs.sorted())
                    handler.post {
                        listView.adapter?.notifyDataSetChanged()
                    }
                }else{
                    println("Can't connect to host $host")
                }
            }catch (e: SMBApiException){
                println("Authentication Failed! Incorrect User Credentials.")
//                Toast.makeText(applicationContext, "Authentication Failed! Incorrect User Credentials.", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_CANCELED, Intent().putExtra("host", host))
                finish()
            }
        }
    }

    private fun openSharePointDir(host: String, sharePoint:String) {
        val handler = Handler(Looper.getMainLooper())
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
//            client.connect(host).use { connection ->
                try{
                    val share = session.connectShare(sharePoint)
                    if(share is DiskShare) {
                        openShare = sharePoint
//                        currentPath=sharePoint+"\\"
                        listDir(share,"")
                        handler.post{
                            listView.adapter?.notifyDataSetChanged()
                        }
                    }
                }catch (access_denied: SMBApiException){
                    println("SMB Access Denied")
                }
//            }
        }
    }

    private fun listDir(share: DiskShare, path: String): Boolean{
        val d: List<FileIdBothDirectoryInformation>?
        try{
            d = share.list(path)
        }catch (not_dir: SMBApiException){
            println("SmbApiException :: "+
                    path.substringBeforeLast("\\")+//.substringAfterLast("\\")+
                    " is not a directory")
            return false
        }
//        val newSubDir = HashSet<String>()
        for (f in d!!) {
            if (!(".".equals(f.fileName) || "..".equals(f.fileName))) {
                if (EnumWithValue.EnumUtils.isSet(f.fileAttributes, FileAttributes.FILE_ATTRIBUTE_DIRECTORY)) {
                    println("DIR: ${f.fileName}")
                }
                else{
                    println("FILE: ${f.fileName}")
                }
//            newSubDir.add(f.fileName)
                listSubDir.add(f.fileName)
            }
        }
        listSubDir.sort()
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listDir(selectedOption: String) {
        val handler = Handler(Looper.getMainLooper())
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
//            client.connect(host).use { connection ->
                try {
                    val share = session.connectShare(openShare)
                    if(share is DiskShare){
                        currentPath+=selectedOption+"\\"
                        if (listDir(share, currentPath)){
                            handler.post{
                                listView.adapter?.notifyDataSetChanged()
                            }
                        }else{
                            // open file using streams
                            playStream(share, currentPath.removeSuffix("\\"))
                            handler.post{
                                if(!dirTree.empty()){
                                    listSubDir.clear()
                                    listSubDir.addAll(dirTree.pop())
                                    println("BackTrack: $dirTree")
                                    currentPath = if(dirTree.size > 1){
                                        currentPath.substringBeforeLast("\\").substringBeforeLast("\\")+"\\"
                                    }else{
                                        ""
                                    }
                                    listView.adapter?.notifyDataSetChanged()
                                }
//                                onBackPressed()
                            }
                        }
                    }
                }catch (access_denied: SMBApiException){
                    println(access_denied.printStackTrace())
                }
//            }
        }
    }

    override fun onItemClick(pos: Int) {
        executor.shutdownNow()
        val clickedOption = listSubDir.elementAt(pos)
        println("Clicked Option: $clickedOption")
//        if(clickedDir.isDirectory){
        dirTree.push(ArrayList(listSubDir))
        listSubDir.clear()
//        }
        println("BackTrack: $dirTree")
        if(dirTree.size > 1){
            listDir(clickedOption)
        }else {
            openSharePointDir(host, clickedOption)
        }
    }

    override fun onBackPressed() {
        listSubDir.clear()
        if(!dirTree.empty()){
            listSubDir.addAll(dirTree.pop())
            println("BackTrack: $dirTree")
            currentPath = if(dirTree.size > 1){
                currentPath.substringBeforeLast("\\").substringBeforeLast("\\")+"\\"
            }else{
                ""
            }
            listView.adapter?.notifyDataSetChanged()
        }
        else{
            setResult(Activity.RESULT_OK)
            super.onBackPressed()
        }
    }

    private fun playStream(share: DiskShare, filePath: String){
        if(!share.fileExists(filePath)) return
        val file: File = share.openFile(filePath,
            EnumSet.of(AccessMask.FILE_READ_DATA),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )
        fileLoader = FileLoader(file)
        // wait until vid_path is set with the path of newly created
        // temp file for buffering contents
        val handlerThread = Handler(Looper.myLooper() ?: mainLooper)
        Thread{
            var vid_path = ""
            do{
                vid_path = fileLoader!!.getVideoPath()
                println("vid_path->$vid_path")
            }while(vid_path.isEmpty())
            handlerThread.post{
                val intentPlay = Intent(this, Player::class.java)
                intentPlay.putExtra("videoPath", vid_path)
                startActivityForResult(intentPlay, REQUEST_CODE_PLAY)
            }
        }.start()
        println("ShareFileOpen::File Open Success")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_PLAY){
            if(resultCode == RESULT_CODE_PLAY_SUCCESS){
                println("Streaming from Local Network successful")
                fileLoader?.close()
            }else{
                println("Error Occurred while Playing video")
                fileLoader?.close()
            }
        }
    }

    fun closeConnection(){
        Thread{
            fileLoader?.close()
        }
//        session.close()
//        connection.close()
        client.close()
        executor.shutdownNow()
    }
}