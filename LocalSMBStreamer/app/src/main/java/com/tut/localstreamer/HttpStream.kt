package com.tut.localstreamer

import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.mssmb2.SMBApiException
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import java.lang.NumberFormatException
import java.net.Socket
import com.hierynomus.smbj.share.File
import java.io.*
import java.lang.Exception
import java.net.ServerSocket
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.Executors


//class HttpStream(fileIStream: File, forceMimeType: String?) {
class HttpStream(val host: String, val auth: AuthenticationContext, val sharedPath: String, val currentPath: String, forceMimeType: String?) {

    val client = SMBClient()

//    val file = fileIStream
    private val fileMimeType: String? = forceMimeType

    var serverSocket: ServerSocket? = null
    var mainThread: Thread? = null
    var isPlaying: Boolean = false

    init {

        val file = openSharedFile()

        serverSocket = ServerSocket(6000)
        mainThread = Thread {
            try {
                println("Server Listening.."+ serverSocket!!.localSocketAddress+":"+ serverSocket!!.localPort)
                while (true) {
                    val accept: Socket = serverSocket!!.accept()
                    if (accept.isConnected){
                        println("socket connected"+accept.localAddress+":"+accept.port)
                    }
                    HttpSession(file, accept).run()
                    isPlaying = true
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        mainThread!!.name = "Stream over HTTP"
        mainThread!!.isDaemon = true
        mainThread!!.start()
    }

    private fun openSharedFile(): File? {
        client.connect(host).use { connection ->
            try{
                val session = connection.authenticate(auth)
                val share = session.connectShare(sharedPath)
                if(share is DiskShare) {
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
        }
        return null
    }

    fun getUri(filePath: String?): Uri? {
        val port: Int = serverSocket!!.localPort
        var url = "http://localhost:$port"
        if (filePath != null) url += '/' + URLEncoder.encode(filePath)
        println("URI Generated: "+url)
        return Uri.parse(url)
    }

    fun close() {
        println("Closing stream over http")
        try {
            serverSocket!!.close()
            mainThread!!.join()
            client.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private class HttpSession(private val file: File?, s: Socket) : Runnable {
        private var canSeek = false
        private val socket: Socket = s

        val HTTP_BADREQUEST = "400 Bad Request"
        val HTTP_416 = "416 Range not satisfiable"
        val HTTP_INTERNALERROR = "500 Internal Server Error"

        override fun run() {
            try {
                println("Starting Session")
                handleResponse2(socket)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                /*if (file != null) {
                    try {
                        file.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }*/
            }
        }

        fun handleResponse2(socket: Socket){
            try{
                val status = ""
                val sendCount = 0
                val header = generateHeader(file!!.inputStream, null, status, sendCount)
                val out = socket.getOutputStream()
                val outputStreamWriter = OutputStreamWriter(out)
                run {
                    val retLine = "HTTP/1.0 $status \r\n"
                    outputStreamWriter.write(retLine)
                }
                if ("mimeType" != null) {
                    val mT = "Content-Type: video/*\r\n"
                    outputStreamWriter.write(mT)
                }
                if (header != null) {
                    val e: Enumeration<*> = header.keys()
                    while (e.hasMoreElements()) {
                        val key = e.nextElement() as String
                        val value = header.getProperty(key)
                        val l = "$key: $value\r\n"
                        //               if(debug) BrowserUtils.LOGRUN(l);
                        println("header: $l")
                        outputStreamWriter.write(l)
                    }
                }
                outputStreamWriter.flush()

                file.read(out)
                out.flush()
                out.close()
            } catch (e: IOException) {
                println(e.message)
            } finally {
                try {
                    socket.close()
                } catch (t: Throwable) {
                }
            }
            if (file != null) {
                file.read(socket.getOutputStream())
            }
//            copyStream(istream, socket.getOutputStream(), ByteArray(8192), 8192)
        }

        private fun getClientRequest(inS: InputStream, buf: ByteArray): Properties?{
            println("Read Client Request")
            val rlen: Int = inS.read(buf, 0, buf.size)
            if (rlen <= 0) return null

            // Create a BufferedReader for parsing the header.
            val hbis = ByteArrayInputStream(buf, 0, rlen)
            val hin = BufferedReader(InputStreamReader(hbis))
            val pre = Properties()

            // Decode the header into params and header java properties
            if (!decodeHeader(socket, hin, pre)) return null
            println("Client Request: $pre")
            return pre
        }

        private fun generateHeader(clientIStream:InputStream, range: String?, status: String, sendCount: Int) : Properties? {
            var headers = Properties()
            val eof = file!!.fileInformation.standardInformation.endOfFile.toInt()
            if (eof != -1) headers.put(
                "Content-Length",
                java.lang.String.valueOf(eof)
            )
            headers.put("Accept-Ranges", if (canSeek) "bytes" else "none")
//            if (range == null || !canSeek) {
                status.replace(status, "200 OK")
                sendCount.plus(eof)
//                sendCount.plus(eof.minus(sendCount))
//            } else {
//                if (!range.startsWith("bytes=")) {
//                    sendError(socket, "416 Range not satisfiable", null)
//                    return null
//                }
//                println(range)
//                range.replace(range, range.substring(6))
//                var startFrom: Long = 0
//                var endAt: Long = -1
//                val minus = range.indexOf('-')
//                if (minus > 0) {
//                    try {
//                        val startR = range.substring(0, minus)
//                        startFrom = startR.toLong()
//                        val endR = range.substring(minus + 1)
//                        endAt = endR.toLong()
//                    } catch (nfe: NumberFormatException) {
//                    }
//                }
//                if (startFrom >= eof) {
//                    sendError(socket, "416 Range not satisfiable", null)
//                    clientIStream.close()
//                    return null
//                }
//                if (endAt < 0) endAt = eof - 1.toLong()
//                sendCount.plus((endAt - startFrom + 1).toInt())
//                if (sendCount < 0) sendCount.minus(sendCount)
//                status.replace(status,"206 Partial Content")
//                (file as RandomAccessInputStream).seek(startFrom)
            headers["Content-Length"] = "" + sendCount
//                val rangeSpec = "bytes " + startFrom + "-" + endAt + "/" + file.available()
//                headers.put("Content-Range", rangeSpec)
//            }
            return headers
        }

        private fun handleResponse(socket: Socket) {
            try {
                val clientIStream: InputStream = socket.getInputStream() ?: return
                val buf = ByteArray(8192)
                val pre = getClientRequest(clientIStream, buf)?:return
                var range: String? = pre.getProperty("range")

                if (file != null) {
                    var sendCount = 0
                    val status = ""
                    val headers = generateHeader(clientIStream, range, status, sendCount) ?: return
                    sendResponse(socket, status, "video/mp4", headers, file.inputStream, sendCount, buf, null)
                }
                clientIStream.close()
                println("Http stream finished")
            } catch (ioe: IOException) {
                ioe.printStackTrace()
                try {
                    sendError(
                        socket,
                        HTTP_INTERNALERROR,
                        "SERVER INTERNAL ERROR: IOException: " + ioe.message
                    )
                } catch (t: Throwable) {
                }
            } catch (ie: InterruptedException) {
                // thrown by sendError, ignore and exit the thread
                ie.printStackTrace()
            }
        }

        @Throws(InterruptedException::class)
        private fun decodeHeader(socket: Socket, `in`: BufferedReader, pre: Properties): Boolean {
            try {
                // Read the request line
                val inLine: String = `in`.readLine() ?: return false
                val st = StringTokenizer(inLine)
                if (!st.hasMoreTokens()) sendError(socket, HTTP_BADREQUEST, "Syntax error")
                val method: String = st.nextToken()
                if (method != "GET") return false
                if (!st.hasMoreTokens()) sendError(socket, HTTP_BADREQUEST, "Missing URI")
                while (true) {
                    val line: String = `in`.readLine() ?: break
                    //            if(debug && line.length()>0) BrowserUtils.LOGRUN(line);
                    val p = line.indexOf(':')
                    if (p < 0) continue
                    val atr = line.substring(0, p).trim { it <= ' ' }.lowercase(Locale.getDefault())
                    val `val` = line.substring(p + 1).trim { it <= ' ' }
                    pre.put(atr, `val`)
                }
            } catch (ioe: IOException) {
                sendError(
                    socket,
                    HTTP_INTERNALERROR,
                    "SERVER INTERNAL ERROR: IOException: " + ioe.message
                )
            }
            return true
        }

//        init {
//            val t = Thread(this, "Http response")
//            t.isDaemon = true
//            t.start()
//        }

        private fun sendError(socket: Socket, status: String, msg: String?) {
            sendResponse(socket, status, "text/plain", null, null, 0, null, msg)
            throw InterruptedException()
        }

        @Throws(IOException::class)
        private fun copyStream(
            `in`: InputStream,
            out: OutputStream,
            tmpBuf: ByteArray,
            maxSize: Long
        ) {
            while (`in`.available() > -1){
                val byte = `in`.read()
                if(byte > -1){
//                pass it to http output stream
//                    print("${byte.toChar()} ")
//                    out.write(byte)
                    tmpBuf.plus(byte.toByte())
                }else {
                    break
                }
            }
            out.write(tmpBuf)
            out.flush()
            /*var mSize = maxSize
            while (maxSize > 0) {
                var count = Math.min(mSize, tmpBuf.size.toLong()).toInt()
                count = `in`.read(tmpBuf, 0, count)
                if (count < 0) break
                out.write(tmpBuf, 0, count)
                mSize -= count.toLong()
            }*/
        }

        private fun sendResponse(
            socket: Socket,
            status: String,
            mimeType: String?,
            header: Properties?,
            isInput: InputStream?,
            sendCount: Int,
            buf: ByteArray?,
            errMsg: String?
        ) {
            try {
                val out = socket.getOutputStream()
                val outputStreamWriter = OutputStreamWriter(out)
                run {
                    val retLine = "HTTP/1.0 $status \r\n"
                    outputStreamWriter.write(retLine)
                }
                if (mimeType != null) {
                    val mT = "Content-Type: $mimeType\r\n"
                    outputStreamWriter.write(mT)
                }
                if (header != null) {
                    val e: Enumeration<*> = header.keys()
                    while (e.hasMoreElements()) {
                        val key = e.nextElement() as String
                        val value = header.getProperty(key)
                        val l = "$key: $value\r\n"
                        //               if(debug) BrowserUtils.LOGRUN(l);
                        println("header: $l")
                        outputStreamWriter.write(l)
                    }
                }
                outputStreamWriter.flush()
                if (isInput != null) copyStream(
                    isInput,
                    out,
                    (buf)?:ByteArray(8192),
                    sendCount.toLong()
                ) else if (errMsg != null) {
                    outputStreamWriter.write(errMsg)
                    outputStreamWriter.flush()
                }
                out.flush()
                out.close()
            } catch (e: IOException) {
                println(e.message)
            } finally {
                try {
                    socket.close()
                } catch (t: Throwable) {
                }
            }
        }

    }

    abstract class RandomAccessInputStream : InputStream() {
        /**
         * @return total length of stream (file)
         */
        abstract fun length(): Long

        /**
         * Seek within stream for next read-ing.
         */
        @Throws(IOException::class)
        abstract fun seek(offset: Long)

        @Throws(IOException::class)
        override fun read(): Int {
            val b = ByteArray(1)
            read(b)
            return b[0].toInt()
        }
    }
}