package com.tut.locationquery

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.tut.locationquery.adaptors.LocCardAdaptor
import com.tut.locationquery.container.CardData
import com.tut.locationquery.ui.main.SectionsPagerAdapter
import com.tut.locationquery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val locations = listOf("Bajghera Rd, near Gramin Bank, Ward No.7, Rajendra Park, Sector 105, Gurugram, Haryana 122001",
        "379, Sector 5, Gurugram, Haryana 122022",
        "F292+36F, Block C, Surya Vihar, Sector 9, Gurugram, Haryana 122006",
        "1031/1, Old Railway Rd, Sector 5, Gurugram, Haryana 122001")
    val myLoc = "Sector 4, Gurugram, Haryana 122001"
    lateinit var listDataAdaptor: RecyclerView.Adapter<LocCardAdaptor.ViewHolder>

    val pin_locations = ArrayList<CardData>()

    inner class CardClickListener : LocCardAdaptor.OnItemClickListener {
        override fun onItemClick(data: CardData, pos: Int) {
            val dialog = Dialog(this@MainActivity)
            dialog.window?.setBackgroundDrawableResource(R.drawable.round_bg)
            dialog.apply {
                setContentView(R.layout.layout_dialog_item_detail)
                findViewById<TextView>(R.id.tv_loc_name).text = "Location Name"
                findViewById<TextView>(R.id.tv_loc_address).text = data.address
                findViewById<TextView>(R.id.tv_operator_name).text = data.pinData?.featureName
                findViewById<TextView>(R.id.tv_contact_num).text = data.pinData?.phone
                findViewById<TextView>(R.id.tv_timings).text = data.pinData?.thoroughfare
                findViewById<ImageView>(R.id.im_close_dialog).setOnClickListener {
                    cancel()
                }
                findViewById<Button>(R.id.btn_request_test).setOnClickListener{
                    Toast.makeText(this@MainActivity,
                        "Make a request on the given no.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()

            /* AlertDialog
            val dialogBuilder = AlertDialog.Builder(this@MapsActivity)
            val layoutView = layoutInflater.inflate(R.layout.layout_dialog_item_detail, null)
            val dialog = dialogBuilder.apply {
                setView(
                    layoutView
                )
                layoutView.findViewById<TextView>(R.id.tv_loc_name).text = "LocaName"
                layoutView.findViewById<TextView>(R.id.tv_loc_address).text = data.address
                layoutView.findViewById<TextView>(R.id.tv_operator_name).text = data.pinData?.featureName
                layoutView.findViewById<TextView>(R.id.tv_contact_num).text = data.pinData?.phone
                layoutView.findViewById<TextView>(R.id.tv_timings).text = data.pinData?.thoroughfare
                layoutView.findViewById<Button>(R.id.btn_request_test).setOnClickListener{
                    Toast.makeText(this@MapsActivity,
                        "Make a request on the given no.",
                        Toast.LENGTH_SHORT).show()
                }
            }.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.round_bg)
            layoutView.findViewById<ImageView>(R.id.im_close_dialog).setOnClickListener {
                dialog.cancel()
            }
            dialog.show()*/
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabs: TabLayout = binding.tabs
        val viewPager: ViewPager = binding.viewPager
        tabs.setupWithViewPager(viewPager)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = sectionsPagerAdapter

        listDataAdaptor = LocCardAdaptor(pin_locations).apply {
            itemClickListener = CardClickListener()
        }
    }
}