package com.tut.downloader

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.net.URI

class MainActivity : AppCompatActivity() {

    private val fileLinks = listOf(
        "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
        "http://www.wright.edu/~david.wilson/eng3000/samplereport.pdf",
        "http://www.africau.edu/images/default/sample.pdf"
    )

    private val perms = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        val fileNames = mutableListOf<String>()
        for (link in fileLinks) {
            fileNames.add(getFileName(link))
        }
        println(fileNames)
        val listView = findViewById<ListView>(R.id.lv_download_files)
        listView.adapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, fileNames)
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, pos, _ ->
            val selectedFile = parent.getItemAtPosition(pos).toString()
            downloadFile(selectedFile, fileLinks[pos])
        }
    }

    @SuppressLint("Range")
    private fun downloadFile(fileName: String, selectedURL: String) {
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(selectedURL)

        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                +"/Krishi/$fileName")
        if(file.exists()){
            Toast.makeText(this,"Report already Exists!",Toast.LENGTH_LONG).show()
            return
        }

        val request = DownloadManager.Request(uri)
        request.setTitle(fileName.uppercase())
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Krishi/$fileName")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val refId = manager.enqueue(request)

        Toast.makeText(this, "Download Started", Toast.LENGTH_SHORT)
            .show()
        downloadStatus(manager, refId)

        /*URL(selectedURL).openStream().use { input ->
            FileOutputStream(File(Environment.getExternalStorageDirectory().toString()+"/Krishi/")).use { output ->
                input.copyTo(output)
            }
        }*/
    }

    private fun getFileName(url: String): String {
        val uri = URI.create(url)
        return if (uri.host.isNotEmpty() and url.endsWith(uri.host)) {
            "Soil Health Report.pdf"
        } else {
            url.substring(url.lastIndexOf("/") + 1)
        }
    }

    @SuppressLint("Range")
    private fun downloadStatus(manager: DownloadManager, id: Long){
        var finishDownload = false
        while (!finishDownload) {
            val cursor: Cursor = manager.query(DownloadManager.Query().setFilterById(id))
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_FAILED -> {
                        finishDownload = true
                        Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                    DownloadManager.STATUS_PAUSED -> {
                        Toast.makeText(this,"Download Paused", Toast.LENGTH_SHORT).show()
                    }
                    /*DownloadManager.STATUS_RUNNING -> {
                        val total: Long =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (total >= 0) {
                            val downloaded: Long =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            val progress = (downloaded * 100L / total)
                            println("Progress: $progress")
                        }
                    }*/
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        finishDownload = true
                        Toast.makeText(this, "Download Completed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        for (permission in perms) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(perms, 0)
            }
        }
    }
}