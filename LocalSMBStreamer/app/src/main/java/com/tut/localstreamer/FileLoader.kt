package com.tut.localstreamer

import com.hierynomus.smbj.share.File
import java.io.*
import java.io.File.*
import java.lang.Exception
import java.util.concurrent.Executors


//class FileLoader(val host: String, val auth: AuthenticationContext, val sharedPath: String, val currentPath: String) {
class FileLoader(file: File?) {

    private val fileSize = file?.fileInformation?.standardInformation?.endOfFile
    var tempFilePath: String = ""
    private var temp: java.io.File? = null

//    val client = SMBClient()
    val copyExecutor = Executors.newSingleThreadExecutor()

    init {
//        val file = openSharedFile()
        copyExecutor.execute{
            try{
                if (file != null) {
                    tempFilePath = getDataSource(file.inputStream)
                }
            }catch (ioe: IOException){}
            println("Executor signing Off")
        }
    }

/*    private fun openSharedFile(): File? {
        try {
            val connection = client.connect(host)
            try{
                val session = connection.authenticate(auth)
                val share = session.connectShare(sharedPath)
                if(share is DiskShare) {
                    println("FILE Exists::==="+share.fileExists(currentPath))
                    return share.openFile(currentPath,
                        EnumSet.of(AccessMask.FILE_READ_DATA),
                        null,
                        SMB2ShareAccess.ALL,
                        SMB2CreateDisposition.FILE_OPEN,
                        null
                    )
                }
            }catch (access_denied: SMBApiException){
                println("SMB Access Denied")
            }
        }catch (ioe: IOException){
            println("Host $host not reachable!!")
        }
        return null
    }*/

    @Throws(IOException::class)
    private fun getDataSource(iStream: InputStream, offset: Int = 0): String {

//        if(!cn.contentType.contains("video")) return ""
        temp = createTempFile("tempmedia", ".dat")
        temp!!.deleteOnExit()
        val tempPath = temp!!.absolutePath

        val stream = iStream
        val out = FileOutputStream(temp)
        val buf = ByteArray(256)
        do {
            val numread = stream.read(buf, offset, buf.size)
            if (numread <= 0) break
            out.write(buf, offset, numread)
        } while (true)
        try {
            stream.close()
            out.close()
        } catch (ex: IOException) {
            //  Log.e(TAG, "error: " + ex.getMessage(), ex);
        }

        return tempPath
    }

    fun getVideoPath(): String {
        return tempFilePath
    }

    private fun clearCache(){
        temp?.delete()
    }

    fun close() {
        println("Closing stream over http")
        try {
            copyExecutor.shutdown()
            clearCache()
//            client.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}